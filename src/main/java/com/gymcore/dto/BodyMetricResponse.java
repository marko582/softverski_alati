package com.gymcore.dto;

import java.time.Instant;

public record BodyMetricResponse(
		long id,
		Instant measuredAt,
		Double weightKg,
		Double bodyFatPct,
		Double chestCm,
		Double waistCm,
		Double hipsCm,
		Double armCm,
		String notes
) {
}
