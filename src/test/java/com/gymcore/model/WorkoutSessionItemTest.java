package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutSessionItemTest {

	private WorkoutSession validSession() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		WorkoutSession session = new WorkoutSession();
		session.setUser(user);
		session.setTitle("Morning lift");
		return session;
	}

	private WorkoutSessionItem validItem() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSession(validSession());
		item.setExerciseId(5L);
		item.setSortOrder(0);
		item.setSetsPlanned(3);
		item.setRepsPlanned(10);
		item.setSetsDone(1);
		item.setSetWeightsKg(List.of(BigDecimal.valueOf(50)));
		item.setSetRepsPlanned(List.of(10));
		return item;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setId(500L);

		assertEquals(500L, item.getId());
	}

	@Test
	@DisplayName("Should store session through getter after setter")
	void setSession_getSession_ReturnsSameValue() {
		WorkoutSession session = validSession();
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSession(session);

		assertEquals(session, item.getSession());
	}

	@Test
	@DisplayName("Should store exercise id through getter after setter")
	void setExerciseId_getExerciseId_ReturnsSameValue() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setExerciseId(5L);

		assertEquals(5L, item.getExerciseId());
	}

	@Test
	@DisplayName("Should store sets planned through getter after setter")
	void setSetsPlanned_getSetsPlanned_ReturnsSameValue() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSetsPlanned(3);

		assertEquals(3, item.getSetsPlanned());
	}

	@Test
	@DisplayName("Should store reps planned through getter after setter")
	void setRepsPlanned_getRepsPlanned_ReturnsSameValue() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setRepsPlanned(10);

		assertEquals(10, item.getRepsPlanned());
	}

	@Test
	@DisplayName("Should store sets done through getter after setter")
	void setSetsDone_getSetsDone_ReturnsSameValue() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSetsDone(1);

		assertEquals(1, item.getSetsDone());
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
	@DisplayName("Should default set weights list setter to empty list for null input")
	void setSetWeightsKg_NullInput_UsesEmptyList() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSetWeightsKg(null);

		assertNotNull(item.getSetWeightsKg());
		assertTrue(item.getSetWeightsKg().isEmpty());
	}

	@Test
	@DisplayName("Should store per-set planned weights")
	void setSetWeightsKg_StoresValues() {
		WorkoutSessionItem item = new WorkoutSessionItem();
		item.setSetWeightsKg(new ArrayList<>());
		item.getSetWeightsKg().add(BigDecimal.valueOf(50));
		item.getSetWeightsKg().add(null);

		assertEquals(2, item.getSetWeightsKg().size());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validItem());
	}

	@Test
	@DisplayName("Should reject null session reference")
	void validate_SessionNull_HasViolation() {
		WorkoutSessionItem item = validItem();
		item.setSession(null);

		assertViolationOn(item, "session");
	}

	@ParameterizedTest
	@ValueSource(longs = { 0L, -1L })
	@DisplayName("Should reject non-positive exercise id")
	void validate_ExerciseIdNotPositive_HasViolation(long exerciseId) {
		WorkoutSessionItem item = validItem();
		item.setExerciseId(exerciseId);

		assertViolationOn(item, "exerciseId");
	}

	@ParameterizedTest
	@ValueSource(ints = { -1 })
	@DisplayName("Should reject negative sort order")
	void validate_SortOrderNegative_HasViolation(int sortOrder) {
		WorkoutSessionItem item = validItem();
		item.setSortOrder(sortOrder);

		assertViolationOn(item, "sortOrder");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100 })
	@DisplayName("Should reject sets planned outside allowed range")
	void validate_SetsPlannedOutOfRange_HasViolation(int setsPlanned) {
		WorkoutSessionItem item = validItem();
		item.setSetsPlanned(setsPlanned);

		assertViolationOn(item, "setsPlanned");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 10000 })
	@DisplayName("Should reject reps planned outside allowed range")
	void validate_RepsPlannedOutOfRange_HasViolation(int repsPlanned) {
		WorkoutSessionItem item = validItem();
		item.setRepsPlanned(repsPlanned);

		assertViolationOn(item, "repsPlanned");
	}

	@ParameterizedTest
	@ValueSource(ints = { -1 })
	@DisplayName("Should reject negative sets done")
	void validate_SetsDoneNegative_HasViolation(int setsDone) {
		WorkoutSessionItem item = validItem();
		item.setSetsDone(setsDone);

		assertViolationOn(item, "setsDone");
	}
}
