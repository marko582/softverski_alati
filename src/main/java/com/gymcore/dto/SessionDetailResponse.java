package com.gymcore.dto;

import java.time.Instant;
import java.util.List;

public record SessionDetailResponse(
		long id,
		Long workoutId,
		String workoutName,
		Instant startedAt,
		Instant completedAt,
		boolean active,
		List<SessionItemResponse> items
) {
}
