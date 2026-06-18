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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** When the measurement was taken. */
	@Column(name = "measured_at", nullable = false)
	private Instant measuredAt = Instant.now();

	/** Body weight in kilograms. */
	@Column(name = "weight_kg", precision = 6, scale = 2)
	private BigDecimal weightKg;

	/** Body fat percentage. */
	@Column(name = "body_fat_pct", precision = 4, scale = 2)
	private BigDecimal bodyFatPct;

	/** Chest circumference in centimeters. */
	@Column(name = "chest_cm", precision = 6, scale = 2)
	private BigDecimal chestCm;

	/** Waist circumference in centimeters. */
	@Column(name = "waist_cm", precision = 6, scale = 2)
	private BigDecimal waistCm;

	/** Hip circumference in centimeters. */
	@Column(name = "hips_cm", precision = 6, scale = 2)
	private BigDecimal hipsCm;

	/** Arm circumference in centimeters. */
	@Column(name = "arm_cm", precision = 6, scale = 2)
	private BigDecimal armCm;

	/** Optional free-text notes. */
	@Column(length = 500)
	private String notes;
}
