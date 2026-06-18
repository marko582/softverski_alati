package com.gymcore.dto;

import java.time.Instant;

public record SessionSummaryResponse(
		long id,
		Long workoutId,
		String workoutName,
		Instant startedAt,
		Instant completedAt,
		boolean active
) {
}
