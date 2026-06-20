package com.gymcore.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TokenHasherTest {

	@Test
	@DisplayName("Should produce deterministic SHA-256 hex for the same input")
	void sha256Hex_SameInput_ReturnsSameDigest() {
		String raw = "sample-refresh-token";

		String first = TokenHasher.sha256Hex(raw);
		String second = TokenHasher.sha256Hex(raw);

		assertEquals(first, second);
		assertEquals(64, first.length());
		assertTrue(first.matches("[0-9a-f]+"));
	}

	@ParameterizedTest
	@CsvSource({
			"token-a, token-b",
			"abc, ABC",
			"short, longer-value"
	})
	@DisplayName("Should produce different digests for different inputs")
	void sha256Hex_DifferentInputs_ReturnsDifferentDigests(String firstRaw, String secondRaw) {
		String first = TokenHasher.sha256Hex(firstRaw);
		String second = TokenHasher.sha256Hex(secondRaw);

		assertNotEquals(first, second);
	}

	@Test
	@DisplayName("Should match known SHA-256 digest for empty string")
	void sha256Hex_EmptyString_ReturnsKnownDigest() {
		String digest = TokenHasher.sha256Hex("");

		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", digest);
	}
}
