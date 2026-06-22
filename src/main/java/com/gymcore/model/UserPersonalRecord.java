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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * User-logged personal record (e.g. bench press PR).
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "user_personal_records")
@Getter
@Setter
public class UserPersonalRecord {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Owner of this record. */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** Exercise or lift name. */
	@NotBlank
	@Size(max = 120)
	@Column(nullable = false, length = 120)
	private String title;

	/** Weight lifted in kilograms. */
	@DecimalMin("0.01")
	@DecimalMax("999999.99")
	@Column(name = "weight_kg", precision = 8, scale = 2)
	private BigDecimal weightKg;

	/** Rep count for the record. */
	@Min(1)
	@Max(9999)
	private Integer reps;

	/** When the record was logged. */
	@NotNull
	@Column(name = "recorded_at", nullable = false)
	private Instant recordedAt = Instant.now();

	/** Optional free-text notes. */
	@Size(max = 500)
	@Column(length = 500)
	private String notes;
}
