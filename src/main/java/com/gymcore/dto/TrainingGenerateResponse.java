package com.gymcore.dto;

public class TrainingGenerateResponse {

	private final String plan;

	public TrainingGenerateResponse(String plan) {
		this.plan = plan;
	}

	public String getPlan() {
		return plan;
	}
}
