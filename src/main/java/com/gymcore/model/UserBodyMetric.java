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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Body measurement snapshot for a user (weight, body fat, circumferences).
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "user_body_metrics")
@Getter
@Setter
public class UserBodyMetric {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** User who logged the measurement. */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** When the measurement was taken. */
	@NotNull
	@Column(name = "measured_at", nullable = false)
	private Instant measuredAt = Instant.now();

	/** Body weight in kilograms. */
	@DecimalMin("0.01")
	@DecimalMax("9999.99")
	@Column(name = "weight_kg", precision = 6, scale = 2)
	private BigDecimal weightKg;

	/** Body fat percentage. */
	@DecimalMin("0")
	@DecimalMax("100")
	@Column(name = "body_fat_pct", precision = 4, scale = 2)
	private BigDecimal bodyFatPct;

	/** Chest circumference in centimeters. */
	@DecimalMin("0.01")
	@DecimalMax("9999.99")
	@Column(name = "chest_cm", precision = 6, scale = 2)
	private BigDecimal chestCm;

	/** Waist circumference in centimeters. */
	@DecimalMin("0.01")
	@DecimalMax("9999.99")
	@Column(name = "waist_cm", precision = 6, scale = 2)
	private BigDecimal waistCm;

	/** Hip circumference in centimeters. */
	@DecimalMin("0.01")
	@DecimalMax("9999.99")
	@Column(name = "hips_cm", precision = 6, scale = 2)
	private BigDecimal hipsCm;

	/** Arm circumference in centimeters. */
	@DecimalMin("0.01")
	@DecimalMax("9999.99")
	@Column(name = "arm_cm", precision = 6, scale = 2)
	private BigDecimal armCm;

	/** Optional free-text notes. */
	@Size(max = 500)
	@Column(length = 500)
	private String notes;
}
