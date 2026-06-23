package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAchievementTest {

	private User validUser() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		return user;
	}

	private Achievement validAchievement() {
		Achievement achievement = new Achievement();
		achievement.setId("first_workout");
		achievement.setTitle("First workout");
		achievement.setDescription("Create a workout");
		achievement.setSortOrder(1);
		return achievement;
	}

	private UserAchievement validUserAchievement() {
		UserAchievement userAchievement = new UserAchievement();
		userAchievement.setUser(validUser());
		userAchievement.setAchievement(validAchievement());
		userAchievement.setUnlockedAt(Instant.parse("2025-01-01T12:00:00Z"));
		return userAchievement;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		UserAchievement userAchievement = new UserAchievement();
		userAchievement.setId(1L);

		assertEquals(1L, userAchievement.getId());
	}

	@Test
	@DisplayName("Should store user through getter after setter")
	void setUser_getUser_ReturnsSameValue() {
		User user = validUser();
		UserAchievement userAchievement = new UserAchievement();
		userAchievement.setUser(user);

		assertEquals(user, userAchievement.getUser());
	}

	@Test
	@DisplayName("Should store achievement through getter after setter")
	void setAchievement_getAchievement_ReturnsSameValue() {
		Achievement achievement = validAchievement();
		UserAchievement userAchievement = new UserAchievement();
		userAchievement.setAchievement(achievement);

		assertEquals(achievement, userAchievement.getAchievement());
	}

	@Test
	@DisplayName("Should store unlockedAt through getter after setter")
	void setUnlockedAt_getUnlockedAt_ReturnsSameValue() {
		Instant unlockedAt = Instant.parse("2025-01-01T12:00:00Z");
		UserAchievement userAchievement = new UserAchievement();
		userAchievement.setUnlockedAt(unlockedAt);

		assertEquals(unlockedAt, userAchievement.getUnlockedAt());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validUserAchievement());
	}

	@Test
	@DisplayName("Should reject null user reference")
	void validate_UserNull_HasViolation() {
		UserAchievement userAchievement = validUserAchievement();
		userAchievement.setUser(null);

		assertViolationOn(userAchievement, "user");
	}

	@Test
	@DisplayName("Should reject null achievement reference")
	void validate_AchievementNull_HasViolation() {
		UserAchievement userAchievement = validUserAchievement();
		userAchievement.setAchievement(null);

		assertViolationOn(userAchievement, "achievement");
	}

	@Test
	@DisplayName("Should reject null unlockedAt")
	void validate_UnlockedAtNull_HasViolation() {
		UserAchievement userAchievement = validUserAchievement();
		userAchievement.setUnlockedAt(null);

		assertViolationOn(userAchievement, "unlockedAt");
	}
}
