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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * One exercise line within an active or completed {@link WorkoutSession}.
 * Tracks planned and completed sets, reps, and weights.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "workout_session_items")
@Getter
@Setter
public class WorkoutSessionItem {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Parent workout session. */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "session_id", nullable = false)
	private WorkoutSession session;

	/** Catalog exercise identifier. */
	@Column(name = "exercise_id", nullable = false)
	private Long exerciseId;

	/** Display order within the session. */
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	/** Total sets planned for this exercise. */
	@Column(name = "sets_planned", nullable = false)
	private int setsPlanned;

	/** Default reps per set when no per-set override exists. */
	@Column(name = "reps_planned", nullable = false)
	private int repsPlanned;

	/** Number of sets completed so far. */
	@Column(name = "sets_done", nullable = false)
	private int setsDone;

	/** Per-set weight values in kilograms (JSON array). */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "set_weights_kg", nullable = false, columnDefinition = "jsonb")
	private List<BigDecimal> setWeightsKg = new ArrayList<>();

	/** Per-set rep targets (JSON array). */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "set_reps_planned", nullable = false, columnDefinition = "jsonb")
	private List<Integer> setRepsPlanned = new ArrayList<>();

	public List<BigDecimal> getSetWeightsKg() {
		return setWeightsKg;
	}

	public void setSetWeightsKg(List<BigDecimal> setWeightsKg) {
		this.setWeightsKg = setWeightsKg != null ? setWeightsKg : new ArrayList<>();
	}

	public List<Integer> getSetRepsPlanned() {
		return setRepsPlanned;
	}

	public void setSetRepsPlanned(List<Integer> setRepsPlanned) {
		this.setRepsPlanned = setRepsPlanned != null ? setRepsPlanned : new ArrayList<>();
	}
}
