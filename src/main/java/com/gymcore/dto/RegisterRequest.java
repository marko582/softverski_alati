package com.gymcore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank
		@Size(min = 3, max = 50)
		@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must be alphanumeric (underscore allowed)")
		String username,

		@NotBlank
		@Email
		String email,

		@NotBlank
		@Size(min = 8, message = "Password must be at least 8 characters")
		@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).+$", message = "Password must contain at least one uppercase letter and one number")
		String password
) {
}
