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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WorkoutTest {

	private User validUser() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		return user;
	}

	private Workout validWorkout() {
		Workout workout = new Workout();
		workout.setUser(validUser());
		workout.setName("Push Day");
		return workout;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		Workout workout = new Workout();
		workout.setId(10L);

		assertEquals(10L, workout.getId());
	}

	@Test
	@DisplayName("Should store user through getter after setter")
	void setUser_getUser_ReturnsSameValue() {
		User user = validUser();
		Workout workout = new Workout();
		workout.setUser(user);

		assertEquals(user, workout.getUser());
	}

	@Test
	@DisplayName("Should store name through getter after setter")
	void setName_getName_ReturnsSameValue() {
		Workout workout = new Workout();
		workout.setName("Push Day");

		assertEquals("Push Day", workout.getName());
	}

	@Test
	@DisplayName("Should store description through getter after setter")
	void setDescription_getDescription_ReturnsSameValue() {
		Workout workout = new Workout();
		workout.setDescription("Chest");

		assertEquals("Chest", workout.getDescription());
	}

	@Test
	@DisplayName("Should store deleted flag through getter after setter")
	void setDeleted_isDeleted_ReturnsSameValue() {
		Workout workout = new Workout();
		workout.setDeleted(false);

		assertFalse(workout.isDeleted());
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

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validWorkout());
	}

	@Test
	@DisplayName("Should reject null user reference")
	void validate_UserNull_HasViolation() {
		Workout workout = validWorkout();
		workout.setUser(null);

		assertViolationOn(workout, "user");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank workout name")
	void validate_NameBlank_HasViolation(String name) {
		Workout workout = validWorkout();
		workout.setName(name);

		assertViolationOn(workout, "name");
	}

	@Test
	@DisplayName("Should reject workout name longer than 100 characters")
	void validate_NameTooLong_HasViolation() {
		Workout workout = validWorkout();
		workout.setName("a".repeat(101));

		assertViolationOn(workout, "name");
	}
}
