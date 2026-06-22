package com.gymcore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Achievement definition (badge) that users can unlock through app activity.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "achievements")
@Getter
@Setter
public class Achievement {

	/** Stable achievement code used in unlock logic. */
	@NotBlank
	@Size(max = 64)
	@Id
	@Column(length = 64)
	private String id;

	/** Short badge title. */
	@NotBlank
	@Size(max = 120)
	@Column(nullable = false, length = 120)
	private String title;

	/** Longer description of how to earn the badge. */
	@NotBlank
	@Size(max = 500)
	@Column(nullable = false, length = 500)
	private String description;

	/** Display order in the achievements list. */
	@PositiveOrZero
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;
}
