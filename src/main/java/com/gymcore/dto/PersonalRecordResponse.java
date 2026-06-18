package com.gymcore.dto;

import java.time.Instant;

public record PersonalRecordResponse(
		long id,
		String title,
		Double weightKg,
		Integer reps,
		Instant recordedAt,
		String notes
) {
}
