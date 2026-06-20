package com.gymcore.service;

import com.gymcore.dto.WorkoutCreateRequest;
import com.gymcore.dto.WorkoutDetailResponse;
import com.gymcore.dto.WorkoutExerciseInput;
import com.gymcore.dto.WorkoutSummaryResponse;
import com.gymcore.dto.WorkoutUpdateRequest;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.model.User;
import com.gymcore.model.Workout;
import com.gymcore.model.WorkoutExercise;
import com.gymcore.repository.WorkoutRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

	@Mock
	private WorkoutRepository workoutRepository;

	@InjectMocks
	private WorkoutService workoutService;

	private User currentUser;
	private Workout sampleWorkout;

	@BeforeEach
	void setUp() {
		currentUser = new User();
		currentUser.setId(1L);
		currentUser.setEmail("marko@example.com");
		currentUser.setDisplayName("marko582");
		currentUser.setActive(true);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities()));

		sampleWorkout = new Workout();
		sampleWorkout.setId(10L);
		sampleWorkout.setUser(currentUser);
		sampleWorkout.setName("Push Day");
		sampleWorkout.setDescription("Chest focus");
		sampleWorkout.setCreatedAt(Instant.parse("2025-01-01T10:00:00Z"));
		sampleWorkout.setDeleted(false);
		WorkoutExercise exercise = new WorkoutExercise();
		exercise.setId(100L);
		exercise.setExerciseId(5L);
		exercise.setSets(3);
		exercise.setReps(10);
		exercise.setRestSeconds(90);
		exercise.setSortOrder(0);
		exercise.setWorkout(sampleWorkout);
		sampleWorkout.setExercises(new ArrayList<>(List.of(exercise)));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("Should return mapped workout summaries for current user")
	void listMine_WorkoutsExist_ReturnsSummaryList() {
		when(workoutRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L))
				.thenReturn(List.of(sampleWorkout));

		List<WorkoutSummaryResponse> result = workoutService.listMine();

		assertEquals(1, result.size());
		assertEquals(10L, result.get(0).id());
		assertEquals("Push Day", result.get(0).name());
		assertEquals(1, result.get(0).exerciseCount());
	}

	@Test
	@DisplayName("Should return empty list when user has no workouts")
	void listMine_NoWorkouts_ReturnsEmptyList() {
		when(workoutRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L))
				.thenReturn(Collections.emptyList());

		List<WorkoutSummaryResponse> result = workoutService.listMine();

		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("Should return workout detail when owned by current user")
	void getMine_WorkoutExists_ReturnsDetail() {
		when(workoutRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L))
				.thenReturn(Optional.of(sampleWorkout));

		WorkoutDetailResponse result = workoutService.getMine(10L);

		assertEquals(10L, result.id());
		assertEquals("Push Day", result.name());
		assertEquals(1, result.exercises().size());
	}

	@Test
	@DisplayName("Should throw when workout does not exist for current user")
	void getMine_WorkoutMissing_ThrowsResourceNotFoundException() {
		when(workoutRepository.findByIdAndUserIdAndDeletedFalse(99L, 1L))
				.thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> workoutService.getMine(99L));
	}

	@Test
	@DisplayName("Should create workout and return detail response")
	void create_ValidInput_SavesAndReturnsDetail() {
		WorkoutCreateRequest req = new WorkoutCreateRequest(
				"Leg Day",
				"Quads",
				List.of(new WorkoutExerciseInput(5L, 3, 10, 90, 0, null, null)));
		when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> {
			Workout saved = invocation.getArgument(0);
			saved.setId(20L);
			saved.setCreatedAt(Instant.now());
			assignExerciseIds(saved);
			return saved;
		});

		WorkoutDetailResponse result = workoutService.create(req);

		assertEquals("Leg Day", result.name());
		assertEquals(1, result.exercises().size());
		verify(workoutRepository).save(any(Workout.class));
	}

	@Test
	@DisplayName("Should update existing workout owned by current user")
	void update_WorkoutExists_UpdatesAndReturnsDetail() {
		WorkoutUpdateRequest req = new WorkoutUpdateRequest(
				"Updated Push",
				"New desc",
				List.of(new WorkoutExerciseInput(5L, 4, 8, 60, 0, null, null)));
		when(workoutRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L))
				.thenReturn(Optional.of(sampleWorkout));
		when(workoutRepository.save(sampleWorkout)).thenAnswer(invocation -> {
			assignExerciseIds(invocation.getArgument(0));
			return sampleWorkout;
		});

		WorkoutDetailResponse result = workoutService.update(10L, req);

		assertEquals("Updated Push", result.name());
		assertEquals(1, result.exercises().size());
	}

	@Test
	@DisplayName("Should soft-delete workout owned by current user")
	void softDelete_WorkoutExists_MarksDeleted() {
		when(workoutRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L))
				.thenReturn(Optional.of(sampleWorkout));

		workoutService.softDelete(10L);

		assertTrue(sampleWorkout.isDeleted());
	}

	private static void assignExerciseIds(Workout workout) {
		long nextId = 200L;
		for (WorkoutExercise exercise : workout.getExercises()) {
			if (exercise.getId() == null) {
				exercise.setId(nextId++);
			}
		}
	}
}
