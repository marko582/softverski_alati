package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutExerciseTest {

	private Workout validWorkout() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		Workout workout = new Workout();
		workout.setUser(user);
		workout.setName("Push Day");
		return workout;
	}

	private WorkoutExercise validExercise() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setWorkout(validWorkout());
		exercise.setExerciseId(5L);
		exercise.setSets(4);
		exercise.setReps(8);
		exercise.setRestSeconds(60);
		exercise.setSortOrder(1);
		exercise.setSetWeightsKg(List.of(BigDecimal.valueOf(60)));
		exercise.setSetReps(List.of(8));
		return exercise;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setId(1L);

		assertEquals(1L, exercise.getId());
	}

	@Test
	@DisplayName("Should store workout through getter after setter")
	void setWorkout_getWorkout_ReturnsSameValue() {
		Workout workout = validWorkout();
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setWorkout(workout);

		assertEquals(workout, exercise.getWorkout());
	}

	@Test
	@DisplayName("Should store exercise id through getter after setter")
	void setExerciseId_getExerciseId_ReturnsSameValue() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setExerciseId(5L);

		assertEquals(5L, exercise.getExerciseId());
	}

	@Test
	@DisplayName("Should store sets through getter after setter")
	void setSets_getSets_ReturnsSameValue() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setSets(4);

		assertEquals(4, exercise.getSets());
	}

	@Test
	@DisplayName("Should store reps through getter after setter")
	void setReps_getReps_ReturnsSameValue() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setReps(8);

		assertEquals(8, exercise.getReps());
	}

	@Test
	@DisplayName("Should store rest seconds through getter after setter")
	void setRestSeconds_getRestSeconds_ReturnsSameValue() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setRestSeconds(60);

		assertEquals(60, exercise.getRestSeconds());
	}

	@Test
	@DisplayName("Should store sort order through getter after setter")
	void setSortOrder_getSortOrder_ReturnsSameValue() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setSortOrder(1);

		assertEquals(1, exercise.getSortOrder());
	}

	@Test
	@DisplayName("Should default JSON list setters to empty lists for null input")
	void setSetWeightsKg_NullInput_UsesEmptyList() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setSetWeightsKg(null);

		assertNotNull(exercise.getSetWeightsKg());
		assertTrue(exercise.getSetWeightsKg().isEmpty());
	}

	@Test
	@DisplayName("Should default set reps list setter to empty list for null input")
	void setSetReps_NullInput_UsesEmptyList() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setSetReps(null);

		assertNotNull(exercise.getSetReps());
		assertTrue(exercise.getSetReps().isEmpty());
	}

	@Test
	@DisplayName("Should store per-set weights and reps")
	void setPerSetValues_StoresLists() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setSetWeightsKg(List.of(BigDecimal.valueOf(60), BigDecimal.valueOf(65)));
		exercise.setSetReps(List.of(10, 8));

		assertEquals(2, exercise.getSetWeightsKg().size());
		assertEquals(2, exercise.getSetReps().size());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validExercise());
	}

	@Test
	@DisplayName("Should reject null workout reference")
	void validate_WorkoutNull_HasViolation() {
		WorkoutExercise exercise = validExercise();
		exercise.setWorkout(null);

		assertViolationOn(exercise, "workout");
	}

	@ParameterizedTest
	@ValueSource(longs = { 0L, -1L })
	@DisplayName("Should reject non-positive exercise id")
	void validate_ExerciseIdNotPositive_HasViolation(long exerciseId) {
		WorkoutExercise exercise = validExercise();
		exercise.setExerciseId(exerciseId);

		assertViolationOn(exercise, "exerciseId");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100 })
	@DisplayName("Should reject sets outside allowed range")
	void validate_SetsOutOfRange_HasViolation(int sets) {
		WorkoutExercise exercise = validExercise();
		exercise.setSets(sets);

		assertViolationOn(exercise, "sets");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 10000 })
	@DisplayName("Should reject reps outside allowed range")
	void validate_RepsOutOfRange_HasViolation(int reps) {
		WorkoutExercise exercise = validExercise();
		exercise.setReps(reps);

		assertViolationOn(exercise, "reps");
	}

	@ParameterizedTest
	@ValueSource(ints = { -1 })
	@DisplayName("Should reject negative rest seconds")
	void validate_RestSecondsNegative_HasViolation(int restSeconds) {
		WorkoutExercise exercise = validExercise();
		exercise.setRestSeconds(restSeconds);

		assertViolationOn(exercise, "restSeconds");
	}

	@ParameterizedTest
	@ValueSource(ints = { -1 })
	@DisplayName("Should reject negative sort order")
	void validate_SortOrderNegative_HasViolation(int sortOrder) {
		WorkoutExercise exercise = validExercise();
		exercise.setSortOrder(sortOrder);

		assertViolationOn(exercise, "sortOrder");
	}
}
