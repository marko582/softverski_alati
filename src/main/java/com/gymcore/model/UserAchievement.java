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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Links a {@link User} to an unlocked {@link Achievement}.
 * Each user may earn a given achievement at most once.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(
		name = "user_achievements",
		uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "achievement_id" }))
@Getter
@Setter
public class UserAchievement {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

<<<<<<< Updated upstream
	/** User who earned the achievement. */
=======
	/** User who earned the achievement. Invalid values: {@code null}. */
	@NotNull
>>>>>>> Stashed changes
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

<<<<<<< Updated upstream
	/** Achievement that was unlocked. */
=======
	/** Achievement that was unlocked. Invalid values: {@code null}. */
	@NotNull
>>>>>>> Stashed changes
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "achievement_id", nullable = false)
	private Achievement achievement;

<<<<<<< Updated upstream
	/** When the achievement was first earned. */
=======
	/** When the achievement was first earned. Invalid values: {@code null}. */
	@NotNull
>>>>>>> Stashed changes
	@Column(name = "unlocked_at", nullable = false)
	private Instant unlockedAt = Instant.now();
}
