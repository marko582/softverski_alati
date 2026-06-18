CREATE TABLE workout_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    workout_id BIGINT NOT NULL REFERENCES workouts (id) ON DELETE CASCADE,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ
);

CREATE TABLE workout_session_items (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES workout_sessions (id) ON DELETE CASCADE,
    exercise_id BIGINT NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    sets_planned INTEGER NOT NULL,
    reps_planned INTEGER NOT NULL,
    sets_done INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_workout_sessions_user_started ON workout_sessions (user_id, started_at DESC);
CREATE INDEX idx_workout_session_items_session_id ON workout_session_items (session_id);
