package com.gymcore.repository;

import com.gymcore.model.CatalogExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatalogExerciseRepository extends JpaRepository<CatalogExercise, Integer> {

	@Query("""
			SELECT DISTINCT e FROM CatalogExercise e
			LEFT JOIN FETCH e.bodyParts
			ORDER BY e.id
			""")
	List<CatalogExercise> findAllWithBodyParts();

	@Query(value = """
			SELECT DISTINCT e.id, e.name, e.image_url, e.video_url, e.exercise_type, e.difficulty, e.overview
			FROM exercises e
			LEFT JOIN exercise_body_parts ebp ON e.id = ebp.exercise_id
			LEFT JOIN body_parts bp ON bp.id = ebp.body_part_id
			LEFT JOIN exercise_equipments ee ON e.id = ee.exercise_id
			LEFT JOIN equipments eq ON eq.id = ee.equipment_id
			WHERE (CAST(:bodyPart AS text) IS NULL OR LOWER(bp.name) = LOWER(CAST(:bodyPart AS text)))
			  AND (CAST(:equipment AS text) IS NULL OR LOWER(eq.name) = LOWER(CAST(:equipment AS text)))
			  AND (CAST(:difficulty AS text) IS NULL OR LOWER(e.difficulty) = LOWER(CAST(:difficulty AS text)))
			ORDER BY e.id
			""", nativeQuery = true)
	List<Object[]> filterRows(
			@Param("bodyPart") String bodyPart,
			@Param("equipment") String equipment,
			@Param("difficulty") String difficulty);
}
