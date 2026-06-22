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

<<<<<<< Updated upstream
	/** Owner of this record. */
=======
	/** Owner of this record. Invalid values: {@code null}. */
	@NotNull
>>>>>>> Stashed changes
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

<<<<<<< Updated upstream
	/** Exercise or lift name. */
	@Column(nullable = false, length = 120)
	private String title;

	/** Weight lifted in kilograms. */
	@Column(name = "weight_kg", precision = 8, scale = 2)
	private BigDecimal weightKg;

	/** Rep count for the record. */
	private Integer reps;

	/** When the record was logged. */
	@Column(name = "recorded_at", nullable = false)
	private Instant recordedAt = Instant.now();

	/** Optional free-text notes. */
=======
	/** Exercise or lift name. Invalid values: null, blank, or length greater than 120. */
	@NotBlank
	@Size(max = 120)
	@Column(nullable = false, length = 120)
	private String title;

	/** Weight lifted in kilograms. Invalid values: less than 0.01 or greater than 999999.99. */
	@DecimalMin("0.01")
	@DecimalMax("999999.99")
	@Column(name = "weight_kg", precision = 8, scale = 2)
	private BigDecimal weightKg;

	/** Rep count for the record. Invalid values: less than 1 or greater than 9999. */
	@Min(1)
	@Max(9999)
	private Integer reps;

	/** When the record was logged. Invalid values: {@code null}. */
	@NotNull
	@Column(name = "recorded_at", nullable = false)
	private Instant recordedAt = Instant.now();

	/** Optional free-text notes. Invalid values: length greater than 500. */
	@Size(max = 500)
>>>>>>> Stashed changes
	@Column(length = 500)
	private String notes;
}
