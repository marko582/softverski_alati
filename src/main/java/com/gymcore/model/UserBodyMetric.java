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

@Entity
@Table(name = "user_body_metrics")
@Getter
@Setter
public class UserBodyMetric {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "measured_at", nullable = false)
	private Instant measuredAt = Instant.now();

	@Column(name = "weight_kg", precision = 6, scale = 2)
	private BigDecimal weightKg;

	@Column(name = "body_fat_pct", precision = 4, scale = 2)
	private BigDecimal bodyFatPct;

	@Column(name = "chest_cm", precision = 6, scale = 2)
	private BigDecimal chestCm;

	@Column(name = "waist_cm", precision = 6, scale = 2)
	private BigDecimal waistCm;

	@Column(name = "hips_cm", precision = 6, scale = 2)
	private BigDecimal hipsCm;

	@Column(name = "arm_cm", precision = 6, scale = 2)
	private BigDecimal armCm;

	@Column(length = 500)
	private String notes;
}
