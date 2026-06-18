package com.gymcore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WorkoutExerciseInput(
		@NotNull
		Long exerciseId,

		@Min(1)
		int sets,

		@Min(1)
		int reps,

		@Min(0)
		int restSeconds,

		Integer sortOrder,

		List<Integer> repsPerSet,

		List<Double> weightsKg
) {
}
