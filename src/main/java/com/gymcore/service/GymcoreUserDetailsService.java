package com.gymcore.service;

import com.gymcore.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security user-details loader backed by the user repository.
 * @author Marko Mijailovic (marko582)
 */
@Service
public class GymcoreUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public GymcoreUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Loads a user by email address for authentication.
	 * @param email the user's email (Spring Security username).
	 * @return user details for the found account.
	 * @throws UsernameNotFoundException if no user matches the email.
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
