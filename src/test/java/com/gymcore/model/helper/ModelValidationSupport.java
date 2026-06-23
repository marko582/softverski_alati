package com.gymcore.model.support;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ModelValidationSupport {

	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

	private ModelValidationSupport() {
	}

	public static <T> void assertValid(T object) {
		Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object);
		assertTrue(violations.isEmpty(), () -> "Expected no violations but got: " + violations);
	}

	public static <T> void assertViolationOn(T object, String propertyPath) {
		Set<ConstraintViolation<T>> violations = VALIDATOR.validate(object);
		assertFalse(violations.isEmpty(), "Expected validation failure on " + propertyPath);
		assertTrue(
				violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(propertyPath)),
				() -> "Expected violation on " + propertyPath + " but got: " + violations);
	}
}
