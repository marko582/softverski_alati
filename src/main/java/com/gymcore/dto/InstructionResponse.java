package com.gymcore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InstructionResponse(
		int id,
		@JsonProperty("exercise_id")
		int exerciseId,
		@JsonProperty("step_number")
		int stepNumber,
		String description
) {
}
