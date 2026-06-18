ALTER TABLE workout_exercises
    ADD COLUMN IF NOT EXISTS set_weights_kg JSONB NOT NULL DEFAULT '[]'::jsonb;
