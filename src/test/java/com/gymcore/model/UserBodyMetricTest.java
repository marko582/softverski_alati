package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserBodyMetricTest {

	private User validUser() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		return user;
	}

	private UserBodyMetric validMetric() {
		UserBodyMetric metric = new UserBodyMetric();
		metric.setUser(validUser());
		metric.setMeasuredAt(Instant.parse("2025-04-01T07:00:00Z"));
		metric.setWeightKg(BigDecimal.valueOf(82.5));
		return metric;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		UserBodyMetric metric = new UserBodyMetric();
		metric.setId(30L);

		assertEquals(30L, metric.getId());
	}

	@Test
	@DisplayName("Should store user through getter after setter")
	void setUser_getUser_ReturnsSameValue() {
		User user = validUser();
		UserBodyMetric metric = new UserBodyMetric();
		metric.setUser(user);

		assertEquals(user, metric.getUser());
	}

	@Test
	@DisplayName("Should store measuredAt through getter after setter")
	void setMeasuredAt_getMeasuredAt_ReturnsSameValue() {
		Instant measuredAt = Instant.parse("2025-04-01T07:00:00Z");
		UserBodyMetric metric = new UserBodyMetric();
		metric.setMeasuredAt(measuredAt);

		assertEquals(measuredAt, metric.getMeasuredAt());
	}

	@Test
	@DisplayName("Should store weight through getter after setter")
	void setWeightKg_getWeightKg_ReturnsSameValue() {
		UserBodyMetric metric = new UserBodyMetric();
		metric.setWeightKg(BigDecimal.valueOf(82.5));

		assertEquals(82.5, metric.getWeightKg().doubleValue());
	}

	@Test
	@DisplayName("Should store body fat percentage through getter after setter")
	void setBodyFatPct_getBodyFatPct_ReturnsSameValue() {
		UserBodyMetric metric = new UserBodyMetric();
		metric.setBodyFatPct(BigDecimal.valueOf(14.2));

		assertEquals(14.2, metric.getBodyFatPct().doubleValue());
	}

	@Test
	@DisplayName("Should store notes through getter after setter")
	void setNotes_getNotes_ReturnsSameValue() {
		UserBodyMetric metric = new UserBodyMetric();
		metric.setNotes("Fasted");

		assertEquals("Fasted", metric.getNotes());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validMetric());
	}

	@Test
	@DisplayName("Should reject null user reference")
	void validate_UserNull_HasViolation() {
		UserBodyMetric metric = validMetric();
		metric.setUser(null);

		assertViolationOn(metric, "user");
	}

	@Test
	@DisplayName("Should reject null measuredAt")
	void validate_MeasuredAtNull_HasViolation() {
		UserBodyMetric metric = validMetric();
		metric.setMeasuredAt(null);

		assertViolationOn(metric, "measuredAt");
	}

	@ParameterizedTest
	@ValueSource(doubles = { 0.0, 0.001, 10000.0 })
	@DisplayName("Should reject weight outside allowed range")
	void validate_WeightOutOfRange_HasViolation(double weight) {
		UserBodyMetric metric = validMetric();
		metric.setWeightKg(BigDecimal.valueOf(weight));

		assertViolationOn(metric, "weightKg");
	}

	@ParameterizedTest
	@ValueSource(doubles = { -0.01, 100.01 })
	@DisplayName("Should reject body fat percentage outside allowed range")
	void validate_BodyFatPctOutOfRange_HasViolation(double bodyFatPct) {
		UserBodyMetric metric = validMetric();
		metric.setBodyFatPct(BigDecimal.valueOf(bodyFatPct));

		assertViolationOn(metric, "bodyFatPct");
	}

	@Test
	@DisplayName("Should reject notes longer than 500 characters")
	void validate_NotesTooLong_HasViolation() {
		UserBodyMetric metric = validMetric();
		metric.setNotes("a".repeat(501));

		assertViolationOn(metric, "notes");
	}
}
