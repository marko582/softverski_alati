package com.gymcore.service;

import com.gymcore.dto.SessionDetailResponse;
import com.gymcore.dto.SessionItemDraftRequest;
import com.gymcore.dto.SessionItemPatchRequest;
import com.gymcore.dto.SessionStartRequest;
import com.gymcore.dto.SessionSummaryResponse;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.model.User;
import com.gymcore.model.Workout;
import com.gymcore.model.WorkoutExercise;
import com.gymcore.model.WorkoutSession;
import com.gymcore.model.WorkoutSessionItem;
import com.gymcore.repository.WorkoutRepository;
import com.gymcore.repository.WorkoutSessionRepository;
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
class WorkoutSessionServiceTest {

	@Mock
	private WorkoutSessionRepository sessionRepository;

	@Mock
	private WorkoutRepository workoutRepository;

	@InjectMocks
	private WorkoutSessionService workoutSessionService;

	private User currentUser;
	private WorkoutSession sampleSession;
	private WorkoutSessionItem sampleItem;

	@BeforeEach
	void setUp() {
		currentUser = new User();
		currentUser.setId(1L);
		currentUser.setEmail("marko@example.com");
		currentUser.setActive(true);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities()));

		sampleSession = new WorkoutSession();
		sampleSession.setId(50L);
		sampleSession.setUser(currentUser);
		sampleSession.setTitle("Morning session");
		sampleSession.setStartedAt(Instant.parse("2025-01-01T08:00:00Z"));

		sampleItem = new WorkoutSessionItem();
		sampleItem.setId(500L);
		sampleItem.setSession(sampleSession);
		sampleItem.setExerciseId(5L);
		sampleItem.setSortOrder(0);
		sampleItem.setSetsPlanned(3);
		sampleItem.setRepsPlanned(10);
		sampleItem.setSetsDone(0);
		sampleSession.setItems(new ArrayList<>(List.of(sampleItem)));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("Should list session summaries for current user")
	void listMine_SessionsExist_ReturnsSummaries() {
		when(sessionRepository.findByUser_IdOrderByStartedAtDesc(1L)).thenReturn(List.of(sampleSession));

		List<SessionSummaryResponse> result = workoutSessionService.listMine();

		assertEquals(1, result.size());
		assertEquals(50L, result.get(0).id());
		assertTrue(result.get(0).active());
	}

	@Test
	@DisplayName("Should return completed session details since the given instant")
	void listCompletedDetailsSince_CompletedSessionsExist_ReturnsDetails() {
		sampleSession.setCompletedAt(Instant.parse("2025-01-02T10:00:00Z"));
		Instant since = Instant.parse("2025-01-01T00:00:00Z");
		when(sessionRepository.findCompletedWithItemsSince(1L, since)).thenReturn(List.of(sampleSession));

		List<SessionDetailResponse> result = workoutSessionService.listCompletedDetailsSince(since);

		assertEquals(1, result.size());
		assertEquals(50L, result.get(0).id());
		assertFalse(result.get(0).active());
		verify(sessionRepository).findCompletedWithItemsSince(1L, since);
	}

	@Test
	@DisplayName("Should return session detail when owned by current user")
	void getMine_SessionExists_ReturnsDetail() {
		when(sessionRepository.findByIdAndUser_Id(50L, 1L)).thenReturn(Optional.of(sampleSession));

		SessionDetailResponse result = workoutSessionService.getMine(50L);

		assertEquals(50L, result.id());
		assertEquals(1, result.items().size());
	}

	@Test
	@DisplayName("Should start custom session from draft items")
	void start_CustomItems_CreatesSession() {
		SessionStartRequest req = new SessionStartRequest(
				null,
				"Custom",
				List.of(new SessionItemDraftRequest(5L, 3, 10, null, null)));
		when(sessionRepository.save(any(WorkoutSession.class))).thenAnswer(invocation -> {
			WorkoutSession saved = invocation.getArgument(0);
			saved.setId(60L);
			saved.getItems().forEach(item -> item.setId(600L));
			return saved;
		});

		SessionDetailResponse result = workoutSessionService.start(req);

		assertEquals("Custom", result.workoutName());
		assertEquals(1, result.items().size());
		verify(sessionRepository).save(any(WorkoutSession.class));
	}

	@Test
	@DisplayName("Should start session from existing workout template")
	void start_FromWorkout_CopiesTemplateItems() {
		Workout workout = new Workout();
		workout.setId(10L);
		workout.setName("Push Day");
		WorkoutExercise templateExercise = new WorkoutExercise();
		templateExercise.setId(100L);
		templateExercise.setExerciseId(5L);
		templateExercise.setSets(3);
		templateExercise.setReps(10);
		templateExercise.setRestSeconds(90);
		templateExercise.setSortOrder(0);
		templateExercise.setWorkout(workout);
		workout.setExercises(new ArrayList<>(List.of(templateExercise)));

		when(workoutRepository.findByIdAndUserIdAndDeletedFalse(10L, 1L)).thenReturn(Optional.of(workout));
		when(sessionRepository.save(any(WorkoutSession.class))).thenAnswer(invocation -> {
			WorkoutSession saved = invocation.getArgument(0);
			saved.setId(70L);
			saved.getItems().forEach(item -> item.setId(700L));
			return saved;
		});

		SessionDetailResponse result = workoutSessionService.start(new SessionStartRequest(10L, null, null));

		assertEquals("Push Day", result.workoutName());
		assertEquals(10L, result.workoutId());
	}

	@Test
	@DisplayName("Should patch session item sets done for active session")
	void patchItem_ValidPatch_UpdatesItem() {
		when(sessionRepository.findByIdAndUser_Id(50L, 1L)).thenReturn(Optional.of(sampleSession));

		SessionDetailResponse result = workoutSessionService.patchItem(
				50L,
				500L,
				new SessionItemPatchRequest(2, null, null));

		assertEquals(2, sampleItem.getSetsDone());
		assertEquals(50L, result.id());
	}

	@Test
	@DisplayName("Should throw when patching completed session")
	void patchItem_CompletedSession_ThrowsIllegalArgumentException() {
		sampleSession.setCompletedAt(Instant.now());
		when(sessionRepository.findByIdAndUser_Id(50L, 1L)).thenReturn(Optional.of(sampleSession));

		assertThrows(IllegalArgumentException.class,
				() -> workoutSessionService.patchItem(50L, 500L, new SessionItemPatchRequest(1, null, null)));
	}

	@Test
	@DisplayName("Should mark session as completed")
	void complete_ActiveSession_SetsCompletedAt() {
		when(sessionRepository.findByIdAndUser_Id(50L, 1L)).thenReturn(Optional.of(sampleSession));

		SessionDetailResponse result = workoutSessionService.complete(50L);

		assertNotNull(sampleSession.getCompletedAt());
		assertFalse(result.active());
	}

	@Test
	@DisplayName("Should delete session owned by current user")
	void deleteMine_SessionExists_DeletesSession() {
		when(sessionRepository.findByIdAndUser_Id(50L, 1L)).thenReturn(Optional.of(sampleSession));

		workoutSessionService.deleteMine(50L);

		verify(sessionRepository).delete(sampleSession);
	}

	@Test
	@DisplayName("Should throw when session is not found")
	void getMine_SessionMissing_ThrowsResourceNotFoundException() {
		when(sessionRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> workoutSessionService.getMine(99L));
	}

	@Test
	@DisplayName("Should return empty list when user has no sessions")
	void listMine_NoSessions_ReturnsEmptyList() {
		when(sessionRepository.findByUser_IdOrderByStartedAtDesc(1L)).thenReturn(Collections.emptyList());

		assertTrue(workoutSessionService.listMine().isEmpty());
	}
}
