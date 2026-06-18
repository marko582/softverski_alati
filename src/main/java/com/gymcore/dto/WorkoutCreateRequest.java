package com.gymcore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record WorkoutCreateRequest(
		@NotBlank
		@Size(max = 100)
		String name,

		String description,

		@Valid
		List<WorkoutExerciseInput> exercises
) {
}
