package com.gymcore.controller;

import com.gymcore.dto.TrainingGenerateRequest;
import com.gymcore.dto.TrainingGenerateResponse;
import com.gymcore.service.TrainingGeneratorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/training")
public class TrainingGeneratorController {

	private final TrainingGeneratorService trainingGeneratorService;

	public TrainingGeneratorController(TrainingGeneratorService trainingGeneratorService) {
		this.trainingGeneratorService = trainingGeneratorService;
	}

	@PostMapping("/generate")
	public TrainingGenerateResponse generate(@Valid @RequestBody TrainingGenerateRequest body) {
		return trainingGeneratorService.generate(body);
	}
}
