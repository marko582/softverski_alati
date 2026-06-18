package com.gymcore.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
		int status,
		String error,
		String message,
		Instant timestamp,
		String path
) {
}
