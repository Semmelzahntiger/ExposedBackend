CREATE TABLE app.users (
                           userid UUID PRIMARY KEY,
                           email text NOT NULL,
                           username varchar(15) NOT NULL,
                           password text NOT NULL,
                           authorities text[] NOT NULL
);
CREATE TABLE app.refresh_tokens (
                                token_id UUID PRIMARY KEY,
                                userid UUID NOT NULL,
                                token text NOT NULL,
                                expiration_date DATE NOT NULL,
                                revoke_date DATE,
                                FOREIGN KEY (userid) REFERENCES app.users(userid) ON DELETE CASCADE
);
CREATE TABLE app.entries(
                            entry_id BIGSERIAL PRIMARY KEY,
                            user_id UUID NOT NULL REFERENCES app.users(userid) ON DELETE CASCADE,
                            platform TEXT NOT NULL,
                            data_type TEXT NOT NULL,
                            timestamp DATE NOT NULL,
                            ref TEXT NOT NULL
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_entries_user_data_platform ON entries (user_id, platform, data_type);