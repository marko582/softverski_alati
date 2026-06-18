package com.gymcore.dto;

import jakarta.validation.constraints.Size;

public record BodyMetricCreateRequest(
		Double weightKg,
		Double bodyFatPct,
		Double chestCm,
		Double waistCm,
		Double hipsCm,
		Double armCm,

		@Size(max = 500)
		String notes
) {
}
