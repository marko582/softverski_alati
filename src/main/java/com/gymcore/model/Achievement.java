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

	/** Stable achievement code used in unlock logic. */
	@Id
	@Column(length = 64)
	private String id;

	/** Short badge title. */
	@Column(nullable = false, length = 120)
	private String title;

	/** Longer description of how to earn the badge. */
	@Column(nullable = false, length = 500)
	private String description;

	/** Display order in the achievements list. */
	@Column(name = "sort_order", nullable = false)
	private int sortOrder;
}
