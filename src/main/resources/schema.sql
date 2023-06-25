CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(256) NOT NULL,
                                     email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                        description VARCHAR(512) NOT NULL,
                                        requestor_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     owner_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                                     name VARCHAR(256) NOT NULL,
                                     description VARCHAR(512) NOT NULL,
                                     is_available BOOLEAN NOT NULL,
                                     request_id BIGINT REFERENCES requests (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                        start_date TIMESTAMP NOT NULL,
                                        end_date TIMESTAMP NOT NULL,
                                        item_id BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
                                        booker_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                                        status VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                        text VARCHAR(1024) NOT NULL,
                                        item_id BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
                                        user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                                        created TIMESTAMP NOT NULL
);
