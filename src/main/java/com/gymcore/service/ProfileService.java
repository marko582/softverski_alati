package com.gymcore.service;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ProfileService {

	private static final Pattern EMAIL_LOOSE = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

	private final UserRepository userRepository;
	private final AuthService authService;
	private final PasswordEncoder passwordEncoder;
	private final UserPersonalRecordRepository personalRecordRepository;
	private final UserBodyMetricRepository bodyMetricRepository;
	private final AchievementService achievementService;

	public ProfileService(
			UserRepository userRepository,
			AuthService authService,
			PasswordEncoder passwordEncoder,
			UserPersonalRecordRepository personalRecordRepository,
			UserBodyMetricRepository bodyMetricRepository,
			AchievementService achievementService) {
		this.userRepository = userRepository;
		this.authService = authService;
		this.passwordEncoder = passwordEncoder;
		this.personalRecordRepository = personalRecordRepository;
		this.bodyMetricRepository = bodyMetricRepository;
		this.achievementService = achievementService;
	}

	@Transactional(readOnly = true)
	public ProfileResponse getProfile() {
		User user = currentUser();
		return buildProfile(user, null);
	}

	@Transactional
	public ProfileResponse updateProfile(ProfileUpdateRequest req, HttpServletResponse response) {
		User user = currentUser();
		boolean touched = false;
		boolean emailChanged = false;

		if (req.username() != null && !req.username().isBlank()) {
			String next = req.username().strip();
			if (!next.equals(user.getDisplayName())) {
				if (userRepository.existsByDisplayNameAndIdNot(next, user.getId())) {
					throw new IllegalArgumentException("Username already in use");
				}
				user.setDisplayName(next);
				touched = true;
			}
		}

		if (req.email() != null && !req.email().isBlank()) {
			String next = req.email().trim().toLowerCase();
			if (!EMAIL_LOOSE.matcher(next).matches()) {
				throw new IllegalArgumentException("Invalid email address");
			}
			if (!next.equalsIgnoreCase(user.getEmail())) {
				if (userRepository.existsByEmailAndIdNot(next, user.getId())) {
					throw new IllegalArgumentException("Email already in use");
				}
				user.setEmail(next);
				emailChanged = true;
				touched = true;
			}
		}

		if (!touched) {
			throw new IllegalArgumentException("Nothing to update");
		}

		userRepository.save(user);

		AuthResponse newSession = null;
		if (emailChanged) {
			newSession = authService.reissueSession(user, response);
		}

		return buildProfile(user, newSession);
	}

	@Transactional
	public void changePassword(PasswordChangeRequest req) {
		User user = currentUser();
		if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("Current password is incorrect");
		}
		user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
		userRepository.save(user);
	}

	@Transactional
	public PersonalRecordResponse addPersonalRecord(PersonalRecordCreateRequest req) {
		User user = currentUser();
		UserPersonalRecord r = new UserPersonalRecord();
		r.setUser(user);
		r.setTitle(req.title().strip());
		r.setWeightKg(toBd(req.weightKg()));
		r.setReps(req.reps());
		r.setNotes(blankToNull(req.notes()));
		personalRecordRepository.save(r);
		return toRecordDto(r);
	}

	@Transactional
	public PersonalRecordResponse updatePersonalRecord(long id, PersonalRecordUpdateRequest req) {
		User user = currentUser();
		UserPersonalRecord r = personalRecordRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Record not found"));
		if (req.title() != null && !req.title().isBlank()) {
			r.setTitle(req.title().strip());
		}
		if (req.weightKg() != null) {
			r.setWeightKg(toBd(req.weightKg()));
		}
		if (req.reps() != null) {
			r.setReps(req.reps());
		}
		if (req.notes() != null) {
			r.setNotes(blankToNull(req.notes()));
		}
		return toRecordDto(r);
	}

	@Transactional
	public void deletePersonalRecord(long id) {
		User user = currentUser();
		UserPersonalRecord r = personalRecordRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Record not found"));
		personalRecordRepository.delete(r);
	}

	@Transactional
	public BodyMetricResponse addBodyMetric(BodyMetricCreateRequest req) {
		User user = currentUser();
		UserBodyMetric m = new UserBodyMetric();
		m.setUser(user);
		m.setWeightKg(toBd(req.weightKg()));
		m.setBodyFatPct(toBd(req.bodyFatPct()));
		m.setChestCm(toBd(req.chestCm()));
		m.setWaistCm(toBd(req.waistCm()));
		m.setHipsCm(toBd(req.hipsCm()));
		m.setArmCm(toBd(req.armCm()));
		m.setNotes(blankToNull(req.notes()));
		bodyMetricRepository.save(m);
		return toMetricDto(m);
	}

	@Transactional
	public void deleteBodyMetric(long id) {
		User user = currentUser();
		UserBodyMetric m = bodyMetricRepository.findByIdAndUser_Id(id, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Measurement not found"));
		bodyMetricRepository.delete(m);
	}

	private ProfileResponse buildProfile(User user, AuthResponse session) {
		Long uid = user.getId();
		var achievements = achievementService.syncAndList(uid);
		List<PersonalRecordResponse> records = personalRecordRepository.findByUser_IdOrderByRecordedAtDesc(uid).stream()
				.map(this::toRecordDto)
				.toList();
		List<BodyMetricResponse> metrics = bodyMetricRepository.findByUser_IdOrderByMeasuredAtDesc(uid).stream()
				.map(this::toMetricDto)
				.toList();
		return new ProfileResponse(user.getEmail(), user.getDisplayName(), achievements, records, metrics, session);
	}

	private PersonalRecordResponse toRecordDto(UserPersonalRecord r) {
		return new PersonalRecordResponse(
				r.getId(),
				r.getTitle(),
				r.getWeightKg() != null ? r.getWeightKg().doubleValue() : null,
				r.getReps(),
				r.getRecordedAt(),
				r.getNotes());
	}

	private BodyMetricResponse toMetricDto(UserBodyMetric m) {
		return new BodyMetricResponse(
				m.getId(),
				m.getMeasuredAt(),
				m.getWeightKg() != null ? m.getWeightKg().doubleValue() : null,
				m.getBodyFatPct() != null ? m.getBodyFatPct().doubleValue() : null,
				m.getChestCm() != null ? m.getChestCm().doubleValue() : null,
				m.getWaistCm() != null ? m.getWaistCm().doubleValue() : null,
				m.getHipsCm() != null ? m.getHipsCm().doubleValue() : null,
				m.getArmCm() != null ? m.getArmCm().doubleValue() : null,
				m.getNotes());
	}

	private static BigDecimal toBd(Double d) {
		if (d == null || Double.isNaN(d)) {
			return null;
		}
		return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_UP);
	}

	private static String blankToNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return s.strip();
	}

	private static User currentUser() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User user)) {
			throw new IllegalStateException("Unauthenticated");
		}
		return user;
	}
}
