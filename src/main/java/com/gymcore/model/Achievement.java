package com.gymcore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

<<<<<<< Updated upstream
	/** Stable achievement code used in unlock logic. */
=======
	/** Stable achievement code used in unlock logic. Invalid values: null, blank, or length greater than 64. */
	@NotBlank
	@Size(max = 64)
>>>>>>> Stashed changes
	@Id
	@Column(length = 64)
	private String id;

<<<<<<< Updated upstream
	/** Short badge title. */
	@Column(nullable = false, length = 120)
	private String title;

	/** Longer description of how to earn the badge. */
	@Column(nullable = false, length = 500)
	private String description;

	/** Display order in the achievements list. */
=======
	/** Short badge title. Invalid values: null, blank, or length greater than 120. */
	@NotBlank
	@Size(max = 120)
	@Column(nullable = false, length = 120)
	private String title;

	/** Longer description of how to earn the badge. Invalid values: null, blank, or length greater than 500. */
	@NotBlank
	@Size(max = 500)
	@Column(nullable = false, length = 500)
	private String description;

	/** Display order in the achievements list. Invalid values: negative. */
	@PositiveOrZero
>>>>>>> Stashed changes
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;
}
