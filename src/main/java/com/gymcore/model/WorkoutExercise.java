package com.gymcore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * One exercise line within a {@link Workout} template.
 * References a catalog exercise by id and stores planned sets, reps, and weights.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "workout_exercises")
@Getter
@Setter
public class WorkoutExercise {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Parent workout template. Invalid values: {@code null}. */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "workout_id", nullable = false)
	private Workout workout;

	/** Exercise identifier. Invalid values: null, zero, or negative. */
	@NotNull
	@Positive
	@Column(name = "exercise_id", nullable = false)
	private Long exerciseId;

	/** Number of planned sets. Invalid values: less than 1 or greater than 99. */
	@Min(1)
	@Max(99)
	@Column(nullable = false)
	private int sets = 3;

	/** Default reps for sets without per-set overrides. Invalid values: less than 1 or greater than 9999. */
	@Min(1)
	@Max(9999)
	@Column(nullable = false)
	private int reps = 10;

	/** Rest between sets in seconds. Invalid values: negative. */
	@Min(0)
	@Column(name = "rest_seconds", nullable = false)
	private int restSeconds = 90;

	/** Display order within the workout. Invalid values: negative. */
	@Min(0)
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	/** Per-set weight values in kilograms (JSON array). Invalid values: {@code null}. */
	@NotNull
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "set_weights_kg", nullable = false, columnDefinition = "jsonb")
	private List<BigDecimal> setWeightsKg = new ArrayList<>();

	/** Per-set rep targets (JSON array). Invalid values: {@code null}. */
	@NotNull
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "set_reps", nullable = false, columnDefinition = "jsonb")
	private List<Integer> setReps = new ArrayList<>();

	public List<BigDecimal> getSetWeightsKg() {
		return setWeightsKg;
	}

	public void setSetWeightsKg(List<BigDecimal> setWeightsKg) {
		this.setWeightsKg = setWeightsKg != null ? setWeightsKg : new ArrayList<>();
	}

	public List<Integer> getSetReps() {
		return setReps;
	}

	public void setSetReps(List<Integer> setReps) {
		this.setReps = setReps != null ? setReps : new ArrayList<>();
	}
}
