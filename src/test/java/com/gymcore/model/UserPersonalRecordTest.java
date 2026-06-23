package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserPersonalRecordTest {

	private User validUser() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		return user;
	}

	private UserPersonalRecord validRecord() {
		UserPersonalRecord record = new UserPersonalRecord();
		record.setUser(validUser());
		record.setTitle("Deadlift");
		record.setWeightKg(BigDecimal.valueOf(180));
		record.setReps(3);
		record.setRecordedAt(Instant.parse("2025-03-01T10:00:00Z"));
		return record;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		UserPersonalRecord record = new UserPersonalRecord();
		record.setId(20L);

		assertEquals(20L, record.getId());
	}

	@Test
	@DisplayName("Should store user through getter after setter")
	void setUser_getUser_ReturnsSameValue() {
		User user = validUser();
		UserPersonalRecord record = new UserPersonalRecord();
		record.setUser(user);

		assertEquals(user, record.getUser());
	}

	@Test
	@DisplayName("Should store title through getter after setter")
	void setTitle_getTitle_ReturnsSameValue() {
		UserPersonalRecord record = new UserPersonalRecord();
		record.setTitle("Deadlift");

		assertEquals("Deadlift", record.getTitle());
	}

	@Test
	@DisplayName("Should store weight through getter after setter")
	void setWeightKg_getWeightKg_ReturnsSameValue() {
		UserPersonalRecord record = new UserPersonalRecord();
		record.setWeightKg(BigDecimal.valueOf(180));

		assertEquals(180, record.getWeightKg().intValue());
	}

	@Test
	@DisplayName("Should store reps through getter after setter")
	void setReps_getReps_ReturnsSameValue() {
		UserPersonalRecord record = new UserPersonalRecord();
		record.setReps(3);

		assertEquals(3, record.getReps());
	}

	@Test
	@DisplayName("Should store recordedAt through getter after setter")
	void setRecordedAt_getRecordedAt_ReturnsSameValue() {
		Instant recordedAt = Instant.parse("2025-03-01T10:00:00Z");
		UserPersonalRecord record = new UserPersonalRecord();
		record.setRecordedAt(recordedAt);

		assertEquals(recordedAt, record.getRecordedAt());
	}

	@Test
	@DisplayName("Should store notes through getter after setter")
	void setNotes_getNotes_ReturnsSameValue() {
		UserPersonalRecord record = new UserPersonalRecord();
		record.setNotes("Comp PR");

		assertEquals("Comp PR", record.getNotes());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validRecord());
	}

	@Test
	@DisplayName("Should reject null user reference")
	void validate_UserNull_HasViolation() {
		UserPersonalRecord record = validRecord();
		record.setUser(null);

		assertViolationOn(record, "user");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject blank title")
	void validate_TitleBlank_HasViolation(String title) {
		UserPersonalRecord record = validRecord();
		record.setTitle(title);

		assertViolationOn(record, "title");
	}

	@Test
	@DisplayName("Should reject title longer than 120 characters")
	void validate_TitleTooLong_HasViolation() {
		UserPersonalRecord record = validRecord();
		record.setTitle("a".repeat(121));

		assertViolationOn(record, "title");
	}

	@ParameterizedTest
	@ValueSource(doubles = { 0.0, 0.001, 1000000.0 })
	@DisplayName("Should reject weight outside allowed range")
	void validate_WeightOutOfRange_HasViolation(double weight) {
		UserPersonalRecord record = validRecord();
		record.setWeightKg(BigDecimal.valueOf(weight));

		assertViolationOn(record, "weightKg");
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 10000 })
	@DisplayName("Should reject reps outside allowed range")
	void validate_RepsOutOfRange_HasViolation(int reps) {
		UserPersonalRecord record = validRecord();
		record.setReps(reps);

		assertViolationOn(record, "reps");
	}

	@Test
	@DisplayName("Should reject null recordedAt")
	void validate_RecordedAtNull_HasViolation() {
		UserPersonalRecord record = validRecord();
		record.setRecordedAt(null);

		assertViolationOn(record, "recordedAt");
	}

	@Test
	@DisplayName("Should reject notes longer than 500 characters")
	void validate_NotesTooLong_HasViolation() {
		UserPersonalRecord record = validRecord();
		record.setNotes("a".repeat(501));

		assertViolationOn(record, "notes");
	}
}
