CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_entries_user_data_platform ON entries (user_id, platform, data_type);