package com.gymcore.repository;

import com.gymcore.model.UserAchievement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

	@EntityGraph(attributePaths = "achievement")
	List<UserAchievement> findByUser_Id(Long userId);

	boolean existsByUser_IdAndAchievement_Id(Long userId, String achievementId);
}
