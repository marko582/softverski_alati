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

@Entity
@Table(name = "workout_session_items")
@Getter
@Setter
public class WorkoutSessionItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "session_id", nullable = false)
	private WorkoutSession session;

	@Column(name = "exercise_id", nullable = false)
	private Long exerciseId;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Column(name = "sets_planned", nullable = false)
	private int setsPlanned;

	@Column(name = "reps_planned", nullable = false)
	private int repsPlanned;

	@Column(name = "sets_done", nullable = false)
	private int setsDone;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "set_weights_kg", nullable = false, columnDefinition = "jsonb")
	private List<BigDecimal> setWeightsKg = new ArrayList<>();

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
