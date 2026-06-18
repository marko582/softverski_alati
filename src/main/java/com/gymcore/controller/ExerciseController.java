package com.gymcore.controller;

import com.gymcore.dto.EquipmentResponse;
import com.gymcore.dto.ExerciseResponse;
import com.gymcore.dto.InstructionResponse;
import com.gymcore.service.ExerciseCatalogService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ExerciseController {

	private final ExerciseCatalogService catalogService;

	public ExerciseController(ExerciseCatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@GetMapping(value = "/exercises", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ExerciseResponse> allExercises() {
		return catalogService.findAll();
	}

	@GetMapping(value = "/exercises/filter", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ExerciseResponse> filterExercises(
			@RequestParam(required = false) String bodyPart,
			@RequestParam(required = false) String equipments,
			@RequestParam(required = false) String difficulty) {
		return catalogService.filter(bodyPart, equipments, difficulty);
	}

	@GetMapping(value = "/exercises/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ExerciseResponse exerciseById(@PathVariable int id) {
		return catalogService.findById(id);
	}

	@GetMapping(value = "/exercises/{id}/instructions", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<InstructionResponse> instructions(@PathVariable int id) {
		return catalogService.findInstructionsByExerciseId(id);
	}

	@GetMapping(value = "/equipments", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EquipmentResponse> equipments() {
		return catalogService.findAllEquipments();
	}
}
