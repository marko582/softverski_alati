ALTER TABLE users
    ADD COLUMN refresh_token_hash VARCHAR(64),
    ADD COLUMN refresh_token_expires_at TIMESTAMPTZ;

UPDATE users u
SET
    refresh_token_hash = rt.token_hash,
    refresh_token_expires_at = rt.expires_at
FROM (
    SELECT DISTINCT ON (user_id) user_id, token_hash, expires_at
    FROM refresh_tokens
    ORDER BY user_id, expires_at DESC, id DESC
) rt
WHERE u.id = rt.user_id;

CREATE UNIQUE INDEX idx_users_refresh_token_hash
    ON users (refresh_token_hash)
    WHERE refresh_token_hash IS NOT NULL;

DROP INDEX IF EXISTS idx_refresh_tokens_user_id;
DROP TABLE refresh_tokens;
