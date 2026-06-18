package com.gymcore.service;

import com.gymcore.dto.SessionDetailResponse;
import com.gymcore.dto.SessionItemDraftRequest;
import com.gymcore.dto.SessionItemPatchRequest;
import com.gymcore.dto.SessionItemResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WorkoutSessionService {

	private final WorkoutSessionRepository sessionRepository;
	private final WorkoutRepository workoutRepository;

	public WorkoutSessionService(WorkoutSessionRepository sessionRepository, WorkoutRepository workoutRepository) {
		this.sessionRepository = sessionRepository;
		this.workoutRepository = workoutRepository;
	}

	@Transactional(readOnly = true)
	public List<SessionSummaryResponse> listMine() {
		User user = currentUser();
		return sessionRepository.findByUser_IdOrderByStartedAtDesc(user.getId()).stream()
				.map(this::toSummary)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<SessionDetailResponse> listCompletedDetailsSince(Instant since) {
		User user = currentUser();
		return sessionRepository.findCompletedWithItemsSince(user.getId(), since).stream()
				.map(this::toDetail)
				.toList();
	}

	@Transactional(readOnly = true)
	public SessionDetailResponse getMine(long id) {
		User user = currentUser();
		WorkoutSession s = sessionRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Session not found"));
		return toDetail(s);
	}

	@Transactional
	public SessionDetailResponse start(SessionStartRequest req) {
		User user = currentUser();
		if (req.workoutId() != null) {
			Workout workout = workoutRepository.findByIdAndUserIdAndDeletedFalse(req.workoutId(), user.getId())
					.orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
			WorkoutSession session = new WorkoutSession();
			session.setUser(user);
			session.setWorkout(workout);
			session.setTitle(displayTitle(req.title(), workout.getName()));
			List<WorkoutExercise> template = new java.util.ArrayList<>(workout.getExercises());
			template.sort(Comparator.comparingInt(WorkoutExercise::getSortOrder).thenComparingLong(WorkoutExercise::getId));
			for (WorkoutExercise we : template) {
				WorkoutSessionItem item = new WorkoutSessionItem();
				item.setSession(session);
				item.setExerciseId(we.getExerciseId());
				item.setSortOrder(we.getSortOrder());
				item.setSetsPlanned(we.getSets());
				List<Integer> repLine = templateRepsToSession(we.getSetReps(), we.getSets(), we.getReps());
				item.setRepsPlanned(repLine.get(0));
				item.setSetRepsPlanned(new ArrayList<>(repLine));
				item.setSetsDone(0);
				item.setSetWeightsKg(templateWeightsToSession(we.getSetWeightsKg(), we.getSets()));
				session.getItems().add(item);
			}
			sessionRepository.save(session);
			return toDetail(session);
		}
		if (req.items() == null || req.items().isEmpty()) {
			throw new IllegalArgumentException("Custom session needs at least one exercise with sets and reps.");
		}
		WorkoutSession session = new WorkoutSession();
		session.setUser(user);
		session.setWorkout(null);
		session.setTitle(displayTitle(req.title(), "Custom session"));
		int order = 0;
		for (SessionItemDraftRequest line : req.items()) {
			WorkoutSessionItem item = new WorkoutSessionItem();
			item.setSession(session);
			item.setExerciseId(line.exerciseId());
			item.setSortOrder(order++);
			item.setSetsPlanned(line.sets());
			List<Integer> repLine = repsFromDraftLine(line.repsPerSet(), line.reps(), line.sets());
			item.setRepsPlanned(repLine.get(0));
			item.setSetRepsPlanned(new ArrayList<>(repLine));
			item.setSetsDone(0);
			item.setSetWeightsKg(weightsFromDraft(line.weightsKg(), line.sets()));
			session.getItems().add(item);
		}
		sessionRepository.save(session);
		return toDetail(session);
	}

	private static String displayTitle(String requested, String fallback) {
		if (requested != null && !requested.isBlank()) {
			return requested.strip();
		}
		return fallback;
	}

	private static List<BigDecimal> emptyWeightSlots(int sets) {
		List<BigDecimal> w = new ArrayList<>(sets);
		for (int i = 0; i < sets; i++) {
			w.add(null);
		}
		return w;
	}

	private static List<Integer> templateRepsToSession(List<Integer> stored, int sets, int defaultReps) {
		List<Integer> out = new ArrayList<>(sets);
		int fb = Math.max(1, defaultReps);
		for (int i = 0; i < sets; i++) {
			if (stored != null && i < stored.size() && stored.get(i) != null && stored.get(i) >= 1) {
				out.add(stored.get(i));
			}
			else {
				out.add(fb);
			}
		}
		return out;
	}

	private static List<Integer> repsFromDraftLine(List<Integer> repsPerSet, int defaultReps, int sets) {
		if (defaultReps < 1 || defaultReps > 9999) {
			throw new IllegalArgumentException("reps must be between 1 and 9999.");
		}
		if (repsPerSet == null || repsPerSet.isEmpty()) {
			List<Integer> out = new ArrayList<>(sets);
			for (int i = 0; i < sets; i++) {
				out.add(defaultReps);
			}
			return out;
		}
		if (repsPerSet.size() != sets) {
			throw new IllegalArgumentException("repsPerSet must have exactly one entry per set, or be omitted.");
		}
		List<Integer> out = new ArrayList<>(sets);
		for (Integer r : repsPerSet) {
			if (r == null || r < 1 || r > 9999) {
				throw new IllegalArgumentException("Each set needs reps between 1 and 9999.");
			}
			out.add(r);
		}
		return out;
	}

	private static List<BigDecimal> templateWeightsToSession(List<BigDecimal> stored, int sets) {
		List<BigDecimal> w = new ArrayList<>(sets);
		for (int i = 0; i < sets; i++) {
			if (stored != null && i < stored.size()) {
				w.add(stored.get(i));
			}
			else {
				w.add(null);
			}
		}
		return w;
	}

	private static List<BigDecimal> weightsFromDraft(List<Double> weightsKg, int sets) {
		List<BigDecimal> w = emptyWeightSlots(sets);
		if (weightsKg == null || weightsKg.isEmpty()) {
			return w;
		}
		if (weightsKg.size() != sets) {
			throw new IllegalArgumentException("weightsKg must have exactly one value per set, or be omitted.");
		}
		for (int i = 0; i < sets; i++) {
			w.set(i, toStoredWeight(weightsKg.get(i)));
		}
		return w;
	}

	private static BigDecimal toStoredWeight(Double kg) {
		if (kg == null || kg <= 0 || Double.isNaN(kg)) {
			return null;
		}
		if (kg > 9999.99) {
			throw new IllegalArgumentException("Weight must be at most 9999.99 kg.");
		}
		return BigDecimal.valueOf(kg).setScale(2, RoundingMode.HALF_UP);
	}

	@Transactional
	public SessionDetailResponse patchItem(long sessionId, long itemId, SessionItemPatchRequest patch) {
		User user = currentUser();
		WorkoutSession session = sessionRepository.findByIdAndUser_Id(sessionId, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Session not found"));
		if (session.getCompletedAt() != null) {
			throw new IllegalArgumentException("Session is already completed");
		}
		WorkoutSessionItem item = session.getItems().stream()
				.filter(i -> i.getId().equals(itemId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Session item not found"));
		if (patch.setsDone() == null && patch.weightsKg() == null && patch.repsPerSet() == null) {
			throw new IllegalArgumentException("Provide setsDone, repsPerSet, and/or weightsKg to update.");
		}
		if (patch.setsDone() != null) {
			int capped = Math.min(Math.max(patch.setsDone(), 0), item.getSetsPlanned());
			item.setSetsDone(capped);
		}
		if (patch.repsPerSet() != null) {
			if (patch.repsPerSet().size() != item.getSetsPlanned()) {
				throw new IllegalArgumentException("repsPerSet length must match number of sets.");
			}
			for (Integer r : patch.repsPerSet()) {
				if (r == null || r < 1 || r > 9999) {
					throw new IllegalArgumentException("Each set needs reps between 1 and 9999.");
				}
			}
			item.setSetRepsPlanned(new ArrayList<>(patch.repsPerSet()));
			item.setRepsPlanned(patch.repsPerSet().get(0));
		}
		if (patch.weightsKg() != null) {
			if (patch.weightsKg().size() != item.getSetsPlanned()) {
				throw new IllegalArgumentException("weightsKg length must match number of sets.");
			}
			List<BigDecimal> next = new ArrayList<>();
			for (int i = 0; i < item.getSetsPlanned(); i++) {
				next.add(toStoredWeight(patch.weightsKg().get(i)));
			}
			item.setSetWeightsKg(next);
		}
		return toDetail(session);
	}

	@Transactional
	public SessionDetailResponse complete(long sessionId) {
		User user = currentUser();
		WorkoutSession session = sessionRepository.findByIdAndUser_Id(sessionId, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Session not found"));
		if (session.getCompletedAt() == null) {
			session.setCompletedAt(Instant.now());
		}
		return toDetail(session);
	}

	@Transactional
	public void deleteMine(long id) {
		User user = currentUser();
		WorkoutSession session = sessionRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Session not found"));
		sessionRepository.delete(session);
	}

	private SessionSummaryResponse toSummary(WorkoutSession s) {
		boolean active = s.getCompletedAt() == null;
		Long workoutId = s.getWorkout() != null ? s.getWorkout().getId() : null;
		return new SessionSummaryResponse(
				s.getId(),
				workoutId,
				s.getTitle(),
				s.getStartedAt(),
				s.getCompletedAt(),
				active);
	}

	private SessionDetailResponse toDetail(WorkoutSession s) {
		List<SessionItemResponse> items = s.getItems().stream()
				.sorted(Comparator.comparingInt(WorkoutSessionItem::getSortOrder).thenComparingLong(WorkoutSessionItem::getId))
				.map(i -> new SessionItemResponse(
						i.getId(),
						i.getExerciseId(),
						i.getSortOrder(),
						i.getSetsPlanned(),
						i.getRepsPlanned(),
						toRepsPlannedResponse(i),
						i.getSetsDone(),
						toWeightsResponse(i)))
				.toList();
		boolean active = s.getCompletedAt() == null;
		Long workoutId = s.getWorkout() != null ? s.getWorkout().getId() : null;
		return new SessionDetailResponse(
				s.getId(),
				workoutId,
				s.getTitle(),
				s.getStartedAt(),
				s.getCompletedAt(),
				active,
				items);
	}

	private static List<Integer> toRepsPlannedResponse(WorkoutSessionItem i) {
		int n = i.getSetsPlanned();
		List<Integer> raw = i.getSetRepsPlanned();
		int fb = i.getRepsPlanned();
		List<Integer> out = new ArrayList<>(n);
		for (int j = 0; j < n; j++) {
			if (raw != null && j < raw.size() && raw.get(j) != null && raw.get(j) >= 1) {
				out.add(raw.get(j));
			}
			else {
				out.add(Math.max(1, fb));
			}
		}
		return out;
	}

	private static List<Double> toWeightsResponse(WorkoutSessionItem i) {
		int n = i.getSetsPlanned();
		List<Double> out = new ArrayList<>(n);
		List<BigDecimal> raw = i.getSetWeightsKg();
		for (int j = 0; j < n; j++) {
			if (raw != null && j < raw.size() && raw.get(j) != null) {
				out.add(raw.get(j).doubleValue());
			}
			else {
				out.add(null);
			}
		}
		return out;
	}

	private static User currentUser() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User user)) {
			throw new IllegalStateException("Unauthenticated");
		}
		return user;
	}
}
