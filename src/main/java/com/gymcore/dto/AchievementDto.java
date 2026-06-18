package com.gymcore.dto;

import java.time.Instant;

public record AchievementDto(
		String id,
		String title,
		String description,
		boolean unlocked,
		Instant unlockedAt
) {
}
