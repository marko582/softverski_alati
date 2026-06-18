package com.gymcore.controller;

import com.gymcore.dto.SessionDetailResponse;
import com.gymcore.dto.SessionItemPatchRequest;
import com.gymcore.dto.SessionStartRequest;
import com.gymcore.dto.SessionSummaryResponse;
import com.gymcore.service.WorkoutSessionService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
public class WorkoutSessionController {

	private final WorkoutSessionService sessionService;

	public WorkoutSessionController(WorkoutSessionService sessionService) {
		this.sessionService = sessionService;
	}

	@GetMapping
	public List<SessionSummaryResponse> list() {
		return sessionService.listMine();
	}

	@GetMapping("/{id}")
	public SessionDetailResponse get(@PathVariable long id) {
		return sessionService.getMine(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SessionDetailResponse start(@Valid @RequestBody SessionStartRequest body) {
		return sessionService.start(body);
	}

	@PatchMapping("/{sessionId}/items/{itemId}")
	public SessionDetailResponse patchItem(
			@PathVariable long sessionId,
			@PathVariable long itemId,
			@Valid @RequestBody SessionItemPatchRequest body) {
		return sessionService.patchItem(sessionId, itemId, body);
	}

	@PostMapping("/{id}/complete")
	public SessionDetailResponse complete(@PathVariable long id) {
		return sessionService.complete(id);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable long id) {
		sessionService.deleteMine(id);
	}
}
