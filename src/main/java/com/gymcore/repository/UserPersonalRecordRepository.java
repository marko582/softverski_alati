package com.gymcore.repository;

import com.gymcore.model.UserPersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserPersonalRecordRepository extends JpaRepository<UserPersonalRecord, Long> {

	List<UserPersonalRecord> findByUser_IdOrderByRecordedAtDesc(Long userId);

	long countByUser_Id(Long userId);

	Optional<UserPersonalRecord> findByIdAndUser_Id(Long id, Long userId);

	@Query("select min(r.recordedAt) from UserPersonalRecord r where r.user.id = :uid")
	Optional<Instant> findFirstRecordedAt(@Param("uid") Long userId);
}
