package com.gymcore.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utility for hashing refresh tokens before persistence.
 * @author Marko Mijailovic (marko582)
 */
public final class TokenHasher {

	private TokenHasher() {
	}

	/**
	 * Computes the SHA-256 digest of the given string as a lowercase hex string.
	 * @param raw the raw token value to hash.
	 * @return hexadecimal SHA-256 digest.
	 */
	public static String sha256Hex(String raw) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
