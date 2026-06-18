package com.gymcore.dto;

import java.util.List;

public record SessionItemResponse(
		long id,
		long exerciseId,
		int sortOrder,
		int setsPlanned,
		int repsPlanned,
		List<Integer> repsPerSet,
		int setsDone,
		List<Double> weightsKg
) {
}
