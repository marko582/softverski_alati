ALTER TABLE workout_exercises
    ADD COLUMN IF NOT EXISTS set_reps JSONB NOT NULL DEFAULT '[]'::jsonb;

UPDATE workout_exercises we
SET set_reps = (
    SELECT jsonb_agg(we.reps ORDER BY g.i)
    FROM generate_series(1, we.sets) AS g(i)
    )
WHERE jsonb_array_length(we.set_reps) = 0;

ALTER TABLE workout_session_items
    ADD COLUMN IF NOT EXISTS set_reps_planned JSONB NOT NULL DEFAULT '[]'::jsonb;

UPDATE workout_session_items wsi
SET set_reps_planned = (
    SELECT jsonb_agg(wsi.reps_planned ORDER BY g.i)
    FROM generate_series(1, wsi.sets_planned) AS g(i)
    )
WHERE jsonb_array_length(wsi.set_reps_planned) = 0;
