package com.gymcore.repository;

import com.gymcore.model.UserBodyMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserBodyMetricRepository extends JpaRepository<UserBodyMetric, Long> {

	List<UserBodyMetric> findByUser_IdOrderByMeasuredAtDesc(Long userId);

	long countByUser_Id(Long userId);

	Optional<UserBodyMetric> findByIdAndUser_Id(Long id, Long userId);

	@Query("select min(m.measuredAt) from UserBodyMetric m where m.user.id = :uid")
	Optional<Instant> findFirstMeasuredAt(@Param("uid") Long userId);
}
