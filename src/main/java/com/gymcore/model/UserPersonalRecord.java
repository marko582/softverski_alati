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

	/** Owner of this record. */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

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
	@Column(length = 500)
	private String notes;
}
