package com.gymcore.controller;

import com.gymcore.dto.SessionDetailResponse;
import com.gymcore.service.WorkoutSessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

	private final WorkoutSessionService sessionService;

	public DashboardController(WorkoutSessionService sessionService) {
		this.sessionService = sessionService;
	}

	/**
	 * Completed sessions with line items since {@code since} (inclusive), newest first.
	 * Used for training volume / body heatmap without N+1 client calls.
	 */
	@GetMapping("/recent-sessions")
	public List<SessionDetailResponse> recentSessions(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
		return sessionService.listCompletedDetailsSince(since);
	}
}
