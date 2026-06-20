package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutSessionTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		User user = new User();
		Workout workout = new Workout();
		WorkoutSession session = new WorkoutSession();
		session.setId(50L);
		session.setUser(user);
		session.setWorkout(workout);
		session.setTitle("Morning lift");
		session.setCompletedAt(null);

		assertAll(
				() -> assertEquals(50L, session.getId()),
				() -> assertEquals(user, session.getUser()),
				() -> assertEquals(workout, session.getWorkout()),
				() -> assertEquals("Morning lift", session.getTitle()),
				() -> assertNull(session.getCompletedAt())
		);
	}

	@Test
	@DisplayName("Should set startedAt on pre-persist when missing")
	void onCreate_SetsStartedAt() {
		WorkoutSession session = new WorkoutSession();
		session.onCreate();

		assertNotNull(session.getStartedAt());
	}

	@Test
	@DisplayName("Should keep existing startedAt on pre-persist")
	void onCreate_PreservesExistingStartedAt() {
		Instant existing = Instant.parse("2025-02-01T09:00:00Z");
		WorkoutSession session = new WorkoutSession();
		session.setStartedAt(existing);
		session.onCreate();

		assertEquals(existing, session.getStartedAt());
	}
}
