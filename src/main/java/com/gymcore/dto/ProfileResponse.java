package com.gymcore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileResponse(
		String email,
		String username,
		List<AchievementDto> achievements,
		List<PersonalRecordResponse> personalRecords,
		List<BodyMetricResponse> bodyMetrics,
		AuthResponse session
) {
}
