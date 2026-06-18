package com.gymcore.service;

import com.gymcore.dto.AchievementDto;
import com.gymcore.model.Achievement;
import com.gymcore.model.User;
import com.gymcore.model.UserAchievement;
import com.gymcore.model.Workout;
import com.gymcore.model.WorkoutSession;
import com.gymcore.repository.AchievementRepository;
import com.gymcore.repository.UserAchievementRepository;
import com.gymcore.repository.UserBodyMetricRepository;
import com.gymcore.repository.UserPersonalRecordRepository;
import com.gymcore.repository.UserRepository;
import com.gymcore.repository.WorkoutRepository;
import com.gymcore.repository.WorkoutSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service evaluating and persisting user achievement unlocks.
 * Syncs earned badges based on workout, session, PR, and body-metric activity.
 * @author Marko Mijailovic (marko582)
 */
@Service
public class AchievementService {

	private final AchievementRepository achievementRepository;
	private final UserAchievementRepository userAchievementRepository;
	private final UserRepository userRepository;
	private final WorkoutRepository workoutRepository;
	private final WorkoutSessionRepository sessionRepository;
	private final UserPersonalRecordRepository personalRecordRepository;
	private final UserBodyMetricRepository bodyMetricRepository;

	public AchievementService(
			AchievementRepository achievementRepository,
			UserAchievementRepository userAchievementRepository,
			UserRepository userRepository,
			WorkoutRepository workoutRepository,
			WorkoutSessionRepository sessionRepository,
			UserPersonalRecordRepository personalRecordRepository,
			UserBodyMetricRepository bodyMetricRepository) {
		this.achievementRepository = achievementRepository;
		this.userAchievementRepository = userAchievementRepository;
		this.userRepository = userRepository;
		this.workoutRepository = workoutRepository;
		this.sessionRepository = sessionRepository;
		this.personalRecordRepository = personalRecordRepository;
		this.bodyMetricRepository = bodyMetricRepository;
	}

	/**
	 * Persists newly earned achievements and returns the full list with unlock state.
	 * @param userId unique identifier of the user.
	 * @return all achievement definitions with unlock status for the user.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<AchievementDto> syncAndList(Long userId) {
		Context ctx = loadContext(userId);
		User userRef = userRepository.getReferenceById(userId);
		List<Achievement> defs = achievementRepository.findAllByOrderBySortOrderAsc();

		for (Achievement def : defs) {
			if (!isEarned(def.getId(), ctx)) {
				continue;
			}
			if (userAchievementRepository.existsByUser_IdAndAchievement_Id(userId, def.getId())) {
				continue;
			}
			Instant at = unlockInstant(def.getId(), ctx).orElse(Instant.now());
			UserAchievement ua = new UserAchievement();
			ua.setUser(userRef);
			ua.setAchievement(def);
			ua.setUnlockedAt(at);
			userAchievementRepository.save(ua);
		}

		List<UserAchievement> rows = userAchievementRepository.findByUser_Id(userId);
		Map<String, UserAchievement> byCode = new HashMap<>();
		for (UserAchievement ua : rows) {
			byCode.put(ua.getAchievement().getId(), ua);
		}

		return defs.stream()
				.map(def -> {
					UserAchievement ua = byCode.get(def.getId());
					boolean unlocked = ua != null;
					return new AchievementDto(
							def.getId(),
							def.getTitle(),
							def.getDescription(),
							unlocked,
							unlocked ? ua.getUnlockedAt() : null);
				})
				.toList();
	}

	private Context loadContext(Long userId) {
		long workouts = workoutRepository.countByUser_IdAndDeletedFalse(userId);
		long completedSessions = sessionRepository.countByUser_IdAndCompletedAtIsNotNull(userId);
		long allSessions = sessionRepository.countByUser_Id(userId);
		long prCount = personalRecordRepository.countByUser_Id(userId);
		long bodyCount = bodyMetricRepository.countByUser_Id(userId);
		boolean customSession = sessionRepository.existsByUser_IdAndWorkoutIsNull(userId);
		List<WorkoutSession> completedOrdered =
				sessionRepository.findByUser_IdAndCompletedAtIsNotNullOrderByCompletedAtAsc(userId);
		List<Workout> workoutsOrdered = workoutRepository.findByUser_IdAndDeletedFalseOrderByCreatedAtAsc(userId);
		Page<WorkoutSession> veteranPage =
				sessionRepository.findByUser_IdOrderByStartedAtAsc(userId, PageRequest.of(19, 1));
		Optional<Instant> veteranAt = veteranPage.hasContent()
				? Optional.of(veteranPage.getContent().get(0).getStartedAt())
				: Optional.empty();
		return new Context(
				workouts,
				completedSessions,
				allSessions,
				prCount,
				bodyCount,
				customSession,
				completedOrdered,
				workoutsOrdered,
				workoutRepository.findOldestWorkoutCreatedAt(userId),
				sessionRepository.findFirstCompletedAt(userId),
				sessionRepository.findFirstCustomSessionStartedAt(userId),
				personalRecordRepository.findFirstRecordedAt(userId),
				bodyMetricRepository.findFirstMeasuredAt(userId),
				veteranAt);
	}

	private boolean isEarned(String code, Context ctx) {
		return switch (code) {
			case "first_workout" -> ctx.workouts() >= 1;
			case "first_finish" -> ctx.completedSessions() >= 1;
			case "sessions_5" -> ctx.completedSessions() >= 5;
			case "sessions_10" -> ctx.completedSessions() >= 10;
			case "library_5" -> ctx.workouts() >= 5;
			case "custom_runner" -> ctx.customSession();
			case "pr_logged" -> ctx.prCount() >= 1;
			case "body_logged" -> ctx.bodyCount() >= 1;
			case "veteran" -> ctx.allSessions() >= 20;
			default -> false;
		};
	}

	private Optional<Instant> unlockInstant(String code, Context ctx) {
		return switch (code) {
			case "first_workout" -> ctx.oldestWorkout();
			case "first_finish" -> ctx.firstCompleted();
			case "sessions_5" -> Optional.ofNullable(nthCompletedAt(ctx.completedOrdered(), 5));
			case "sessions_10" -> Optional.ofNullable(nthCompletedAt(ctx.completedOrdered(), 10));
			case "library_5" -> Optional.ofNullable(nthWorkoutAt(ctx.workoutsOrdered(), 5));
			case "custom_runner" -> ctx.firstCustomSession();
			case "pr_logged" -> ctx.firstPr();
			case "body_logged" -> ctx.firstBodyMetric();
			case "veteran" -> ctx.veteranStartedAt();
			default -> Optional.empty();
		};
	}

	private static Instant nthCompletedAt(List<WorkoutSession> ordered, int n) {
		if (ordered.size() < n) {
			return null;
		}
		return ordered.get(n - 1).getCompletedAt();
	}

	private static Instant nthWorkoutAt(List<Workout> ordered, int n) {
		if (ordered.size() < n) {
			return null;
		}
		return ordered.get(n - 1).getCreatedAt();
	}

	private record Context(
			long workouts,
			long completedSessions,
			long allSessions,
			long prCount,
			long bodyCount,
			boolean customSession,
			List<WorkoutSession> completedOrdered,
			List<Workout> workoutsOrdered,
			Optional<Instant> oldestWorkout,
			Optional<Instant> firstCompleted,
			Optional<Instant> firstCustomSession,
			Optional<Instant> firstPr,
			Optional<Instant> firstBodyMetric,
			Optional<Instant> veteranStartedAt) {
	}
}
