package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BodyPartTest {

	@Test
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		BodyPart bodyPart = new BodyPart();
		bodyPart.setId(2);
		bodyPart.setName("Back");

		assertAll(
				() -> assertEquals(2, bodyPart.getId()),
				() -> assertEquals("Back", bodyPart.getName())
		);
	}
}
