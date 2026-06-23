package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AchievementTest {

	private Achievement validAchievement() {
		Achievement achievement = new Achievement();
		achievement.setId("first_workout");
		achievement.setTitle("First workout");
		achievement.setDescription("Create a workout");
		achievement.setSortOrder(1);
		return achievement;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		Achievement achievement = new Achievement();
		achievement.setId("first_workout");

		assertEquals("first_workout", achievement.getId());
	}

	@Test
	@DisplayName("Should store title through getter after setter")
	void setTitle_getTitle_ReturnsSameValue() {
		Achievement achievement = new Achievement();
		achievement.setTitle("First workout");

		assertEquals("First workout", achievement.getTitle());
	}

	@Test
	@DisplayName("Should store description through getter after setter")
	void setDescription_getDescription_ReturnsSameValue() {
		Achievement achievement = new Achievement();
		achievement.setDescription("Create a workout");

		assertEquals("Create a workout", achievement.getDescription());
	}

	@Test
	@DisplayName("Should store sort order through getter after setter")
	void setSortOrder_getSortOrder_ReturnsSameValue() {
		Achievement achievement = new Achievement();
		achievement.setSortOrder(5);

		assertEquals(5, achievement.getSortOrder());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validAchievement());
	}

	@Test
	@DisplayName("Should treat achievements with same id as equal regardless of other fields")
	void equals_SameId_ReturnsTrue() {
		Achievement first = new Achievement();
		first.setId("first_workout");
		first.setTitle("First workout");
		Achievement second = new Achievement();
		second.setId("first_workout");
		second.setTitle("Another title");

		assertEquals(first, second);
	}

	@Test
	@DisplayName("Should not treat achievements with different ids as equal")
	void equals_DifferentId_ReturnsFalse() {
		Achievement first = new Achievement();
		first.setId("first_workout");
		Achievement second = new Achievement();
		second.setId("second_workout");

		assertNotEquals(first, second);
	}

	@Test
	@DisplayName("Should not be equal to null")
	void equals_Null_ReturnsFalse() {
		Achievement achievement = new Achievement();
		achievement.setId("first_workout");

		assertNotEquals(null, achievement);
	}

	@Test
	@DisplayName("Should not be equal to object of different type")
	void equals_DifferentType_ReturnsFalse() {
		Achievement achievement = new Achievement();
		achievement.setId("first_workout");

		assertNotEquals(achievement, "first_workout");
	}

	@Test
	@DisplayName("Should produce same hash code for achievements with same id")
	void hashCode_SameId_ReturnsSameValue() {
		Achievement first = new Achievement();
		first.setId("first_workout");
		Achievement second = new Achievement();
		second.setId("first_workout");

		assertEquals(first.hashCode(), second.hashCode());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("Should reject blank achievement id")
	void validate_IdBlank_HasViolation(String id) {
		Achievement achievement = validAchievement();
		achievement.setId(id);

		assertViolationOn(achievement, "id");
	}

	@Test
	@DisplayName("Should reject achievement id longer than 64 characters")
	void validate_IdTooLong_HasViolation() {
		Achievement achievement = validAchievement();
		achievement.setId("a".repeat(65));

		assertViolationOn(achievement, "id");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("Should reject blank achievement title")
	void validate_TitleBlank_HasViolation(String title) {
		Achievement achievement = validAchievement();
		achievement.setTitle(title);

		assertViolationOn(achievement, "title");
	}

	@Test
	@DisplayName("Should reject achievement title longer than 120 characters")
	void validate_TitleTooLong_HasViolation() {
		Achievement achievement = validAchievement();
		achievement.setTitle("a".repeat(121));

		assertViolationOn(achievement, "title");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("Should reject blank achievement description")
	void validate_DescriptionBlank_HasViolation(String description) {
		Achievement achievement = validAchievement();
		achievement.setDescription(description);

		assertViolationOn(achievement, "description");
	}

	@Test
	@DisplayName("Should reject achievement description longer than 500 characters")
	void validate_DescriptionTooLong_HasViolation() {
		Achievement achievement = validAchievement();
		achievement.setDescription("a".repeat(501));

		assertViolationOn(achievement, "description");
	}

	@ParameterizedTest
	@ValueSource(ints = { -1, -100 })
	@DisplayName("Should reject negative sort order")
	void validate_SortOrderNegative_HasViolation(int sortOrder) {
		Achievement achievement = validAchievement();
		achievement.setSortOrder(sortOrder);

		assertViolationOn(achievement, "sortOrder");
	}
}
