package com.gymcore.dto;

public record AuthResponse(
		String accessToken,
		String tokenType,
		long expiresIn,
		String email,
		String username,
		long userId
) {
}
