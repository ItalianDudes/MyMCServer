/* Plugin Name: MyMCServer */

-- Tables Declaration
-- Create the table "users", where are stored the player data
CREATE TABLE IF NOT EXISTS remote_users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    creation_date DATETIME NOT NULL DEFAULT(CURRENT_TIMESTAMP)
);