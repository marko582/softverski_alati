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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Single step in the execution instructions for a {@link CatalogExercise}.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "instructions")
@Getter
@Setter
public class Instruction {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Parent catalog exercise. Invalid values: {@code null}. */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "exercise_id", nullable = false)
	private CatalogExercise exercise;

	/** Step sequence number starting at 1. Invalid values: less than 1. */
	@Min(1)
	@Column(name = "step_number", nullable = false)
	private int stepNumber;

	/** Instruction text for this step. Invalid values: null or blank. */
	@NotBlank
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;
}
