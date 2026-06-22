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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A live or completed gym session for a user.
 * May be started from a {@link Workout} template or as a custom session.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "workout_sessions")
@Getter
@Setter
public class WorkoutSession {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** User who performed this session. Invalid values: {@code null}. */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** Source workout template; null for custom sessions. */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "workout_id")
	private Workout workout;

	/** Session title shown in history. Invalid values: null, blank, or length greater than 255. */
	@NotBlank
	@Size(max = 255)
	@Column(name = "title", nullable = false, length = 255)
	private String title;

	/** When the session was started. */
	@Column(name = "started_at", nullable = false)
	private Instant startedAt;

	/** When the session was marked complete; null while active. */
	@Column(name = "completed_at")
	private Instant completedAt;

	/** Exercise lines tracked during this session. */
	@Valid
	@OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC, id ASC")
	private List<WorkoutSessionItem> items = new ArrayList<>();

	@PrePersist
	void onCreate() {
		if (startedAt == null) {
			startedAt = Instant.now();
		}
	}
}
