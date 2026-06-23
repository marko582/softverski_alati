package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogExerciseTest {

	private CatalogExercise validExercise() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setName("Bench Press");
		exercise.setImageUrl("/img/bench.png");
		exercise.setExerciseType("strength");
		exercise.setDifficulty("intermediate");
		exercise.setOverview("Chest compound lift");
		return exercise;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setId(1);

		assertEquals(1, exercise.getId());
	}

	@Test
	@DisplayName("Should store name through getter after setter")
	void setName_getName_ReturnsSameValue() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setName("Bench Press");

		assertEquals("Bench Press", exercise.getName());
	}

	@Test
	@DisplayName("Should store image URL through getter after setter")
	void setImageUrl_getImageUrl_ReturnsSameValue() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setImageUrl("/img/bench.png");

		assertEquals("/img/bench.png", exercise.getImageUrl());
	}

	@Test
	@DisplayName("Should store video URL through getter after setter")
	void setVideoUrl_getVideoUrl_ReturnsSameValue() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setVideoUrl("/video/bench.mp4");

		assertEquals("/video/bench.mp4", exercise.getVideoUrl());
	}

	@Test
	@DisplayName("Should store exercise type through getter after setter")
	void setExerciseType_getExerciseType_ReturnsSameValue() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setExerciseType("strength");

		assertEquals("strength", exercise.getExerciseType());
	}

	@Test
	@DisplayName("Should store difficulty through getter after setter")
	void setDifficulty_getDifficulty_ReturnsSameValue() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setDifficulty("intermediate");

		assertEquals("intermediate", exercise.getDifficulty());
	}

	@Test
	@DisplayName("Should store overview through getter after setter")
	void setOverview_getOverview_ReturnsSameValue() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setOverview("Chest compound lift");

		assertEquals("Chest compound lift", exercise.getOverview());
	}

	@Test
	@DisplayName("Should initialize relationship collections as empty")
	void defaultCollections_AreEmpty() {
		CatalogExercise exercise = new CatalogExercise();

		assertNotNull(exercise.getBodyParts());
		assertNotNull(exercise.getEquipments());
		assertNotNull(exercise.getInstructions());
		assertTrue(exercise.getBodyParts().isEmpty());
		assertTrue(exercise.getEquipments().isEmpty());
		assertTrue(exercise.getInstructions().isEmpty());
	}

	@Test
	@DisplayName("Should store linked body parts and instructions")
	void setRelationships_StoresAssociations() {
		BodyPart chest = new BodyPart();
		chest.setId(1);
		chest.setName("Chest");

		Instruction step = new Instruction();
		step.setId(10);
		step.setStepNumber(1);
		step.setDescription("Grip the bar");

		CatalogExercise exercise = new CatalogExercise();
		exercise.setBodyParts(new HashSet<>(List.of(chest)));
		exercise.setInstructions(List.of(step));

		assertEquals(1, exercise.getBodyParts().size());
		assertEquals("Chest", exercise.getBodyParts().iterator().next().getName());
		assertEquals(1, exercise.getInstructions().size());
		assertEquals("Grip the bar", exercise.getInstructions().get(0).getDescription());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validExercise());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank exercise name")
	void validate_NameBlank_HasViolation(String name) {
		CatalogExercise exercise = validExercise();
		exercise.setName(name);

		assertViolationOn(exercise, "name");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank image URL")
	void validate_ImageUrlBlank_HasViolation(String imageUrl) {
		CatalogExercise exercise = validExercise();
		exercise.setImageUrl(imageUrl);

		assertViolationOn(exercise, "imageUrl");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank exercise type")
	void validate_ExerciseTypeBlank_HasViolation(String exerciseType) {
		CatalogExercise exercise = validExercise();
		exercise.setExerciseType(exerciseType);

		assertViolationOn(exercise, "exerciseType");
	}

	@Test
	@DisplayName("Should reject exercise type longer than 50 characters")
	void validate_ExerciseTypeTooLong_HasViolation() {
		CatalogExercise exercise = validExercise();
		exercise.setExerciseType("a".repeat(51));

		assertViolationOn(exercise, "exerciseType");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank difficulty")
	void validate_DifficultyBlank_HasViolation(String difficulty) {
		CatalogExercise exercise = validExercise();
		exercise.setDifficulty(difficulty);

		assertViolationOn(exercise, "difficulty");
	}

	@Test
	@DisplayName("Should reject difficulty longer than 50 characters")
	void validate_DifficultyTooLong_HasViolation() {
		CatalogExercise exercise = validExercise();
		exercise.setDifficulty("a".repeat(51));

		assertViolationOn(exercise, "difficulty");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank overview")
	void validate_OverviewBlank_HasViolation(String overview) {
		CatalogExercise exercise = validExercise();
		exercise.setOverview(overview);

		assertViolationOn(exercise, "overview");
	}
}
