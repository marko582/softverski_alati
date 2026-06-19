package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogExerciseTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		CatalogExercise exercise = new CatalogExercise();
		exercise.setId(1);
		exercise.setName("Bench Press");
		exercise.setImageUrl("/img/bench.png");
		exercise.setVideoUrl("/video/bench.mp4");
		exercise.setExerciseType("strength");
		exercise.setDifficulty("intermediate");
		exercise.setOverview("Chest compound lift");

		assertAll(
				() -> assertEquals(1, exercise.getId()),
				() -> assertEquals("Bench Press", exercise.getName()),
				() -> assertEquals("strength", exercise.getExerciseType()),
				() -> assertEquals("intermediate", exercise.getDifficulty())
		);
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
}
