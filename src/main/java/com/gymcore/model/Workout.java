package com.gymcore.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Saved workout template owned by a user.
 * Contains ordered {@link WorkoutExercise} lines and supports soft deletion.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "workouts")
@Getter
@Setter
public class Workout {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

<<<<<<< Updated upstream
	/** Owner of this workout template. */
=======
	/** Owner of this workout template. Invalid values: {@code null}. */
	@NotNull
>>>>>>> Stashed changes
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

<<<<<<< Updated upstream
	/** Workout display name. */
=======
	/** Workout display name. Invalid values: null, blank, or length greater than 100. */
	@NotBlank
	@Size(max = 100)
>>>>>>> Stashed changes
	@Column(nullable = false, length = 100)
	private String name;

	/** Optional longer description. */
	@Column(columnDefinition = "text")
	private String description;

	/** Creation timestamp set on first persist. */
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	/** When true, the workout is hidden from lists but retained in the database. */
	@Column(nullable = false)
	private boolean deleted = false;

	/** Ordered exercise lines belonging to this workout. */
	@OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC, id ASC")
	private List<WorkoutExercise> exercises = new ArrayList<>();

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}
