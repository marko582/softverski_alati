package com.gymcore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SessionStartRequest(
		Long workoutId,
		@Size(max = 200) String title,
		List<@Valid SessionItemDraftRequest> items
) {
}
