package com.gymcore.repository;

import com.gymcore.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, String> {

	List<Achievement> findAllByOrderBySortOrderAsc();
}
