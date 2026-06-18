package com.gymcore.service;

import com.gymcore.dto.WorkoutCreateRequest;
import com.gymcore.dto.WorkoutDetailResponse;
import com.gymcore.dto.WorkoutExerciseInput;
import com.gymcore.dto.WorkoutExerciseResponse;
import com.gymcore.dto.WorkoutSummaryResponse;
import com.gymcore.dto.WorkoutUpdateRequest;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.model.User;
import com.gymcore.model.Workout;
import com.gymcore.model.WorkoutExercise;
import com.gymcore.repository.WorkoutRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service managing workout templates for the authenticated user.
 * Handles creation, retrieval, update, and soft deletion of workouts.
 * @author Marko Mijailovic (marko582)
 */
@Service
public class WorkoutService {

	private final WorkoutRepository workoutRepository;

	public WorkoutService(WorkoutRepository workoutRepository) {
		this.workoutRepository = workoutRepository;
	}

	/**
	 * Lists all non-deleted workouts owned by the current user.
	 * @return summary list ordered by creation date descending.
	 */
	@Transactional(readOnly = true)
	public List<WorkoutSummaryResponse> listMine() {
		User user = currentUser();
		return workoutRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(user.getId()).stream()
				.map(w -> new WorkoutSummaryResponse(
						w.getId(),
						w.getName(),
						w.getDescription(),
						w.getCreatedAt(),
						w.getExercises().size()))
				.toList();
	}

	/**
	 * Retrieves a workout owned by the current user.
	 * @param id unique identifier of the workout.
	 * @return workout detail including exercises.
	 * @throws com.gymcore.exception.ResourceNotFoundException if the workout does not exist or is deleted.
	 */
	@Transactional(readOnly = true)
	public WorkoutDetailResponse getMine(long id) {
		User user = currentUser();
		Workout w = workoutRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
		return toDetail(w);
	}

	/**
	 * Creates a new workout template for the current user.
	 * @param req creation payload with name, description, and exercises.
	 * @return detail of the persisted workout.
	 */
	@Transactional
	public WorkoutDetailResponse create(WorkoutCreateRequest req) {
		User user = currentUser();
		Workout w = new Workout();
		w.setUser(user);
		w.setName(req.name().trim());
		w.setDescription(req.description());
		w.setDeleted(false);
		applyExercises(w, req.exercises() == null ? List.of() : req.exercises());
		workoutRepository.save(w);
		return toDetail(w);
	}

	/**
	 * Updates an existing workout owned by the current user.
	 * @param id unique identifier of the workout.
	 * @param req update payload with name, description, and exercises.
	 * @return detail of the updated workout.
	 * @throws com.gymcore.exception.ResourceNotFoundException if the workout does not exist or is deleted.
	 */
	@Transactional
	public WorkoutDetailResponse update(long id, WorkoutUpdateRequest req) {
		User user = currentUser();
		Workout w = workoutRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
		w.setName(req.name().trim());
		w.setDescription(req.description());
		w.getExercises().clear();
		applyExercises(w, req.exercises() == null ? List.of() : req.exercises());
		workoutRepository.save(w);
		return toDetail(w);
	}

	/**
	 * Soft-deletes a workout owned by the current user.
	 * @param id unique identifier of the workout.
	 * @throws com.gymcore.exception.ResourceNotFoundException if the workout does not exist or is already deleted.
	 */
	@Transactional
	public void softDelete(long id) {
		User user = currentUser();
		Workout w = workoutRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Workout not found"));
		w.setDeleted(true);
	}

	private static void applyExercises(Workout workout, List<WorkoutExerciseInput> inputs) {
		List<WorkoutExerciseInput> sorted = new ArrayList<>(inputs);
		sorted.sort(Comparator.comparingInt(i -> i.sortOrder() != null ? i.sortOrder() : Integer.MAX_VALUE));
		for (int i = 0; i < sorted.size(); i++) {
			WorkoutExerciseInput in = sorted.get(i);
			WorkoutExercise we = new WorkoutExercise();
			we.setWorkout(workout);
			we.setExerciseId(in.exerciseId());
			we.setSets(in.sets());
			List<Integer> normReps = repsFromInput(in.repsPerSet(), in.reps(), in.sets());
			we.setSetReps(new ArrayList<>(normReps));
			we.setReps(normReps.get(0));
			we.setRestSeconds(in.restSeconds());
			we.setSortOrder(in.sortOrder() != null ? in.sortOrder() : i);
			we.setSetWeightsKg(weightsFromInput(in.weightsKg(), in.sets()));
			workout.getExercises().add(we);
		}
	}

	private static List<Integer> repsFromInput(List<Integer> repsPerSet, int defaultReps, int sets) {
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

	private static List<BigDecimal> weightsFromInput(List<Double> weightsKg, int sets) {
		List<BigDecimal> w = new ArrayList<>(sets);
		if (weightsKg == null || weightsKg.isEmpty()) {
			for (int i = 0; i < sets; i++) {
				w.add(null);
			}
			return w;
		}
		if (weightsKg.size() != sets) {
			throw new IllegalArgumentException("weightsKg must have exactly one value per set, or be omitted.");
		}
		for (int i = 0; i < sets; i++) {
			w.add(toStoredWeight(weightsKg.get(i)));
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

	private static WorkoutDetailResponse toDetail(Workout w) {
		List<WorkoutExerciseResponse> ex = w.getExercises().stream()
				.sorted(Comparator.comparingInt(WorkoutExercise::getSortOrder).thenComparingLong(WorkoutExercise::getId))
				.map(we -> new WorkoutExerciseResponse(
						we.getId(),
						we.getExerciseId(),
						we.getSets(),
						we.getReps(),
						we.getRestSeconds(),
						we.getSortOrder(),
						toRepsResponse(we),
						toWeightsResponse(we)))
				.toList();
		return new WorkoutDetailResponse(
				w.getId(),
				w.getName(),
				w.getDescription(),
				w.getCreatedAt(),
				ex);
	}

	private static List<Integer> toRepsResponse(WorkoutExercise we) {
		int n = we.getSets();
		List<Integer> raw = we.getSetReps();
		int fb = we.getReps();
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

	private static List<Double> toWeightsResponse(WorkoutExercise we) {
		int n = we.getSets();
		List<Double> out = new ArrayList<>(n);
		List<BigDecimal> raw = we.getSetWeightsKg();
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
