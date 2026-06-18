package com.gymcore.dto;

import java.time.Instant;

public record WorkoutSummaryResponse(
		long id,
		String name,
		String description,
		Instant createdAt,
		int exerciseCount
) {
}
