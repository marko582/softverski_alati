package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstructionTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setId(1);
		exercise.setName("Squat");

		Instruction instruction = new Instruction();
		instruction.setId(5);
		instruction.setExercise(exercise);
		instruction.setStepNumber(2);
		instruction.setDescription("Lower until thighs are parallel");

		assertAll(
				() -> assertEquals(5, instruction.getId()),
				() -> assertEquals(exercise, instruction.getExercise()),
				() -> assertEquals(2, instruction.getStepNumber()),
				() -> assertEquals("Lower until thighs are parallel", instruction.getDescription())
		);
	}
}
