CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category BIGINT,
    initiator BIGINT,
    description VARCHAR(7000) NOT NULL,
    location_lat DOUBLE PRECISION NOT NULL,
    location_lon DOUBLE PRECISION NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INT NOT NULL,
    request_moderation BOOLEAN NOT NULL,
    event_date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL,
    published_on TIMESTAMP,
    state VARCHAR(50) NOT NULL,
    title VARCHAR(120) NOT NULL
);