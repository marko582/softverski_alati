package com.gymcore.repository;

import com.gymcore.model.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

	@EntityGraph(attributePaths = { "workout", "items" })
	Optional<WorkoutSession> findByIdAndUser_Id(Long id, Long userId);

	@EntityGraph(attributePaths = "workout")
	List<WorkoutSession> findByUser_IdOrderByStartedAtDesc(Long userId);

	long countByUser_IdAndCompletedAtIsNotNull(Long userId);

	long countByUser_Id(Long userId);

	boolean existsByUser_IdAndWorkoutIsNull(Long userId);

	List<WorkoutSession> findByUser_IdAndCompletedAtIsNotNullOrderByCompletedAtAsc(Long userId);

	Page<WorkoutSession> findByUser_IdOrderByStartedAtAsc(Long userId, Pageable pageable);

	@Query("select min(s.completedAt) from WorkoutSession s where s.user.id = :uid and s.completedAt is not null")
	Optional<Instant> findFirstCompletedAt(@Param("uid") Long userId);

	@Query("select min(s.startedAt) from WorkoutSession s where s.user.id = :uid and s.workout is null")
	Optional<Instant> findFirstCustomSessionStartedAt(@Param("uid") Long userId);

	@EntityGraph(attributePaths = { "items" })
	@Query("select s from WorkoutSession s where s.user.id = :userId and s.completedAt is not null and s.completedAt >= :since order by s.completedAt desc")
	List<WorkoutSession> findCompletedWithItemsSince(@Param("userId") Long userId, @Param("since") Instant since);
}
