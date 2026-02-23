-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    commentator BIGINT NOT NULL,
    published_on TIMESTAMP NOT NULL,
    event BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Индексы для ускорения поиска по событию и комментатору (опционально)
CREATE INDEX IF NOT EXISTS idx_comments_event ON comments(event);
CREATE INDEX IF NOT EXISTS idx_comments_commentator ON comments(commentator);