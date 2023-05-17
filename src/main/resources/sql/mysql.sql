/* Plugin Name: MyMCServer */

-- Tables Declaration
-- Create the table "users", where are stored the player data
CREATE TABLE IF NOT EXISTS remote_users (
    user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    username TEXT NOT NULL UNIQUE,
    sha512password TEXT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    token_expiration_date DATETIME NOT NULL
);