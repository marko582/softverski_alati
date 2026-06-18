package com.gymcore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SessionItemDraftRequest(
		@NotNull Long exerciseId,
		@Min(1) @Max(99) int sets,
		@Min(1) @Max(9999) int reps,
		List<Integer> repsPerSet,
		List<Double> weightsKg
) {
}
