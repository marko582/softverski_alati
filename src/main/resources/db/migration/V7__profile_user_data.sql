CREATE TABLE user_personal_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title VARCHAR(120) NOT NULL,
    weight_kg NUMERIC(8, 2),
    reps INTEGER,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    notes VARCHAR(500)
);

CREATE TABLE user_body_metrics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    measured_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    weight_kg NUMERIC(6, 2),
    body_fat_pct NUMERIC(4, 2),
    chest_cm NUMERIC(6, 2),
    waist_cm NUMERIC(6, 2),
    hips_cm NUMERIC(6, 2),
    arm_cm NUMERIC(6, 2),
    notes VARCHAR(500)
);

CREATE INDEX idx_user_personal_records_user ON user_personal_records (user_id);
CREATE INDEX idx_user_body_metrics_user_measured ON user_body_metrics (user_id, measured_at DESC);
