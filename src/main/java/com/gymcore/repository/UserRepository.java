package com.gymcore.repository;

import com.gymcore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByDisplayName(String displayName);

	boolean existsByEmailAndIdNot(String email, Long id);

	boolean existsByDisplayNameAndIdNot(String displayName, Long id);

	Optional<User> findByRefreshTokenHash(String refreshTokenHash);
}
