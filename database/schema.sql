-- Animal Shelter Management Platform - MVP Schema
-- MySQL 8 - Full DDL from project-requirements data model
-- UUIDs used for users, animals, applications, adoptions, and related FKs to prevent enumeration.

CREATE DATABASE IF NOT EXISTS animal_shelter;
USE animal_shelter;

-- ---------------------------------------------------------------------------
-- 1) users 
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id                  CHAR(36)     NOT NULL,
    email               VARCHAR(255) NOT NULL,
    username            VARCHAR(255) NOT NULL,
    password_hash       VARCHAR(255) NULL,
    display_name        VARCHAR(255) NULL,
    phone               VARCHAR(50)  NULL,
    is_enabled          TINYINT(1)   NOT NULL DEFAULT 1,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 2) roles
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 3) user_roles
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_roles (
    user_id CHAR(36) NOT NULL,
    role_id BIGINT   NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY fk_user_roles_role_id (role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 4) adopter_profiles
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS adopter_profiles (
    user_id                  CHAR(36)     NOT NULL,
    address_line1            VARCHAR(255) NULL,
    address_line2            VARCHAR(255) NULL,
    city                     VARCHAR(100) NULL,
    state                    VARCHAR(50)  NULL,
    zip                      VARCHAR(20)  NULL,
    household_size           INT          NULL,
    housing_type             VARCHAR(20)  NULL,
    has_yard                 TINYINT(1)   NULL,
    has_kids                 TINYINT(1)   NULL,
    has_other_pets           TINYINT(1)   NULL,
    needs_good_with_kids     TINYINT(1)   NULL,
    needs_good_with_other_pets TINYINT(1) NULL,
    willing_medically_complex TINYINT(1)  NULL,
    notes                    TEXT         NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_adopter_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 4b) adopter_questionnaires 
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS adopter_questionnaires (
    id                        CHAR(36)     NOT NULL,
    user_id                   CHAR(36)     NOT NULL,
    schema_version            INT          NOT NULL DEFAULT 1,
    household_size            INT          NULL,
    housing_type              VARCHAR(20)  NULL,
    has_yard                  TINYINT(1)   NULL,
    has_kids                  TINYINT(1)   NULL,
    has_other_pets            TINYINT(1)   NULL,
    needs_good_with_kids      TINYINT(1)   NULL,
    needs_good_with_other_pets TINYINT(1)  NULL,
    willing_medically_complex TINYINT(1)  NULL,
    notes                     TEXT         NULL,
    created_at                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_adopter_questionnaires_user_id (user_id),
    CONSTRAINT fk_adopter_questionnaires_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 5) shelters
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shelters (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    address_line1 VARCHAR(255) NULL,
    address_line2 VARCHAR(255) NULL,
    city          VARCHAR(100) NULL,
    state         VARCHAR(50)  NULL,
    zip           VARCHAR(20)  NULL,
    capacity_total INT         NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 6) animals
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS animals (
    id                     CHAR(36)     NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    species                VARCHAR(20)  NOT NULL,
    breed                  VARCHAR(100) NULL,
    sex                    VARCHAR(20)  NULL,
    age_months             INT          NULL,
    good_with_kids         TINYINT(1)   NOT NULL DEFAULT 0,
    good_with_other_pets   TINYINT(1)   NOT NULL DEFAULT 0,
    medically_complex      TINYINT(1)   NOT NULL DEFAULT 0,
    description            TEXT         NULL,
    status                 VARCHAR(30)  NOT NULL,
    current_shelter_id     BIGINT       NULL,
    current_foster_user_id CHAR(36)     NULL,
    created_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY fk_animals_current_shelter (current_shelter_id),
    KEY fk_animals_current_foster_user (current_foster_user_id),
    CONSTRAINT fk_animals_shelter FOREIGN KEY (current_shelter_id) REFERENCES shelters (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_animals_foster_user FOREIGN KEY (current_foster_user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 7) animal_photos 
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS animal_photos (
    id              CHAR(36)     NOT NULL,
    animal_id       CHAR(36)     NOT NULL,
    s3_key          VARCHAR(500) NOT NULL,
    url             VARCHAR(500) NOT NULL,
    is_primary      TINYINT(1)   NOT NULL DEFAULT 0,
    content_type    VARCHAR(100) NULL,
    file_size_bytes BIGINT       NULL,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_animal_photos_s3_key (s3_key),
    KEY fk_animal_photos_animal (animal_id),
    CONSTRAINT fk_animal_photos_animal FOREIGN KEY (animal_id) REFERENCES animals (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 8) animal_events 
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS animal_events (
    id                    CHAR(36)     NOT NULL,
    animal_id             CHAR(36)     NOT NULL,
    event_type            VARCHAR(50)  NOT NULL,
    from_shelter_id       BIGINT       NULL,
    to_shelter_id         BIGINT       NULL,
    from_foster_user_id   CHAR(36)     NULL,
    to_foster_user_id     CHAR(36)     NULL,
    performed_by_user_id  CHAR(36)     NULL,
    notes                 TEXT         NULL,
    occurred_at           DATETIME     NOT NULL,
    PRIMARY KEY (id),
    KEY fk_animal_events_animal (animal_id),
    KEY fk_animal_events_from_shelter (from_shelter_id),
    KEY fk_animal_events_to_shelter (to_shelter_id),
    KEY fk_animal_events_from_foster (from_foster_user_id),
    KEY fk_animal_events_to_foster (to_foster_user_id),
    KEY fk_animal_events_performed_by (performed_by_user_id),
    CONSTRAINT fk_animal_events_animal FOREIGN KEY (animal_id) REFERENCES animals (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_animal_events_from_shelter FOREIGN KEY (from_shelter_id) REFERENCES shelters (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_animal_events_to_shelter FOREIGN KEY (to_shelter_id) REFERENCES shelters (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_animal_events_from_foster FOREIGN KEY (from_foster_user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_animal_events_to_foster FOREIGN KEY (to_foster_user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_animal_events_performed_by FOREIGN KEY (performed_by_user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 9) adoption_applications
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS adoption_applications (
    id                        CHAR(36)     NOT NULL,
    animal_id                 CHAR(36)     NOT NULL,
    adopter_user_id           CHAR(36)     NOT NULL,
    status                    VARCHAR(20)  NOT NULL,
    questionnaire_snapshot_json JSON       NULL,
    staff_reviewer_user_id    CHAR(36)     NULL,
    decision_notes            TEXT         NULL,
    created_at                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY fk_adoption_applications_animal (animal_id),
    KEY fk_adoption_applications_adopter (adopter_user_id),
    KEY fk_adoption_applications_reviewer (staff_reviewer_user_id),
    CONSTRAINT fk_adoption_applications_animal FOREIGN KEY (animal_id) REFERENCES animals (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_adoption_applications_adopter FOREIGN KEY (adopter_user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_adoption_applications_reviewer FOREIGN KEY (staff_reviewer_user_id) REFERENCES users (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 10) adoptions
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS adoptions (
    id                    CHAR(36) NOT NULL,
    animal_id             CHAR(36) NOT NULL,
    adopter_user_id       CHAR(36) NOT NULL,
    application_id        CHAR(36) NOT NULL,
    adopted_at            DATETIME NOT NULL,
    finalized_by_user_id  CHAR(36) NOT NULL,
    notes                 TEXT     NULL,
    PRIMARY KEY (id),
    KEY fk_adoptions_animal (animal_id),
    KEY fk_adoptions_adopter (adopter_user_id),
    UNIQUE KEY uk_adoptions_application (application_id),
    KEY fk_adoptions_finalized_by (finalized_by_user_id),
    CONSTRAINT fk_adoptions_animal FOREIGN KEY (animal_id) REFERENCES animals (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_adoptions_adopter FOREIGN KEY (adopter_user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_adoptions_application FOREIGN KEY (application_id) REFERENCES adoption_applications (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_adoptions_finalized_by FOREIGN KEY (finalized_by_user_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Seed roles (ADOPTER, STAFF, FOSTER)
-- ---------------------------------------------------------------------------
INSERT IGNORE INTO roles (name) VALUES ('ADOPTER'), ('STAFF'), ('FOSTER');
