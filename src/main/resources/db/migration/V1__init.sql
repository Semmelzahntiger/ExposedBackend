CREATE TABLE app.users (
                           userid UUID PRIMARY KEY NOT NULL UNIQUE,
                           email text NOT NULL,
                           username varchar(15) NOT NULL,
                           password text NOT NULL,
                           authorities text[] NOT NULL
);
CREATE TABLE app.refresh_tokens (
                                token_id UUID NOT NULL UNIQUE,
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
)