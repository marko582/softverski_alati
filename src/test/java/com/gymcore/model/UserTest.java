package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static com.gymcore.model.support.ModelValidationSupport.assertValid;
import static com.gymcore.model.support.ModelValidationSupport.assertViolationOn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

	private User validUser() {
		User user = new User();
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setPasswordHash("encoded-hash");
		return user;
	}

	@Test
	@DisplayName("Should store id through getter after setter")
	void setId_getId_ReturnsSameValue() {
		User user = new User();
		user.setId(1L);

		assertEquals(1L, user.getId());
	}

	@Test
	@DisplayName("Should store display name through getter after setter")
	void setDisplayName_getDisplayName_ReturnsSameValue() {
		User user = new User();
		user.setDisplayName("marko582");

		assertEquals("marko582", user.getDisplayName());
	}

	@Test
	@DisplayName("Should store email through getter after setter")
	void setEmail_getEmail_ReturnsSameValue() {
		User user = new User();
		user.setEmail("marko@example.com");

		assertEquals("marko@example.com", user.getEmail());
	}

	@Test
	@DisplayName("Should store active flag through getter after setter")
	void setActive_isActive_ReturnsSameValue() {
		User user = new User();
		user.setActive(true);

		assertTrue(user.isActive());
	}

	@Test
	@DisplayName("Should use email as Spring Security username")
	void getUsername_ReturnsEmail() {
		User user = new User();
		user.setEmail("marko@example.com");

		assertEquals("marko@example.com", user.getUsername());
	}

	@Test
	@DisplayName("Should expose password hash through UserDetails password accessor")
	void getPassword_ReturnsPasswordHash() {
		User user = new User();
		user.setPasswordHash("encoded");

		assertEquals("encoded", user.getPassword());
	}

	@Test
	@DisplayName("Should lock account when active flag is false")
	void isAccountNonLocked_ReflectsActiveFlag() {
		User activeUser = new User();
		activeUser.setActive(true);
		User inactiveUser = new User();
		inactiveUser.setActive(false);

		assertTrue(activeUser.isAccountNonLocked());
		assertFalse(inactiveUser.isAccountNonLocked());
	}

	@Test
	@DisplayName("Should grant ROLE_USER authority")
	void getAuthorities_ContainsRoleUser() {
		User user = new User();

		assertEquals(1, user.getAuthorities().size());
		assertEquals("ROLE_USER", user.getAuthorities().iterator().next().getAuthority());
	}

	@Test
	@DisplayName("Should set created and updated timestamps on pre-persist")
	void onCreate_SetsTimestamps() {
		User user = new User();
		user.onCreate();

		assertNotNull(user.getCreatedAt());
		assertNotNull(user.getUpdatedAt());
		assertEquals(user.getCreatedAt(), user.getUpdatedAt());
	}

	@Test
	@DisplayName("Should pass validation when all required fields are valid")
	void validate_ValidInstance_HasNoViolations() {
		assertValid(validUser());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "ab", "bad-name", "user name" })
	@DisplayName("Should reject invalid display name")
	void validate_DisplayNameInvalid_HasViolation(String displayName) {
		User user = validUser();
		user.setDisplayName(displayName);

		assertViolationOn(user, "displayName");
	}

	@Test
	@DisplayName("Should reject display name longer than 50 characters")
	void validate_DisplayNameTooLong_HasViolation() {
		User user = validUser();
		user.setDisplayName("a".repeat(51));

		assertViolationOn(user, "displayName");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "not-an-email" })
	@DisplayName("Should reject invalid email")
	void validate_EmailInvalid_HasViolation(String email) {
		User user = validUser();
		user.setEmail(email);

		assertViolationOn(user, "email");
	}

	@Test
	@DisplayName("Should reject email longer than 255 characters")
	void validate_EmailTooLong_HasViolation() {
		User user = validUser();
		user.setEmail("a".repeat(250) + "@example.com");

		assertViolationOn(user, "email");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t" })
	@DisplayName("Should reject invalid password hash")
	void validate_PasswordHashInvalid_HasViolation(String passwordHash) {
		User user = validUser();
		user.setPasswordHash(passwordHash);

		assertViolationOn(user, "passwordHash");
	}

	@Test
	@DisplayName("Should reject password hash longer than 255 characters")
	void validate_PasswordHashTooLong_HasViolation() {
		User user = validUser();
		user.setPasswordHash("a".repeat(256));

		assertViolationOn(user, "passwordHash");
	}

	@Test
	@DisplayName("Should reject refresh token hash longer than 64 characters")
	void validate_RefreshTokenHashTooLong_HasViolation() {
		User user = validUser();
		user.setRefreshTokenHash("a".repeat(65));

		assertViolationOn(user, "refreshTokenHash");
	}
}
