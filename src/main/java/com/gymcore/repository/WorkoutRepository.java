package com.gymcore.repository;

import com.gymcore.model.Workout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

	@EntityGraph(attributePaths = "exercises")
	Optional<Workout> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

	@EntityGraph(attributePaths = "exercises")
	List<Workout> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

	long countByUser_IdAndDeletedFalse(Long userId);

	@Query("select min(w.createdAt) from Workout w where w.user.id = :uid and w.deleted = false")
	Optional<Instant> findOldestWorkoutCreatedAt(@Param("uid") Long userId);

	List<Workout> findByUser_IdAndDeletedFalseOrderByCreatedAtAsc(Long userId);
}
