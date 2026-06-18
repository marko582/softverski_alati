package com.gymcore.dto;

import java.time.Instant;
import java.util.List;

public record WorkoutDetailResponse(
		long id,
		String name,
		String description,
		Instant createdAt,
		List<WorkoutExerciseResponse> exercises
) {
}
