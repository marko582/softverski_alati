-- Exercise catalog (migrated from fitness-api Go service)

CREATE TABLE body_parts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE equipments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE exercises (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_url TEXT NOT NULL DEFAULT '',
    video_url TEXT,
    exercise_type VARCHAR(50) NOT NULL DEFAULT '',
    difficulty VARCHAR(50) NOT NULL DEFAULT '',
    overview TEXT NOT NULL DEFAULT ''
);

CREATE TABLE exercise_body_parts (
    exercise_id INTEGER NOT NULL REFERENCES exercises (id) ON DELETE CASCADE,
    body_part_id INTEGER NOT NULL REFERENCES body_parts (id) ON DELETE CASCADE,
    PRIMARY KEY (exercise_id, body_part_id)
);

CREATE TABLE exercise_equipments (
    exercise_id INTEGER NOT NULL REFERENCES exercises (id) ON DELETE CASCADE,
    equipment_id INTEGER NOT NULL REFERENCES equipments (id) ON DELETE CASCADE,
    PRIMARY KEY (exercise_id, equipment_id)
);

CREATE TABLE instructions (
    id SERIAL PRIMARY KEY,
    exercise_id INTEGER NOT NULL REFERENCES exercises (id) ON DELETE CASCADE,
    step_number INTEGER NOT NULL,
    description TEXT NOT NULL,
    CONSTRAINT uq_instructions_exercise_step UNIQUE (exercise_id, step_number)
);

CREATE INDEX idx_exercise_body_parts_body_part_id ON exercise_body_parts (body_part_id);
CREATE INDEX idx_exercise_equipments_equipment_id ON exercise_equipments (equipment_id);
CREATE INDEX idx_instructions_exercise_id ON instructions (exercise_id);
