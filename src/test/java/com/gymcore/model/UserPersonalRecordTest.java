package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserPersonalRecordTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		User user = new User();
		Instant recordedAt = Instant.parse("2025-03-01T10:00:00Z");
		UserPersonalRecord record = new UserPersonalRecord();
		record.setId(20L);
		record.setUser(user);
		record.setTitle("Deadlift");
		record.setWeightKg(BigDecimal.valueOf(180));
		record.setReps(3);
		record.setRecordedAt(recordedAt);
		record.setNotes("Comp PR");

		assertAll(
				() -> assertEquals(20L, record.getId()),
				() -> assertEquals("Deadlift", record.getTitle()),
				() -> assertEquals(180, record.getWeightKg().intValue()),
				() -> assertEquals(recordedAt, record.getRecordedAt())
		);
	}
}
