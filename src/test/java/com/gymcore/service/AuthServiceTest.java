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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;

	@Mock
	private HttpServletResponse response;

	@Mock
	private HttpServletRequest request;

	@InjectMocks
	private AuthService authService;

	private GymcoreProperties properties;
	private User sampleUser;

	@BeforeEach
	void setUp() {
		properties = new GymcoreProperties();
		properties.getJwt().setAccessExpirationMs(3_600_000L);
		properties.getJwt().setRefreshExpirationMs(604_800_000L);
		properties.getCookie().setRefreshName("refreshToken");
		properties.getCookie().setSecure(false);
		authService = new AuthService(
				userRepository,
				passwordEncoder,
				authenticationManager,
				jwtService,
				properties);

		sampleUser = new User();
		sampleUser.setId(1L);
		sampleUser.setEmail("marko@example.com");
		sampleUser.setDisplayName("marko582");
		sampleUser.setPasswordHash("encoded");
		sampleUser.setActive(true);
	}

	@Test
	@DisplayName("Should register user, persist account, and return auth response with cookie")
	void register_ValidInput_SavesUserAndReturnsAuthResponse() {
		RegisterRequest registerRequest = new RegisterRequest("marko582", "marko@example.com", "Password1");
		when(userRepository.existsByEmail("marko@example.com")).thenReturn(false);
		when(userRepository.existsByDisplayName("marko582")).thenReturn(false);
		when(passwordEncoder.encode("Password1")).thenReturn("encoded");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User saved = invocation.getArgument(0);
			saved.setId(1L);
			return saved;
		});
		when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");

		AuthResponse result = authService.register(registerRequest, response);

		assertNotNull(result);
		assertEquals("access-token", result.accessToken());
		assertEquals("marko@example.com", result.email());
		verify(userRepository, atLeastOnce()).save(any(User.class));
		verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
	}

	@Test
	@DisplayName("Should throw when registering with duplicate email")
	void register_DuplicateEmail_ThrowsIllegalArgumentException() {
		RegisterRequest registerRequest = new RegisterRequest("marko582", "marko@example.com", "Password1");
		when(userRepository.existsByEmail("marko@example.com")).thenReturn(true);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> authService.register(registerRequest, response));

		assertEquals("Email already in use", exception.getMessage());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("Should authenticate user and issue session on login")
	void login_ValidCredentials_ReturnsAuthResponse() {
		LoginRequest loginRequest = new LoginRequest("marko@example.com", "Password1");
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(sampleUser);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(jwtService.generateAccessToken(sampleUser)).thenReturn("access-token");
		when(userRepository.save(sampleUser)).thenReturn(sampleUser);

		AuthResponse result = authService.login(loginRequest, response);

		assertEquals("access-token", result.accessToken());
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
	}

	@Test
	@DisplayName("Should refresh session when valid refresh cookie is provided")
	void refresh_ValidCookie_ReturnsNewAuthResponse() {
		String rawToken = "valid-refresh-token";
		String hash = TokenHasher.sha256Hex(rawToken);
		sampleUser.setRefreshTokenHash(hash);
		sampleUser.setRefreshTokenExpiresAt(Instant.now().plusSeconds(3600));
		Cookie cookie = new Cookie("refreshToken", rawToken);
		when(request.getCookies()).thenReturn(new Cookie[] { cookie });
		when(userRepository.findByRefreshTokenHash(hash)).thenReturn(Optional.of(sampleUser));
		when(jwtService.generateAccessToken(sampleUser)).thenReturn("new-access-token");
		when(userRepository.save(sampleUser)).thenReturn(sampleUser);

		AuthResponse result = authService.refresh(request, response);

		assertEquals("new-access-token", result.accessToken());
		verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
	}

	@Test
	@DisplayName("Should throw when refresh cookie is missing")
	void refresh_MissingCookie_ThrowsUnauthorized() {
		when(request.getCookies()).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> authService.refresh(request, response));

		assertEquals(401, exception.getStatusCode().value());
		assertTrue(exception.getReason().contains("Missing refresh token"));
	}

	@Test
	@DisplayName("Should clear refresh token and cookie on logout")
	void logout_ValidCookie_ClearsSession() {
		String rawToken = "logout-token";
		String hash = TokenHasher.sha256Hex(rawToken);
		sampleUser.setRefreshTokenHash(hash);
		Cookie cookie = new Cookie("refreshToken", rawToken);
		when(request.getCookies()).thenReturn(new Cookie[] { cookie });
		when(userRepository.findByRefreshTokenHash(hash)).thenReturn(Optional.of(sampleUser));

		authService.logout(request, response);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		assertNull(userCaptor.getValue().getRefreshTokenHash());
		verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), contains("Max-Age=0"));
	}

	@Test
	@DisplayName("Should reissue session tokens for an existing user")
	void reissueSession_ValidUser_ReturnsNewAuthResponse() {
		when(jwtService.generateAccessToken(sampleUser)).thenReturn("reissued-access");
		when(userRepository.save(sampleUser)).thenReturn(sampleUser);

		AuthResponse result = authService.reissueSession(sampleUser, response);

		assertEquals("reissued-access", result.accessToken());
		assertEquals("marko@example.com", result.email());
		verify(userRepository).save(sampleUser);
		verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
	}
}
