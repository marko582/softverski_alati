package com.gymcore.service;

import com.gymcore.dto.AchievementDto;
import com.gymcore.dto.AuthResponse;
import com.gymcore.dto.BodyMetricCreateRequest;
import com.gymcore.dto.BodyMetricResponse;
import com.gymcore.dto.PasswordChangeRequest;
import com.gymcore.dto.PersonalRecordCreateRequest;
import com.gymcore.dto.PersonalRecordResponse;
import com.gymcore.dto.PersonalRecordUpdateRequest;
import com.gymcore.dto.ProfileResponse;
import com.gymcore.dto.ProfileUpdateRequest;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.model.User;
import com.gymcore.model.UserBodyMetric;
import com.gymcore.model.UserPersonalRecord;
import com.gymcore.repository.UserBodyMetricRepository;
import com.gymcore.repository.UserPersonalRecordRepository;
import com.gymcore.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthService authService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UserPersonalRecordRepository personalRecordRepository;

	@Mock
	private UserBodyMetricRepository bodyMetricRepository;

	@Mock
	private AchievementService achievementService;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private ProfileService profileService;

	private User currentUser;

	@BeforeEach
	void setUp() {
		currentUser = new User();
		currentUser.setId(1L);
		currentUser.setEmail("marko@example.com");
		currentUser.setDisplayName("marko582");
		currentUser.setPasswordHash("encoded");
		currentUser.setActive(true);
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities()));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("Should return profile with achievements, records, and metrics")
	void getProfile_UserExists_ReturnsProfile() {
		when(achievementService.syncAndList(1L)).thenReturn(List.of(
				new AchievementDto("first_workout", "First workout", "Create a workout", true, Instant.now())));
		when(personalRecordRepository.findByUser_IdOrderByRecordedAtDesc(1L)).thenReturn(Collections.emptyList());
		when(bodyMetricRepository.findByUser_IdOrderByMeasuredAtDesc(1L)).thenReturn(Collections.emptyList());

		ProfileResponse result = profileService.getProfile();

		assertEquals("marko@example.com", result.email());
		assertEquals("marko582", result.username());
		assertEquals(1, result.achievements().size());
	}

	@Test
	@DisplayName("Should update username when value is available")
	void updateProfile_NewUsername_UpdatesProfile() {
		when(userRepository.existsByDisplayNameAndIdNot("newname", 1L)).thenReturn(false);
		when(userRepository.save(currentUser)).thenReturn(currentUser);
		when(achievementService.syncAndList(1L)).thenReturn(Collections.emptyList());
		when(personalRecordRepository.findByUser_IdOrderByRecordedAtDesc(1L)).thenReturn(Collections.emptyList());
		when(bodyMetricRepository.findByUser_IdOrderByMeasuredAtDesc(1L)).thenReturn(Collections.emptyList());

		ProfileResponse result = profileService.updateProfile(
				new ProfileUpdateRequest("newname", null),
				response);

		assertEquals("newname", currentUser.getDisplayName());
		assertEquals("newname", result.username());
	}

	@Test
	@DisplayName("Should throw when nothing is provided to update")
	void updateProfile_NoChanges_ThrowsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class,
				() -> profileService.updateProfile(new ProfileUpdateRequest(null, null), response));
	}

	@Test
	@DisplayName("Should change password when current password matches")
	void changePassword_ValidCurrentPassword_UpdatesHash() {
		when(passwordEncoder.matches("OldPass1", "encoded")).thenReturn(true);
		when(passwordEncoder.encode("NewPass1")).thenReturn("new-encoded");
		when(userRepository.save(currentUser)).thenReturn(currentUser);

		profileService.changePassword(new PasswordChangeRequest("OldPass1", "NewPass1"));

		assertEquals("new-encoded", currentUser.getPasswordHash());
		verify(userRepository).save(currentUser);
	}

	@Test
	@DisplayName("Should throw when current password is incorrect")
	void changePassword_WrongCurrentPassword_ThrowsIllegalArgumentException() {
		when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

		assertThrows(IllegalArgumentException.class,
				() -> profileService.changePassword(new PasswordChangeRequest("wrong", "NewPass1")));
	}

	@Test
	@DisplayName("Should add personal record for current user")
	void addPersonalRecord_ValidInput_ReturnsRecord() {
		when(personalRecordRepository.save(any(UserPersonalRecord.class))).thenAnswer(invocation -> {
			UserPersonalRecord record = invocation.getArgument(0);
			record.setId(20L);
			record.setRecordedAt(Instant.now());
			return record;
		});

		PersonalRecordResponse result = profileService.addPersonalRecord(
				new PersonalRecordCreateRequest("Bench press", 100.0, 5, "PR"));

		assertEquals("Bench press", result.title());
		assertEquals(100.0, result.weightKg());
	}

	@Test
	@DisplayName("Should update existing personal record")
	void updatePersonalRecord_RecordExists_ReturnsUpdatedRecord() {
		UserPersonalRecord record = new UserPersonalRecord();
		record.setId(20L);
		record.setUser(currentUser);
		record.setTitle("Squat");
		record.setWeightKg(BigDecimal.valueOf(120));
		record.setReps(3);
		record.setRecordedAt(Instant.now());
		when(personalRecordRepository.findByIdAndUser_Id(20L, 1L)).thenReturn(Optional.of(record));

		PersonalRecordResponse result = profileService.updatePersonalRecord(
				20L,
				new PersonalRecordUpdateRequest("Back squat", 140.0, 2, null));

		assertEquals("Back squat", result.title());
		assertEquals(140.0, result.weightKg());
	}

	@Test
	@DisplayName("Should add body metric for current user")
	void addBodyMetric_ValidInput_ReturnsMetric() {
		when(bodyMetricRepository.save(any(UserBodyMetric.class))).thenAnswer(invocation -> {
			UserBodyMetric metric = invocation.getArgument(0);
			metric.setId(30L);
			metric.setMeasuredAt(Instant.now());
			return metric;
		});

		BodyMetricResponse result = profileService.addBodyMetric(
				new BodyMetricCreateRequest(80.0, 15.0, null, null, null, null, "Morning"));

		assertEquals(80.0, result.weightKg());
		assertEquals(15.0, result.bodyFatPct());
	}

	@Test
	@DisplayName("Should delete body metric owned by current user")
	void deleteBodyMetric_MetricExists_DeletesMetric() {
		UserBodyMetric metric = new UserBodyMetric();
		metric.setId(30L);
		metric.setUser(currentUser);
		when(bodyMetricRepository.findByIdAndUser_Id(30L, 1L)).thenReturn(Optional.of(metric));

		profileService.deleteBodyMetric(30L);

		verify(bodyMetricRepository).delete(metric);
	}

	@Test
	@DisplayName("Should reissue session when email changes")
	void updateProfile_EmailChange_ReissuesSession() {
		when(userRepository.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(false);
		when(userRepository.save(currentUser)).thenReturn(currentUser);
		when(authService.reissueSession(eq(currentUser), eq(response)))
				.thenReturn(new AuthResponse("token", "Bearer", 3600L, "new@example.com", "marko582", 1L));
		when(achievementService.syncAndList(1L)).thenReturn(Collections.emptyList());
		when(personalRecordRepository.findByUser_IdOrderByRecordedAtDesc(1L)).thenReturn(Collections.emptyList());
		when(bodyMetricRepository.findByUser_IdOrderByMeasuredAtDesc(1L)).thenReturn(Collections.emptyList());

		ProfileResponse result = profileService.updateProfile(
				new ProfileUpdateRequest(null, "new@example.com"),
				response);

		assertEquals("new@example.com", currentUser.getEmail());
		assertNotNull(result.session());
		verify(authService).reissueSession(currentUser, response);
	}

	@Test
	@DisplayName("Should throw when personal record is not found")
	void deletePersonalRecord_RecordMissing_ThrowsResourceNotFoundException() {
		when(personalRecordRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> profileService.deletePersonalRecord(99L));
	}
}
