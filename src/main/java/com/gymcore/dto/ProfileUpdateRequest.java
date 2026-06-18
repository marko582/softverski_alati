package com.gymcore.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
		@Size(min = 3, max = 50)
		@Pattern(regexp = "^$|^[a-zA-Z0-9_]+$", message = "Username must be alphanumeric (underscore allowed)")
		String username,

		@Size(max = 255)
		String email
) {
}
