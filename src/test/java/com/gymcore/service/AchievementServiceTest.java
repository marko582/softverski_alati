package com.gymcore.service;

import com.gymcore.dto.AchievementDto;
import com.gymcore.model.Achievement;
import com.gymcore.model.User;
import com.gymcore.model.UserAchievement;
import com.gymcore.repository.AchievementRepository;
import com.gymcore.repository.UserAchievementRepository;
import com.gymcore.repository.UserBodyMetricRepository;
import com.gymcore.repository.UserPersonalRecordRepository;
import com.gymcore.repository.UserRepository;
import com.gymcore.repository.WorkoutRepository;
import com.gymcore.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

	@Mock
	private AchievementRepository achievementRepository;

	@Mock
	private UserAchievementRepository userAchievementRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private WorkoutRepository workoutRepository;

	@Mock
	private WorkoutSessionRepository sessionRepository;

	@Mock
	private UserPersonalRecordRepository personalRecordRepository;

	@Mock
	private UserBodyMetricRepository bodyMetricRepository;

	@InjectMocks
	private AchievementService achievementService;

	private Achievement firstWorkout;

	@BeforeEach
	void setUp() {
		firstWorkout = new Achievement();
		firstWorkout.setId("first_workout");
		firstWorkout.setTitle("First workout");
		firstWorkout.setDescription("Create your first workout");
		firstWorkout.setSortOrder(1);
	}

	private void stubEmptyContext(long userId) {
		when(workoutRepository.countByUser_IdAndDeletedFalse(userId)).thenReturn(0L);
		when(sessionRepository.countByUser_IdAndCompletedAtIsNotNull(userId)).thenReturn(0L);
		when(sessionRepository.countByUser_Id(userId)).thenReturn(0L);
		when(personalRecordRepository.countByUser_Id(userId)).thenReturn(0L);
		when(bodyMetricRepository.countByUser_Id(userId)).thenReturn(0L);
		when(sessionRepository.existsByUser_IdAndWorkoutIsNull(userId)).thenReturn(false);
		when(sessionRepository.findByUser_IdAndCompletedAtIsNotNullOrderByCompletedAtAsc(userId))
				.thenReturn(Collections.emptyList());
		when(workoutRepository.findByUser_IdAndDeletedFalseOrderByCreatedAtAsc(userId))
				.thenReturn(Collections.emptyList());
		when(sessionRepository.findByUser_IdOrderByStartedAtAsc(eq(userId), any(PageRequest.class)))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		when(workoutRepository.findOldestWorkoutCreatedAt(userId)).thenReturn(Optional.empty());
		when(sessionRepository.findFirstCompletedAt(userId)).thenReturn(Optional.empty());
		when(sessionRepository.findFirstCustomSessionStartedAt(userId)).thenReturn(Optional.empty());
		when(personalRecordRepository.findFirstRecordedAt(userId)).thenReturn(Optional.empty());
		when(bodyMetricRepository.findFirstMeasuredAt(userId)).thenReturn(Optional.empty());
	}

	@Test
	@DisplayName("Should unlock first_workout achievement when user has a workout")
	void syncAndList_UserHasWorkout_UnlocksFirstWorkout() {
		long userId = 1L;
		Instant unlockedAt = Instant.parse("2025-01-01T10:00:00Z");
		stubEmptyContext(userId);
		when(workoutRepository.countByUser_IdAndDeletedFalse(userId)).thenReturn(1L);
		when(workoutRepository.findOldestWorkoutCreatedAt(userId)).thenReturn(Optional.of(unlockedAt));
		when(achievementRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(firstWorkout));
		when(userAchievementRepository.existsByUser_IdAndAchievement_Id(userId, "first_workout")).thenReturn(false);
		when(userRepository.getReferenceById(userId)).thenReturn(new User());
		when(userAchievementRepository.findByUser_Id(userId)).thenAnswer(invocation -> {
			UserAchievement ua = new UserAchievement();
			ua.setAchievement(firstWorkout);
			ua.setUnlockedAt(unlockedAt);
			return List.of(ua);
		});

		List<AchievementDto> result = achievementService.syncAndList(userId);

		assertEquals(1, result.size());
		assertTrue(result.get(0).unlocked());
		assertEquals("first_workout", result.get(0).id());
		verify(userAchievementRepository).save(any(UserAchievement.class));
	}

	@Test
	@DisplayName("Should return locked achievements when criteria are not met")
	void syncAndList_NoProgress_ReturnsLockedAchievements() {
		long userId = 2L;
		stubEmptyContext(userId);
		when(achievementRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(firstWorkout));
		when(userAchievementRepository.findByUser_Id(userId)).thenReturn(Collections.emptyList());

		List<AchievementDto> result = achievementService.syncAndList(userId);

		assertEquals(1, result.size());
		assertFalse(result.get(0).unlocked());
		assertNull(result.get(0).unlockedAt());
		verify(userAchievementRepository, never()).save(any(UserAchievement.class));
	}
}
