package com.gymcore.dto;

import java.util.List;

/**
 * Partial update: omit a field to leave it unchanged.
 * {@code weightsKg} must have exactly {@code setsPlanned} entries (use JSON {@code null} or omit values per index).
 * Per index: {@code 0} or {@code null} clears that set's weight.
 * {@code repsPerSet} must have exactly {@code setsPlanned} entries; each rep count 1–9999.
 */
public record SessionItemPatchRequest(
		Integer setsDone,
		List<Integer> repsPerSet,
		List<Double> weightsKg
) {
}
