package com.gymcore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exercise entry in the read-only catalog.
 * Linked to body parts, equipment, and step-by-step instructions.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "exercises")
@Getter
@Setter
public class CatalogExercise {

	/** Unique exercise identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Exercise display name. */
	@Column(nullable = false)
	private String name;

	/** URL or path to a preview image. */
	@Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
	private String imageUrl = "";

	/** URL or path to a exercise video. */
	@Column(name = "video_url", columnDefinition = "TEXT")
	private String videoUrl;

	/** Category such as strength or cardio. */
	@Column(name = "exercise_type", nullable = false, length = 50)
	private String exerciseType = "";

	/** Difficulty level (e.g. beginner, intermediate, advanced). */
	@Column(nullable = false, length = 50)
	private String difficulty = "";

	/** Short overview text. */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String overview = "";

	/** Target muscle groups. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "exercise_body_parts",
			joinColumns = @JoinColumn(name = "exercise_id"),
			inverseJoinColumns = @JoinColumn(name = "body_part_id"))
	private Set<BodyPart> bodyParts = new HashSet<>();

	/** Required or optional equipment. */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "exercise_equipments",
			joinColumns = @JoinColumn(name = "exercise_id"),
			inverseJoinColumns = @JoinColumn(name = "equipment_id"))
	private Set<Equipment> equipments = new HashSet<>();

	/** Ordered execution steps. */
	@OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY)
	@OrderBy("stepNumber ASC")
	private List<Instruction> instructions = new ArrayList<>();
}
