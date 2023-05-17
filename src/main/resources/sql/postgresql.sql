/* Plugin Name: MyMCServer */

-- Tables Declaration
-- Create the table "users", where are stored the player data
CREATE TABLE IF NOT EXISTS remote_users (
    user_id SERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    sha512password TEXT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    token_expiration_date DATETIME NOT NULL
);