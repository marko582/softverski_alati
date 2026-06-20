package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserAchievementTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		User user = new User();
		Achievement achievement = new Achievement();
		achievement.setId("first_workout");
		Instant unlockedAt = Instant.parse("2025-01-01T12:00:00Z");

		UserAchievement userAchievement = new UserAchievement();
		userAchievement.setId(1L);
		userAchievement.setUser(user);
		userAchievement.setAchievement(achievement);
		userAchievement.setUnlockedAt(unlockedAt);

		assertAll(
				() -> assertEquals(1L, userAchievement.getId()),
				() -> assertEquals(user, userAchievement.getUser()),
				() -> assertEquals(achievement, userAchievement.getAchievement()),
				() -> assertEquals(unlockedAt, userAchievement.getUnlockedAt())
		);
	}
}
