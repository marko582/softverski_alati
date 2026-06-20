package com.gymcore.service;

import com.gymcore.dto.EquipmentResponse;
import com.gymcore.dto.ExerciseResponse;
import com.gymcore.dto.InstructionResponse;
import com.gymcore.model.BodyPart;
import com.gymcore.model.CatalogExercise;
import com.gymcore.model.Equipment;
import com.gymcore.model.Instruction;
import com.gymcore.repository.CatalogExerciseRepository;
import com.gymcore.repository.EquipmentRepository;
import com.gymcore.repository.InstructionRepository;
import com.gymcore.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseCatalogServiceTest {

	@Mock
	private CatalogExerciseRepository exerciseRepository;

	@Mock
	private EquipmentRepository equipmentRepository;

	@Mock
	private InstructionRepository instructionRepository;

	@InjectMocks
	private ExerciseCatalogService exerciseCatalogService;

	private CatalogExercise sampleExercise;

	@BeforeEach
	void setUp() {
		sampleExercise = new CatalogExercise();
		sampleExercise.setId(1);
		sampleExercise.setName("Bench Press");
		sampleExercise.setImageUrl("/img/bench.png");
		sampleExercise.setVideoUrl("/video/bench.mp4");
		sampleExercise.setExerciseType("strength");
		sampleExercise.setDifficulty("intermediate");
		sampleExercise.setOverview("Chest compound lift");
		BodyPart chest = new BodyPart();
		chest.setId(1);
		chest.setName("Chest");
		sampleExercise.setBodyParts(Set.of(chest));
	}

	@Test
	@DisplayName("Should return all exercises with body parts")
	void findAll_ExercisesExist_ReturnsResponses() {
		when(exerciseRepository.findAllWithBodyParts()).thenReturn(List.of(sampleExercise));

		List<ExerciseResponse> result = exerciseCatalogService.findAll();

		assertEquals(1, result.size());
		assertEquals("Bench Press", result.get(0).name());
		assertEquals(List.of("Chest"), result.get(0).bodyParts());
	}

	@Test
	@DisplayName("Should return exercise by id")
	void findById_ExerciseExists_ReturnsResponse() {
		when(exerciseRepository.findById(1)).thenReturn(Optional.of(sampleExercise));

		ExerciseResponse result = exerciseCatalogService.findById(1);

		assertEquals(1, result.id());
		assertEquals("Bench Press", result.name());
		assertNull(result.bodyParts());
	}

	@Test
	@DisplayName("Should throw when exercise id does not exist")
	void findById_ExerciseMissing_ThrowsResourceNotFoundException() {
		when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> exerciseCatalogService.findById(99));
	}

	@Test
	@DisplayName("Should filter exercises using repository projection rows")
	void filter_WithCriteria_ReturnsFilteredRows() {
		Object[] row = new Object[] { 2, "Squat", "/img.png", "/vid.mp4", "strength", "beginner", "Leg exercise" };
		when(exerciseRepository.filterRows("Legs", "Barbell", "beginner")).thenReturn(List.<Object[]>of(row));

		List<ExerciseResponse> result = exerciseCatalogService.filter("Legs", "Barbell", "beginner");

		assertEquals(1, result.size());
		assertEquals("Squat", result.get(0).name());
	}

	@Test
	@DisplayName("Should return instructions ordered by step number")
	void findInstructionsByExerciseId_ExerciseExists_ReturnsInstructions() {
		Instruction step = new Instruction();
		step.setId(10);
		step.setExercise(sampleExercise);
		step.setStepNumber(1);
		step.setDescription("Grip the bar");
		when(exerciseRepository.existsById(1)).thenReturn(true);
		when(instructionRepository.findByExercise_IdOrderByStepNumberAsc(1)).thenReturn(List.of(step));

		List<InstructionResponse> result = exerciseCatalogService.findInstructionsByExerciseId(1);

		assertEquals(1, result.size());
		assertEquals(1, result.get(0).stepNumber());
		assertEquals("Grip the bar", result.get(0).description());
	}

	@Test
	@DisplayName("Should throw when instructions are requested for missing exercise")
	void findInstructionsByExerciseId_ExerciseMissing_ThrowsResourceNotFoundException() {
		when(exerciseRepository.existsById(99)).thenReturn(false);

		assertThrows(ResourceNotFoundException.class,
				() -> exerciseCatalogService.findInstructionsByExerciseId(99));
	}

	@Test
	@DisplayName("Should treat blank filter values as null when querying repository")
	void filter_BlankFilters_NormalizesToNull() {
		when(exerciseRepository.filterRows(null, null, null)).thenReturn(Collections.emptyList());

		List<ExerciseResponse> result = exerciseCatalogService.filter("  ", "", null);

		assertTrue(result.isEmpty());
		verify(exerciseRepository).filterRows(null, null, null);
	}

	@Test
	@DisplayName("Should return all equipment entries")
	void findAllEquipments_EntriesExist_ReturnsList() {
		Equipment barbell = new Equipment();
		barbell.setId(1);
		barbell.setName("Barbell");
		when(equipmentRepository.findAll()).thenReturn(List.of(barbell));

		List<EquipmentResponse> result = exerciseCatalogService.findAllEquipments();

		assertEquals(1, result.size());
		assertEquals("Barbell", result.get(0).name());
	}

	@Test
	@DisplayName("Should return empty list when no exercises exist")
	void findAll_NoExercises_ReturnsEmptyList() {
		when(exerciseRepository.findAllWithBodyParts()).thenReturn(Collections.emptyList());

		assertTrue(exerciseCatalogService.findAll().isEmpty());
		verify(exerciseRepository).findAllWithBodyParts();
	}
}
