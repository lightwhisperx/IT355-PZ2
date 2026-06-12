
CREATE DATABASE IF NOT EXISTS webprojekat
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE webprojekat;


CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS users (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user  FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role  FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS categories (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categories_name (name)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS posts (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL,
    category_id BIGINT       NOT NULL,
    author_id   BIGINT       NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    views       BIGINT       NOT NULL DEFAULT 0,
    likes       BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_posts_author   FOREIGN KEY (author_id)   REFERENCES users (id)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS comments (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    content    VARCHAR(2000) NOT NULL,
    post_id    BIGINT        NOT NULL,
    author_id  BIGINT        NOT NULL,
    created_at DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comments_post   FOREIGN KEY (post_id)   REFERENCES posts (id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (id)
) ENGINE=InnoDB;

