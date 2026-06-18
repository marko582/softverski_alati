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

@Entity
@Table(name = "exercises")
@Getter
@Setter
public class CatalogExercise {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
	private String imageUrl = "";

	@Column(name = "video_url", columnDefinition = "TEXT")
	private String videoUrl;

	@Column(name = "exercise_type", nullable = false, length = 50)
	private String exerciseType = "";

	@Column(nullable = false, length = 50)
	private String difficulty = "";

	@Column(nullable = false, columnDefinition = "TEXT")
	private String overview = "";

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "exercise_body_parts",
			joinColumns = @JoinColumn(name = "exercise_id"),
			inverseJoinColumns = @JoinColumn(name = "body_part_id"))
	private Set<BodyPart> bodyParts = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "exercise_equipments",
			joinColumns = @JoinColumn(name = "exercise_id"),
			inverseJoinColumns = @JoinColumn(name = "equipment_id"))
	private Set<Equipment> equipments = new HashSet<>();

	@OneToMany(mappedBy = "exercise", fetch = FetchType.LAZY)
	@OrderBy("stepNumber ASC")
	private List<Instruction> instructions = new ArrayList<>();
}
