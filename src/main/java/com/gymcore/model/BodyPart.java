package com.gymcore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Muscle group or body region targeted by catalog exercises.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "body_parts")
@Getter
@Setter
public class BodyPart {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/** Body part name; unique across the catalog. */
	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, unique = true, length = 100)
	private String name;
}
