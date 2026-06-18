package com.gymcore.service;

import com.gymcore.dto.EquipmentResponse;
import com.gymcore.dto.ExerciseResponse;
import com.gymcore.dto.InstructionResponse;
import com.gymcore.model.BodyPart;
import com.gymcore.model.CatalogExercise;
import com.gymcore.repository.CatalogExerciseRepository;
import com.gymcore.repository.EquipmentRepository;
import com.gymcore.repository.InstructionRepository;
import com.gymcore.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExerciseCatalogService {

	private final CatalogExerciseRepository exerciseRepository;
	private final EquipmentRepository equipmentRepository;
	private final InstructionRepository instructionRepository;

	public ExerciseCatalogService(
			CatalogExerciseRepository exerciseRepository,
			EquipmentRepository equipmentRepository,
			InstructionRepository instructionRepository) {
		this.exerciseRepository = exerciseRepository;
		this.equipmentRepository = equipmentRepository;
		this.instructionRepository = instructionRepository;
	}

	public List<ExerciseResponse> findAll() {
		return exerciseRepository.findAllWithBodyParts().stream()
				.map(ex -> toResponse(ex, true))
				.toList();
	}

	public List<ExerciseResponse> filter(String bodyPart, String equipment, String difficulty) {
		String bp = blankToNull(bodyPart);
		String eq = blankToNull(equipment);
		String diff = blankToNull(difficulty);
		return exerciseRepository.filterRows(bp, eq, diff).stream()
				.map(row -> new ExerciseResponse(
						((Number) row[0]).intValue(),
						(String) row[1],
						(String) row[2],
						(String) row[3],
						(String) row[4],
						(String) row[5],
						(String) row[6],
						null))
				.toList();
	}

	public ExerciseResponse findById(int id) {
		CatalogExercise ex = exerciseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
		return toResponse(ex, false);
	}

	public List<InstructionResponse> findInstructionsByExerciseId(int exerciseId) {
		if (!exerciseRepository.existsById(exerciseId)) {
			throw new ResourceNotFoundException("Exercise not found");
		}
		return instructionRepository.findByExercise_IdOrderByStepNumberAsc(exerciseId).stream()
				.map(ins -> new InstructionResponse(ins.getId(), exerciseId, ins.getStepNumber(), ins.getDescription()))
				.toList();
	}

	public List<EquipmentResponse> findAllEquipments() {
		return equipmentRepository.findAll().stream()
				.map(eq -> new EquipmentResponse(eq.getId(), eq.getName()))
				.toList();
	}

	private ExerciseResponse toResponse(CatalogExercise ex, boolean includeBodyParts) {
		List<String> bodyParts = null;
		if (includeBodyParts) {
			bodyParts = ex.getBodyParts().stream()
					.map(BodyPart::getName)
					.sorted(String.CASE_INSENSITIVE_ORDER)
					.distinct()
					.toList();
		}
		return new ExerciseResponse(
				ex.getId(),
				ex.getName(),
				ex.getImageUrl(),
				ex.getVideoUrl(),
				ex.getExerciseType(),
				ex.getDifficulty(),
				ex.getOverview(),
				bodyParts);
	}

	private static String blankToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}
