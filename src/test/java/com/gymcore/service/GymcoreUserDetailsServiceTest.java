package com.gymcore.service;

import com.gymcore.model.User;
import com.gymcore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymcoreUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private GymcoreUserDetailsService userDetailsService;

	private User sampleUser;

	@BeforeEach
	void setUp() {
		sampleUser = new User();
		sampleUser.setId(7L);
		sampleUser.setEmail("marko@example.com");
		sampleUser.setDisplayName("marko582");
		sampleUser.setPasswordHash("encoded");
		sampleUser.setActive(true);
	}

	@Test
	@DisplayName("Should load user details when email exists")
	void loadUserByUsername_UserExists_ReturnsUserDetails() {
		when(userRepository.findByEmail("marko@example.com")).thenReturn(Optional.of(sampleUser));

		UserDetails result = userDetailsService.loadUserByUsername("marko@example.com");

		assertNotNull(result);
		assertEquals("marko@example.com", result.getUsername());
		assertTrue(result.isEnabled());
		verify(userRepository).findByEmail("marko@example.com");
	}

	@Test
	@DisplayName("Should throw UsernameNotFoundException when email is missing")
	void loadUserByUsername_UserMissing_ThrowsUsernameNotFoundException() {
		when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
				() -> userDetailsService.loadUserByUsername("missing@example.com"));

		assertEquals("User not found", exception.getMessage());
		verify(userRepository).findByEmail("missing@example.com");
	}
}
