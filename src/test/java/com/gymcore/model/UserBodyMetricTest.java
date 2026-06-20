package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserBodyMetricTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		User user = new User();
		Instant measuredAt = Instant.parse("2025-04-01T07:00:00Z");
		UserBodyMetric metric = new UserBodyMetric();
		metric.setId(30L);
		metric.setUser(user);
		metric.setMeasuredAt(measuredAt);
		metric.setWeightKg(BigDecimal.valueOf(82.5));
		metric.setBodyFatPct(BigDecimal.valueOf(14.2));
		metric.setChestCm(BigDecimal.valueOf(100));
		metric.setNotes("Fasted");

		assertAll(
				() -> assertEquals(30L, metric.getId()),
				() -> assertEquals(user, metric.getUser()),
				() -> assertEquals(measuredAt, metric.getMeasuredAt()),
				() -> assertEquals(82.5, metric.getWeightKg().doubleValue())
		);
	}
}
