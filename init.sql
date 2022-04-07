-- TODO: Please implement. Hint: both init.sql files should be the same.
CREATE TABLE IF NOT EXISTS users
(
    id   uuid NOT NULL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS slots
(
    id       uuid NOT NULL PRIMARY KEY,
    title    TEXT NULL,
    start_at TIMESTAMPTZ,
    end_at   TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS meetings
(
    id       uuid NOT NULL PRIMARY KEY,
    title    TEXT NOT NULL,
    start_at TIMESTAMPTZ,
    end_at   TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS users_x_meetings
(
    user_id    UUID NOT NULL REFERENCES users (id),
    meeting_id UUID NOT NULL REFERENCES meetings (id)
);