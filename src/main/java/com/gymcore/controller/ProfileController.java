package com.gymcore.controller;

import com.gymcore.dto.BodyMetricCreateRequest;
import com.gymcore.dto.BodyMetricResponse;
import com.gymcore.dto.PasswordChangeRequest;
import com.gymcore.dto.PersonalRecordCreateRequest;
import com.gymcore.dto.PersonalRecordResponse;
import com.gymcore.dto.PersonalRecordUpdateRequest;
import com.gymcore.dto.ProfileResponse;
import com.gymcore.dto.ProfileUpdateRequest;
import com.gymcore.service.ProfileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

	private final ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@GetMapping
	public ProfileResponse get() {
		return profileService.getProfile();
	}

	@PatchMapping
	public ProfileResponse patch(@Valid @RequestBody ProfileUpdateRequest body, HttpServletResponse response) {
		return profileService.updateProfile(body, response);
	}

	@PostMapping("/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void password(@Valid @RequestBody PasswordChangeRequest body) {
		profileService.changePassword(body);
	}

	@PostMapping("/personal-records")
	@ResponseStatus(HttpStatus.CREATED)
	public PersonalRecordResponse addRecord(@Valid @RequestBody PersonalRecordCreateRequest body) {
		return profileService.addPersonalRecord(body);
	}

	@PatchMapping("/personal-records/{id}")
	public PersonalRecordResponse updateRecord(
			@PathVariable long id,
			@Valid @RequestBody PersonalRecordUpdateRequest body) {
		return profileService.updatePersonalRecord(id, body);
	}

	@DeleteMapping("/personal-records/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRecord(@PathVariable long id) {
		profileService.deletePersonalRecord(id);
	}

	@PostMapping("/body-metrics")
	@ResponseStatus(HttpStatus.CREATED)
	public BodyMetricResponse addMetric(@Valid @RequestBody BodyMetricCreateRequest body) {
		return profileService.addBodyMetric(body);
	}

	@DeleteMapping("/body-metrics/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMetric(@PathVariable long id) {
		profileService.deleteBodyMetric(id);
	}
}
