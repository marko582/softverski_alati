package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		User user = new User();
		user.setId(1L);
		Workout workout = new Workout();
		workout.setId(10L);
		workout.setUser(user);
		workout.setName("Push Day");
		workout.setDescription("Chest");
		workout.setDeleted(false);

		assertAll(
				() -> assertEquals(10L, workout.getId()),
				() -> assertEquals(user, workout.getUser()),
				() -> assertEquals("Push Day", workout.getName()),
				() -> assertFalse(workout.isDeleted())
		);
	}

	@Test
	@DisplayName("Should set createdAt on pre-persist when missing")
	void onCreate_SetsCreatedAt() {
		Workout workout = new Workout();
		workout.onCreate();

		assertNotNull(workout.getCreatedAt());
	}

	@Test
	@DisplayName("Should keep existing createdAt on pre-persist")
	void onCreate_PreservesExistingCreatedAt() {
		Instant existing = Instant.parse("2025-01-01T00:00:00Z");
		Workout workout = new Workout();
		workout.setCreatedAt(existing);
		workout.onCreate();

		assertEquals(existing, workout.getCreatedAt());
	}
}
