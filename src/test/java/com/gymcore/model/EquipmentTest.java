package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EquipmentTest {

	private Equipment validEquipment() {
		Equipment equipment = new Equipment();
		equipment.setName("Barbell");
		return equipment;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		Equipment equipment = new Equipment();
		equipment.setId(3);

		assertEquals(3, equipment.getId());
	}

	@Test
	@DisplayName("Should store name through getter after setter")
	void setName_getName_ReturnsSameValue() {
		Equipment equipment = new Equipment();
		equipment.setName("Barbell");

		assertEquals("Barbell", equipment.getName());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validEquipment());
	}

	@Test
	@DisplayName("Should treat equipment with same id as equal regardless of name")
	void equals_SameId_ReturnsTrue() {
		Equipment first = new Equipment();
		first.setId(3);
		first.setName("Barbell");
		Equipment second = new Equipment();
		second.setId(3);
		second.setName("Dumbbell");

		assertEquals(first, second);
	}

	@Test
	@DisplayName("Should not treat equipment with different ids as equal")
	void equals_DifferentId_ReturnsFalse() {
		Equipment first = new Equipment();
		first.setId(3);
		Equipment second = new Equipment();
		second.setId(4);

		assertNotEquals(first, second);
	}

	@Test
	@DisplayName("Should not be equal to null")
	void equals_Null_ReturnsFalse() {
		Equipment equipment = new Equipment();
		equipment.setId(3);

		assertNotEquals(null, equipment);
	}

	@Test
	@DisplayName("Should not be equal to object of different type")
	void equals_DifferentType_ReturnsFalse() {
		Equipment equipment = new Equipment();
		equipment.setId(3);

		assertNotEquals(equipment, "Barbell");
	}

	@Test
	@DisplayName("Should produce same hash code for equipment with same id")
	void hashCode_SameId_ReturnsSameValue() {
		Equipment first = new Equipment();
		first.setId(3);
		Equipment second = new Equipment();
		second.setId(3);

		assertEquals(first.hashCode(), second.hashCode());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("Should reject blank equipment name")
	void validate_NameBlank_HasViolation(String name) {
		Equipment equipment = validEquipment();
		equipment.setName(name);

		assertViolationOn(equipment, "name");
	}

	@Test
	@DisplayName("Should reject equipment name longer than 100 characters")
	void validate_NameTooLong_HasViolation() {
		Equipment equipment = validEquipment();
		equipment.setName("a".repeat(101));

		assertViolationOn(equipment, "name");
	}
}
