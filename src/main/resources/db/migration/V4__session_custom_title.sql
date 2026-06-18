ALTER TABLE workout_sessions ADD COLUMN title VARCHAR(255);

UPDATE workout_sessions ws
SET title = w.name
FROM workouts w
WHERE w.id = ws.workout_id;

ALTER TABLE workout_sessions ALTER COLUMN title SET NOT NULL;

ALTER TABLE workout_sessions ALTER COLUMN workout_id DROP NOT NULL;
