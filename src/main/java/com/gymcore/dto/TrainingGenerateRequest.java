package com.gymcore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TrainingGenerateRequest {

	@NotBlank
	@Size(max = 600)
	private String goal;

	@Min(1)
	@Max(7)
	private int daysPerWeek;

	@NotBlank
	@Pattern(regexp = "beginner|intermediate|advanced", flags = Pattern.Flag.CASE_INSENSITIVE)
	private String experience;

	@Size(max = 400)
	private String equipment;

	@Min(20)
	@Max(180)
	private Integer sessionMinutes;

	@Size(max = 600)
	private String constraints;

	@Pattern(regexp = "en|sr")
	private String language = "en";

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public int getDaysPerWeek() {
		return daysPerWeek;
	}

	public void setDaysPerWeek(int daysPerWeek) {
		this.daysPerWeek = daysPerWeek;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getEquipment() {
		return equipment;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public Integer getSessionMinutes() {
		return sessionMinutes;
	}

	public void setSessionMinutes(Integer sessionMinutes) {
		this.sessionMinutes = sessionMinutes;
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
