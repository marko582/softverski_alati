package com.gymcore.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

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
	@DisplayName("Should verify Lombok getters and setters")
	void testLombokMethods() {
		User user = new User();
		user.setId(1L);
		user.setDisplayName("marko582");
		user.setEmail("marko@example.com");
		user.setActive(true);

		assertAll(
				() -> assertEquals(1L, user.getId()),
				() -> assertEquals("marko582", user.getDisplayName()),
				() -> assertEquals("marko@example.com", user.getEmail()),
				() -> assertTrue(user.isActive())
		);
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
}
