package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AchievementTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		Achievement achievement = new Achievement();
		achievement.setId("first_workout");
		achievement.setTitle("First workout");
		achievement.setDescription("Create a workout");
		achievement.setSortOrder(1);

		assertAll(
				() -> assertEquals("first_workout", achievement.getId()),
				() -> assertEquals("First workout", achievement.getTitle()),
				() -> assertEquals(1, achievement.getSortOrder())
		);
	}
}
