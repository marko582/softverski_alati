package com.gymcore.controller;

import com.gymcore.dto.WorkoutCreateRequest;
import com.gymcore.dto.WorkoutDetailResponse;
import com.gymcore.dto.WorkoutSummaryResponse;
import com.gymcore.dto.WorkoutUpdateRequest;
import com.gymcore.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

	private final WorkoutService workoutService;

	public WorkoutController(WorkoutService workoutService) {
		this.workoutService = workoutService;
	}

	@GetMapping
	public List<WorkoutSummaryResponse> list() {
		return workoutService.listMine();
	}

	@GetMapping("/{id}")
	public WorkoutDetailResponse get(@PathVariable long id) {
		return workoutService.getMine(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public WorkoutDetailResponse create(@Valid @RequestBody WorkoutCreateRequest body) {
		return workoutService.create(body);
	}

	@PutMapping("/{id}")
	public WorkoutDetailResponse update(@PathVariable long id, @Valid @RequestBody WorkoutUpdateRequest body) {
		return workoutService.update(id, body);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		workoutService.softDelete(id);
	}
}
