package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BodyPartTest {

	private BodyPart validBodyPart() {
		BodyPart bodyPart = new BodyPart();
		bodyPart.setName("Back");
		return bodyPart;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		BodyPart bodyPart = new BodyPart();
		bodyPart.setId(2);

		assertEquals(2, bodyPart.getId());
	}

	@Test
	@DisplayName("Should store name through getter after setter")
	void setName_getName_ReturnsSameValue() {
		BodyPart bodyPart = new BodyPart();
		bodyPart.setName("Back");

		assertEquals("Back", bodyPart.getName());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validBodyPart());
	}

	@Test
	@DisplayName("Should treat body parts with same id as equal regardless of name")
	void equals_SameId_ReturnsTrue() {
		BodyPart first = new BodyPart();
		first.setId(1);
		first.setName("Back");
		BodyPart second = new BodyPart();
		second.setId(1);
		second.setName("Chest");

		assertEquals(first, second);
	}

	@Test
	@DisplayName("Should not treat body parts with different ids as equal")
	void equals_DifferentId_ReturnsFalse() {
		BodyPart first = new BodyPart();
		first.setId(1);
		BodyPart second = new BodyPart();
		second.setId(2);

		assertNotEquals(first, second);
	}

	@Test
	@DisplayName("Should not be equal to null")
	void equals_Null_ReturnsFalse() {
		BodyPart bodyPart = new BodyPart();
		bodyPart.setId(1);

		assertNotEquals(null, bodyPart);
	}

	@Test
	@DisplayName("Should not be equal to object of different type")
	void equals_DifferentType_ReturnsFalse() {
		BodyPart bodyPart = new BodyPart();
		bodyPart.setId(1);

		assertNotEquals(bodyPart, "Back");
	}

	@Test
	@DisplayName("Should produce same hash code for body parts with same id")
	void hashCode_SameId_ReturnsSameValue() {
		BodyPart first = new BodyPart();
		first.setId(1);
		BodyPart second = new BodyPart();
		second.setId(1);

		assertEquals(first.hashCode(), second.hashCode());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("Should reject blank body part name")
	void validate_NameBlank_HasViolation(String name) {
		BodyPart bodyPart = validBodyPart();
		bodyPart.setName(name);

		assertViolationOn(bodyPart, "name");
	}

	@Test
	@DisplayName("Should reject body part name longer than 100 characters")
	void validate_NameTooLong_HasViolation() {
		BodyPart bodyPart = validBodyPart();
		bodyPart.setName("a".repeat(101));

		assertViolationOn(bodyPart, "name");
	}
}
