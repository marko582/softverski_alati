package com.gymcore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonalRecordCreateRequest(
		@NotBlank
		@Size(max = 120)
		String title,

		Double weightKg,

		Integer reps,

		@Size(max = 500)
		String notes
) {
}
