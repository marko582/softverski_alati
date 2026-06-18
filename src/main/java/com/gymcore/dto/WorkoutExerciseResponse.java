package com.gymcore.dto;

import java.util.List;

public record WorkoutExerciseResponse(
		long id,
		long exerciseId,
		int sets,
		int reps,
		int restSeconds,
		int sortOrder,
		List<Integer> repsPerSet,
		List<Double> weightsKg
) {
}
