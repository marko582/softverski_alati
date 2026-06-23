package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InstructionTest {

	private CatalogExercise validExercise() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setName("Squat");
		exercise.setImageUrl("/img/squat.png");
		exercise.setExerciseType("strength");
		exercise.setDifficulty("beginner");
		exercise.setOverview("Compound leg exercise");
		return exercise;
	}

	private Instruction validInstruction() {
		Instruction instruction = new Instruction();
		instruction.setExercise(validExercise());
		instruction.setStepNumber(1);
		instruction.setDescription("Lower until thighs are parallel");
		return instruction;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		Instruction instruction = new Instruction();
		instruction.setId(5);

		assertEquals(5, instruction.getId());
	}

	@Test
	@DisplayName("Should store exercise through getter after setter")
	void setExercise_getExercise_ReturnsSameValue() {
		CatalogExercise exercise = validExercise();
		Instruction instruction = new Instruction();
		instruction.setExercise(exercise);

		assertEquals(exercise, instruction.getExercise());
	}

	@Test
	@DisplayName("Should store step number through getter after setter")
	void setStepNumber_getStepNumber_ReturnsSameValue() {
		Instruction instruction = new Instruction();
		instruction.setStepNumber(2);

		assertEquals(2, instruction.getStepNumber());
	}

	@Test
	@DisplayName("Should store description through getter after setter")
	void setDescription_getDescription_ReturnsSameValue() {
		Instruction instruction = new Instruction();
		instruction.setDescription("Lower until thighs are parallel");

		assertEquals("Lower until thighs are parallel", instruction.getDescription());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validInstruction());
	}

	@Test
	@DisplayName("Should reject null exercise reference")
	void validate_ExerciseNull_HasViolation() {
		Instruction instruction = validInstruction();
		instruction.setExercise(null);

		assertViolationOn(instruction, "exercise");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, -1 })
	@DisplayName("Should reject step number less than 1")
	void validate_StepNumberTooSmall_HasViolation(int stepNumber) {
		Instruction instruction = validInstruction();
		instruction.setStepNumber(stepNumber);

		assertViolationOn(instruction, "stepNumber");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank description")
	void validate_DescriptionBlank_HasViolation(String description) {
		Instruction instruction = validInstruction();
		instruction.setDescription(description);

		assertViolationOn(instruction, "description");
	}
}
