package com.gymcore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcore.config.GymcoreProperties;
import com.gymcore.dto.TrainingGenerateRequest;
import com.gymcore.dto.TrainingGenerateResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingGeneratorService {

	private static final String SYSTEM_PROMPT = """
			You are an experienced strength and conditioning coach. The user wants a training plan.
			Produce a clear, practical weekly plan they can follow in the gym.
			Include: brief rationale, split by day, main lifts/accessories with sets × reps (or time for cardio/core),
			suggested progression, rest guidance, and a short deload or recovery note if relevant.
			Do not invent medical diagnoses; if they mention injury, suggest conservative alternatives and seeing a professional when needed.
			Format using markdown headings (##), bullet lists, and tables where helpful.
			""";

	private final GymcoreProperties properties;
	private final ObjectMapper objectMapper;

	public TrainingGeneratorService(GymcoreProperties properties, ObjectMapper objectMapper) {
		this.properties = properties;
		this.objectMapper = objectMapper;
	}

	public TrainingGenerateResponse generate(TrainingGenerateRequest req) {
		GymcoreProperties.Ollama ollama = properties.getOllama();
		if (!ollama.isEnabled()) {
			throw new ResponseStatusException(
					org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
					"AI training generator is disabled. Set gymcore.ollama.enabled=true (or OLLAMA_ENABLED=true).");
		}

		String base = normalizeBaseUrl(ollama.getBaseUrl());
		String model = ollama.getModel().trim();
		if (!StringUtils.hasText(model)) {
			throw new ResponseStatusException(
					org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
					"Ollama model is not configured. Set OLLAMA_MODEL (e.g. llama3.2:3b).");
		}

		RestClient client = RestClient.builder()
				.baseUrl(base)
				.requestFactory(ollamaRequestFactory(ollama.getReadTimeoutMs()))
				.build();

		String userContent = buildUserPrompt(req);
		String langNote = "sr".equalsIgnoreCase(req.getLanguage())
				? "Write the entire answer in Serbian (Latin script)."
				: "Write the entire answer in English.";

		List<Map<String, String>> messages = new ArrayList<>();
		messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT + "\n" + langNote));
		messages.add(Map.of("role", "user", "content", userContent));

		Map<String, Object> options = new LinkedHashMap<>();
		options.put("temperature", 0.35);
		options.put("top_p", 0.9);
		options.put("num_predict", 3000);

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("model", model);
		body.put("messages", messages);
		body.put("stream", false);
		body.put("options", options);

		byte[] json;
		try {
			json = objectMapper.writeValueAsBytes(body);
		} catch (Exception e) {
			throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Could not build request");
		}

		String raw = postChat(client, json, base, model);
		String plan = extractAssistantContent(raw);
		if (!StringUtils.hasText(plan)) {
			throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY, "Empty response from Ollama");
		}
		return new TrainingGenerateResponse(plan.trim());
	}

	private String postChat(RestClient client, byte[] json, String baseUrl, String model) {
		try {
			return client.post()
					.uri("/api/chat")
					.contentType(MediaType.APPLICATION_JSON)
					.body(json)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (request, response) -> {
						String err = readBody(response);
						String hint = err.contains("not found") || err.contains("model")
								? " Run: ollama pull " + model
								: "";
						throw new ResponseStatusException(
								org.springframework.http.HttpStatus.BAD_GATEWAY,
								"Ollama error (" + response.getStatusCode() + "): " + truncate(err, 500) + hint);
					})
					.body(String.class);
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception e) {
			throw new ResponseStatusException(
					org.springframework.http.HttpStatus.BAD_GATEWAY,
					"Cannot reach Ollama at " + baseUrl + ". Is it running? Install from https://ollama.com, then: ollama pull "
							+ model + " — " + describeException(e));
		}
	}

	private static SimpleClientHttpRequestFactory ollamaRequestFactory(long readTimeoutMs) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(10_000);
		factory.setReadTimeout((int) Math.max(30_000, readTimeoutMs));
		return factory;
	}

	private static String readBody(org.springframework.http.client.ClientHttpResponse response) {
		try {
			byte[] b = response.getBody().readAllBytes();
			if (b.length > 0) {
				return new String(b);
			}
			return response.getStatusCode().toString();
		} catch (Exception ignored) {
			return "request failed";
		}
	}

	private static String describeException(Throwable e) {
		String msg = e.getMessage();
		if (StringUtils.hasText(msg)) {
			return msg;
		}
		Throwable cause = e.getCause();
		if (cause != null && StringUtils.hasText(cause.getMessage())) {
			return cause.getMessage();
		}
		return e.getClass().getSimpleName();
	}

	private static String normalizeBaseUrl(String base) {
		if (base == null || base.isBlank()) {
			return "http://localhost:11434";
		}
		String b = base.trim();
		if (b.endsWith("/")) {
			return b.substring(0, b.length() - 1);
		}
		return b;
	}

	private static String buildUserPrompt(TrainingGenerateRequest req) {
		StringBuilder sb = new StringBuilder();
		sb.append("Goal: ").append(req.getGoal().trim()).append('\n');
		sb.append("Training days per week: ").append(req.getDaysPerWeek()).append('\n');
		sb.append("Experience: ").append(req.getExperience().toLowerCase()).append('\n');
		if (StringUtils.hasText(req.getEquipment())) {
			sb.append("Available equipment: ").append(req.getEquipment().trim()).append('\n');
		}
		if (req.getSessionMinutes() != null) {
			sb.append("Typical session length (minutes): ").append(req.getSessionMinutes()).append('\n');
		}
		if (StringUtils.hasText(req.getConstraints())) {
			sb.append("Constraints / injuries / preferences: ").append(req.getConstraints().trim()).append('\n');
		}
		sb.append("\nGenerate one coherent weekly plan aligned with these inputs.");
		return sb.toString();
	}

	private String extractAssistantContent(String rawJson) {
		try {
			JsonNode root = objectMapper.readTree(rawJson);
			String content = root.path("message").path("content").asText("");
			if (StringUtils.hasText(content)) {
				return content;
			}
			if (root.has("error")) {
				throw new ResponseStatusException(
						org.springframework.http.HttpStatus.BAD_GATEWAY,
						"Ollama error: " + truncate(root.get("error").asText(""), 600));
			}
		} catch (ResponseStatusException e) {
			throw e;
		} catch (Exception ignored) {
			/* fall through */
		}
		return "";
	}

	private static String truncate(String s, int max) {
		if (s == null) {
			return "";
		}
		return s.length() <= max ? s : s.substring(0, max) + "…";
	}
}
