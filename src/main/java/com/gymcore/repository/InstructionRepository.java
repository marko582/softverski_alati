package com.gymcore.repository;

import com.gymcore.model.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructionRepository extends JpaRepository<Instruction, Integer> {

	List<Instruction> findByExercise_IdOrderByStepNumberAsc(int exerciseId);
}
