package com.gymcore.service;

import com.gymcore.config.GymcoreProperties;
import com.gymcore.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Service for creating and validating JWT access tokens.
 * @author Marko Mijailovic (marko582)
 */
@Service
public class JwtService {

	private final GymcoreProperties properties;
	private SecretKey key;

	public JwtService(GymcoreProperties properties) {
		this.properties = properties;
	}

	private SecretKey getKey() {
		if (key == null) {
			String secret = properties.getJwt().getSecret();
			byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
			if (bytes.length < 32) {
				throw new IllegalStateException("gymcore.jwt.secret must be at least 32 bytes");
			}
			key = Keys.hmacShaKeyFor(bytes);
		}
		return key;
	}

	/**
	 * Generates a signed JWT access token for the given user.
	 * @param user the authenticated user.
	 * @return compact JWT string.
	 */
	public String generateAccessToken(User user) {
		long exp = properties.getJwt().getAccessExpirationMs();
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(exp)))
				.claim("uid", user.getId())
				.signWith(getKey())
				.compact();
	}

	/**
	 * Extracts the email (subject) claim from a JWT.
	 * @param token the JWT to parse.
	 * @return email address stored in the token subject.
	 */
	public String extractEmail(String token) {
		return parseClaims(token).getSubject();
	}

	/**
	 * Checks whether the token belongs to the user and has not expired.
	 * @param token the JWT to validate.
	 * @param user the expected user details.
	 * @return {@code true} if the token is valid for the user.
	 */
	public boolean isTokenValid(String token, UserDetails user) {
		String subject = extractEmail(token);
		return subject != null && subject.equalsIgnoreCase(user.getUsername()) && !isExpired(token);
	}

	private boolean isExpired(String token) {
		return parseClaims(token).getExpiration().before(new Date());
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
