package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		Equipment equipment = new Equipment();
		equipment.setId(3);
		equipment.setName("Barbell");

		assertAll(
				() -> assertEquals(3, equipment.getId()),
				() -> assertEquals("Barbell", equipment.getName())
		);
	}
}
