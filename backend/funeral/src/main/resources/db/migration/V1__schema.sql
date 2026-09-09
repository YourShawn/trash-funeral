-- trash-funeral schema: users, object types, funerals (PK/FK)

CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(64)  NOT NULL,
    email         VARCHAR(191) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(80)  NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE object_types (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    code            VARCHAR(32) NOT NULL,
    name_zh         VARCHAR(80) NOT NULL,
    name_en         VARCHAR(80) NOT NULL,
    eulogy_zh       TEXT        NOT NULL,
    eulogy_en       TEXT        NOT NULL,
    default_music   VARCHAR(64) NOT NULL,
    default_flowers VARCHAR(64) NOT NULL,
    throw_hint_zh   TEXT        NOT NULL,
    throw_hint_en   TEXT        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_object_types_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE funerals (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          BIGINT       NOT NULL,
    object_type_id   BIGINT       NOT NULL,
    photo_id         VARCHAR(48)  NOT NULL,
    identified_label VARCHAR(160) NOT NULL,
    object_name      VARCHAR(120) NOT NULL,
    eulogy           TEXT         NOT NULL,
    music_code       VARCHAR(64)  NOT NULL,
    flowers_code     VARCHAR(64)  NOT NULL,
    locale           VARCHAR(8)   NOT NULL,
    almanac_json     TEXT         NOT NULL,
    ritual_date      DATE         NOT NULL,
    public_token     VARCHAR(36)  NOT NULL,
    status           VARCHAR(16)  NOT NULL,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_funerals_public_token (public_token),
    KEY idx_funerals_user_created (user_id, created_at),
    CONSTRAINT fk_funerals_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_funerals_object_type
        FOREIGN KEY (object_type_id) REFERENCES object_types (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
