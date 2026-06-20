package com.gymcore.service;

import com.gymcore.config.GymcoreProperties;
import com.gymcore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	private JwtService jwtService;
	private User sampleUser;

	@BeforeEach
	void setUp() {
		GymcoreProperties properties = new GymcoreProperties();
		properties.getJwt().setSecret("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
		properties.getJwt().setAccessExpirationMs(3_600_000L);
		jwtService = new JwtService(properties);

		sampleUser = new User();
		sampleUser.setId(1L);
		sampleUser.setEmail("marko@example.com");
		sampleUser.setDisplayName("marko582");
		sampleUser.setPasswordHash("hash");
		sampleUser.setActive(true);
	}

	@Test
	@DisplayName("Should generate token with extractable email for valid user")
	void generateAccessToken_ValidUser_ReturnsParsableToken() {
		String token = jwtService.generateAccessToken(sampleUser);

		assertNotNull(token);
		assertFalse(token.isBlank());
		assertEquals("marko@example.com", jwtService.extractEmail(token));
	}

	@Test
	@DisplayName("Should validate token for matching user details")
	void isTokenValid_MatchingUser_ReturnsTrue() {
		String token = jwtService.generateAccessToken(sampleUser);

		assertTrue(jwtService.isTokenValid(token, sampleUser));
	}

	@Test
	@DisplayName("Should reject token when user email does not match")
	void isTokenValid_DifferentUser_ReturnsFalse() {
		String token = jwtService.generateAccessToken(sampleUser);
		User other = new User();
		other.setEmail("other@example.com");

		assertFalse(jwtService.isTokenValid(token, other));
	}
}
