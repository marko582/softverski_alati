CREATE TABLE achievements (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(500) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE user_achievements (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    achievement_id VARCHAR(64) NOT NULL REFERENCES achievements (id) ON DELETE CASCADE,
    unlocked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, achievement_id)
);

CREATE INDEX idx_user_achievements_user ON user_achievements (user_id);

INSERT INTO achievements (id, title, description, sort_order) VALUES
    ('first_workout', 'First template', 'Save your first workout template.', 10),
    ('first_finish', 'Session finisher', 'Complete a workout session.', 20),
    ('sessions_5', 'Five done', 'Complete 5 sessions.', 30),
    ('sessions_10', 'Ten strong', 'Complete 10 sessions.', 40),
    ('library_5', 'Program builder', 'Create 5 workout templates.', 50),
    ('custom_runner', 'Freestyle', 'Start a custom session (no template).', 60),
    ('pr_logged', 'PR board', 'Log a personal record.', 70),
    ('body_logged', 'Track the tape', 'Add a body measurement entry.', 80),
    ('veteran', 'Veteran', 'Log 20 sessions (any state).', 90);
