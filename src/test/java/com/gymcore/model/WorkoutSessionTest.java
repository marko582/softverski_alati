package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkoutSessionTest {

	private User validUser() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		return user;
	}

	private WorkoutSession validSession() {
		WorkoutSession session = new WorkoutSession();
		session.setUser(validUser());
		session.setTitle("Morning lift");
		return session;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		WorkoutSession session = new WorkoutSession();
		session.setId(50L);

		assertEquals(50L, session.getId());
	}

	@Test
	@DisplayName("Should store user through getter after setter")
	void setUser_getUser_ReturnsSameValue() {
		User user = validUser();
		WorkoutSession session = new WorkoutSession();
		session.setUser(user);

		assertEquals(user, session.getUser());
	}

	@Test
	@DisplayName("Should store workout through getter after setter")
	void setWorkout_getWorkout_ReturnsSameValue() {
		Workout workout = new Workout();
		WorkoutSession session = new WorkoutSession();
		session.setWorkout(workout);

		assertEquals(workout, session.getWorkout());
	}

	@Test
	@DisplayName("Should store title through getter after setter")
	void setTitle_getTitle_ReturnsSameValue() {
		WorkoutSession session = new WorkoutSession();
		session.setTitle("Morning lift");

		assertEquals("Morning lift", session.getTitle());
	}

	@Test
	@DisplayName("Should store completedAt through getter after setter")
	void setCompletedAt_getCompletedAt_ReturnsSameValue() {
		WorkoutSession session = new WorkoutSession();
		session.setCompletedAt(null);

		assertNull(session.getCompletedAt());
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

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validSession());
	}

	@Test
	@DisplayName("Should reject null user reference")
	void validate_UserNull_HasViolation() {
		WorkoutSession session = validSession();
		session.setUser(null);

		assertViolationOn(session, "user");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank session title")
	void validate_TitleBlank_HasViolation(String title) {
		WorkoutSession session = validSession();
		session.setTitle(title);

		assertViolationOn(session, "title");
	}

	@Test
	@DisplayName("Should reject session title longer than 255 characters")
	void validate_TitleTooLong_HasViolation() {
		WorkoutSession session = validSession();
		session.setTitle("a".repeat(256));

		assertViolationOn(session, "title");
	}
}
