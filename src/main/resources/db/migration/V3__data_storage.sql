ALTER TABLE app.users DROP COLUMN tiktok_data, DROP COLUMN instagram_data;
CREATE TABLE app.user_data (
    user_id UUID NOT NULL REFERENCES app.users(userid) ON DELETE CASCADE,
    platform TEXT NOT NULL,
    data_type TEXT NOT NULL,
    data JSONB NOT NULL,
    last_update DATE NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, platform, data_type)
)
