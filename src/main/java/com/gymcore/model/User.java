package com.gymcore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * Application user account implementing Spring Security {@link UserDetails}.
 * Stores credentials, refresh-token state, and profile identifiers.
 * @author Marko Mijailovic (marko582)
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {

	/** Unique identifier. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Display name shown in the UI; unique across users. Invalid values: null, blank, length outside 3–50, or non-alphanumeric characters (except underscore). */
	@NotBlank
	@Size(min = 3, max = 50)
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must be alphanumeric (underscore allowed)")
	@Column(name = "username", nullable = false, unique = true, length = 50)
	private String displayName;

	/** Login email; used as Spring Security username. Invalid values: null, blank, invalid email format, or length greater than 255. */
	@NotBlank
	@Email
	@Size(max = 255)
	@Column(nullable = false, unique = true, length = 255)
	private String email;

	/** BCrypt password hash. Invalid values: null, blank, or length greater than 255. */
	@NotBlank
	@Size(max = 255)
	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	/** Account creation timestamp. */
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	/** Last profile or credential update timestamp. */
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	/** When false, the account is locked and cannot authenticate. */
	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	/** SHA-256 hash of the current refresh token, if any. Invalid values: length greater than 64. */
	@Size(max = 64)
	@Column(name = "refresh_token_hash", length = 64)
	private String refreshTokenHash;

	/** Expiration time of the stored refresh token. */
	@Column(name = "refresh_token_expires_at")
	private Instant refreshTokenExpiresAt;

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return active;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}
}
