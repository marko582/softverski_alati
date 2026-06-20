package com.gymcore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcore.config.GymcoreProperties;
import com.gymcore.dto.TrainingGenerateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainingGeneratorServiceTest {

	private TrainingGeneratorService trainingGeneratorService;
	private GymcoreProperties properties;

	@BeforeEach
	void setUp() {
		properties = new GymcoreProperties();
		trainingGeneratorService = new TrainingGeneratorService(properties, new ObjectMapper());
	}

	@Test
	@DisplayName("Should throw service unavailable when Ollama integration is disabled")
	void generate_OllamaDisabled_ThrowsServiceUnavailable() {
		properties.getOllama().setEnabled(false);
		TrainingGenerateRequest request = validRequest();

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> trainingGeneratorService.generate(request));

		assertEquals(503, exception.getStatusCode().value());
		assertTrue(exception.getReason().contains("disabled"));
	}

	@Test
	@DisplayName("Should throw service unavailable when Ollama model is blank")
	void generate_BlankModel_ThrowsServiceUnavailable() {
		properties.getOllama().setEnabled(true);
		properties.getOllama().setModel("   ");
		TrainingGenerateRequest request = validRequest();

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> trainingGeneratorService.generate(request));

		assertEquals(503, exception.getStatusCode().value());
		assertTrue(exception.getReason().contains("model"));
	}

	private static TrainingGenerateRequest validRequest() {
		TrainingGenerateRequest request = new TrainingGenerateRequest();
		request.setGoal("Build muscle");
		request.setDaysPerWeek(4);
		request.setExperience("beginner");
		request.setLanguage("en");
		return request;
	}
}
