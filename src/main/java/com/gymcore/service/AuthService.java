package com.gymcore.service;

import com.gymcore.config.GymcoreProperties;
import com.gymcore.dto.AuthResponse;
import com.gymcore.dto.LoginRequest;
import com.gymcore.dto.RegisterRequest;
import com.gymcore.model.User;
import com.gymcore.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

/**
 * Service managing user registration, login, token refresh, and logout.
 * Issues JWT access tokens and httpOnly refresh-token cookies.
 * @author Marko Mijailovic (marko582)
 */
@Service
public class AuthService {

	private static final SecureRandom RANDOM = new SecureRandom();

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final GymcoreProperties properties;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			GymcoreProperties properties) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.properties = properties;
	}

	/**
	 * Registers a new user and starts an authenticated session.
	 * @param request registration payload with username, email, and password.
	 * @param response HTTP response used to set the refresh-token cookie.
	 * @return auth response containing the access token and user info.
	 * @throws IllegalArgumentException if email or username is already in use.
	 */
	@Transactional
	public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
		if (userRepository.existsByEmail(request.email())) {
			throw new IllegalArgumentException("Email already in use");
		}
		if (userRepository.existsByDisplayName(request.username())) {
			throw new IllegalArgumentException("Username already in use");
		}
		User user = new User();
		user.setDisplayName(request.username());
		user.setEmail(request.email().trim().toLowerCase());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setActive(true);
		userRepository.save(user);
		return issueSession(user, response);
	}

	/**
	 * Authenticates a user and starts a new session.
	 * @param request login payload with email and password.
	 * @param response HTTP response used to set the refresh-token cookie.
	 * @return auth response containing the access token and user info.
	 */
	@Transactional
	public AuthResponse login(LoginRequest request, HttpServletResponse response) {
		var token = new UsernamePasswordAuthenticationToken(
				request.email().trim().toLowerCase(),
				request.password());
		var auth = authenticationManager.authenticate(token);
		User user = (User) auth.getPrincipal();
		return issueSession(user, response);
	}

	/**
	 * Rotates the access token using a valid refresh-token cookie.
	 * @param request HTTP request carrying the refresh-token cookie.
	 * @param response HTTP response used to set a new refresh-token cookie.
	 * @return auth response with a new access token.
	 * @throws org.springframework.web.server.ResponseStatusException if the refresh token is missing, invalid, or expired.
	 */
	@Transactional
	public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
		String raw = readRefreshCookie(request)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token"));
		String hash = TokenHasher.sha256Hex(raw);
		User user = userRepository.findByRefreshTokenHash(hash)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
		if (user.getRefreshTokenExpiresAt() == null || user.getRefreshTokenExpiresAt().isBefore(Instant.now())) {
			clearRefreshToken(user);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
		}
		return issueSession(user, response);
	}

	/**
	 * Invalidates the refresh token and clears the refresh-token cookie.
	 * @param request HTTP request carrying the refresh-token cookie.
	 * @param response HTTP response used to clear the cookie.
	 */
	@Transactional
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		readRefreshCookie(request).ifPresent(raw -> {
			String hash = TokenHasher.sha256Hex(raw);
			userRepository.findByRefreshTokenHash(hash).ifPresent(this::clearRefreshToken);
		});
		appendClearRefreshCookie(response);
	}

	/**
	 * Re-issues access and refresh tokens for an already authenticated user (e.g. after email change).
	 * @param user the user to issue a session for.
	 * @param response HTTP response used to set the refresh-token cookie.
	 * @return auth response containing new tokens.
	 */
	@Transactional
	public AuthResponse reissueSession(User user, HttpServletResponse response) {
		return issueSession(user, response);
	}

	private AuthResponse issueSession(User user, HttpServletResponse response) {
		String access = jwtService.generateAccessToken(user);
		String refreshRaw = newRefreshRaw();
		String refreshHash = TokenHasher.sha256Hex(refreshRaw);
		user.setRefreshTokenHash(refreshHash);
		user.setRefreshTokenExpiresAt(Instant.now().plusMillis(properties.getJwt().getRefreshExpirationMs()));
		userRepository.save(user);
		appendRefreshCookie(response, refreshRaw);
		long expSec = properties.getJwt().getAccessExpirationMs() / 1000;
		return new AuthResponse(access, "Bearer", expSec, user.getEmail(), user.getDisplayName(), user.getId());
	}

	private void clearRefreshToken(User user) {
		user.setRefreshTokenHash(null);
		user.setRefreshTokenExpiresAt(null);
		userRepository.save(user);
	}

	private String newRefreshRaw() {
		byte[] buf = new byte[32];
		RANDOM.nextBytes(buf);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
	}

	private void appendRefreshCookie(HttpServletResponse response, String raw) {
		long maxAgeSec = Math.max(1, properties.getJwt().getRefreshExpirationMs() / 1000);
		ResponseCookie cookie = ResponseCookie.from(properties.getCookie().getRefreshName(), raw)
				.httpOnly(true)
				.secure(properties.getCookie().isSecure())
				.path("/api/v1/auth")
				.maxAge(Duration.ofSeconds(maxAgeSec))
				.sameSite("Strict")
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	private void appendClearRefreshCookie(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from(properties.getCookie().getRefreshName(), "")
				.httpOnly(true)
				.secure(properties.getCookie().isSecure())
				.path("/api/v1/auth")
				.maxAge(0)
				.sameSite("Strict")
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	private Optional<String> readRefreshCookie(HttpServletRequest request) {
		if (request.getCookies() == null) {
			return Optional.empty();
		}
		String name = properties.getCookie().getRefreshName();
		return Arrays.stream(request.getCookies())
				.filter(c -> name.equals(c.getName()))
				.map(Cookie::getValue)
				.filter(v -> v != null && !v.isBlank())
				.findFirst();
	}
}
