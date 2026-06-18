package com.gymcore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExerciseResponse(
		int id,
		String name,
		@JsonProperty("image_url")
		String imageUrl,
		@JsonProperty("video_url")
		String videoUrl,
		@JsonProperty("exercise_type")
		String exerciseType,
		String difficulty,
		String overview,
		@JsonProperty("body_parts")
		List<String> bodyParts
) {
}
