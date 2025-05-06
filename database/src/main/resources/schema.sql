-- Créer la base de données et l'utilisateur
-- CREATE DATABASE wakfu_server;
-- CREATE USER wakfu_user WITH PASSWORD 'votre_mot_de_passe';
-- GRANT ALL PRIVILEGES ON DATABASE wakfu_server TO wakfu_user;

-- Table des comptes
CREATE TABLE IF NOT EXISTS accounts
(
    id
    SERIAL
    PRIMARY
    KEY,
    username
    VARCHAR
(
    50
) UNIQUE NOT NULL,
    password VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    100
) UNIQUE NOT NULL,
    last_login TIMESTAMP,
    is_banned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Table des personnages
CREATE TABLE IF NOT EXISTS characters
(
    id
    SERIAL
    PRIMARY
    KEY,
    account_id
    INTEGER
    REFERENCES
    accounts
(
    id
),
    name VARCHAR
(
    50
) UNIQUE NOT NULL,
    level INTEGER DEFAULT 1,
    experience BIGINT DEFAULT 0,
    class_id SMALLINT NOT NULL,
    gender SMALLINT NOT NULL,
    appearance JSONB,
    stats JSONB,
    position JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );