package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutSessionItemTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		WorkoutSession session = new WorkoutSession();
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setId(500L);
		item.setSession(session);
		item.setExerciseId(5L);
		item.setSortOrder(0);
		item.setSetsPlanned(3);
		item.setRepsPlanned(10);
		item.setSetsDone(1);

		assertAll(
				() -> assertEquals(500L, item.getId()),
				() -> assertEquals(session, item.getSession()),
				() -> assertEquals(3, item.getSetsPlanned()),
				() -> assertEquals(1, item.getSetsDone())
		);
	}

	@Test
	@DisplayName("Should default JSON list setters to empty lists for null input")
	void setSetRepsPlanned_NullInput_UsesEmptyList() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSetRepsPlanned(null);

		assertNotNull(item.getSetRepsPlanned());
		assertTrue(item.getSetRepsPlanned().isEmpty());
	}

	@Test
	@DisplayName("Should store per-set planned weights")
	void setSetWeightsKg_StoresValues() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSetWeightsKg(new java.util.ArrayList<>());
		item.getSetWeightsKg().add(BigDecimal.valueOf(50));
		item.getSetWeightsKg().add(null);

		assertEquals(2, item.getSetWeightsKg().size());
	}
}
