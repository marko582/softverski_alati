package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutExerciseTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		Workout workout = new Workout();
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setId(1L);
		exercise.setWorkout(workout);
		exercise.setExerciseId(5L);
		exercise.setSets(4);
		exercise.setReps(8);
		exercise.setRestSeconds(60);
		exercise.setSortOrder(1);

		assertAll(
				() -> assertEquals(1L, exercise.getId()),
				() -> assertEquals(workout, exercise.getWorkout()),
				() -> assertEquals(5L, exercise.getExerciseId()),
				() -> assertEquals(4, exercise.getSets())
		);
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
	@DisplayName("Should store per-set weights and reps")
	void setPerSetValues_StoresLists() {
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setSetWeightsKg(List.of(BigDecimal.valueOf(60), BigDecimal.valueOf(65)));
		exercise.setSetReps(List.of(10, 8));

		assertEquals(2, exercise.getSetWeightsKg().size());
		assertEquals(2, exercise.getSetReps().size());
	}
}
