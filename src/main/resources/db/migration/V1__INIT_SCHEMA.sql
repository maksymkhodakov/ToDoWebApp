CREATE TABLE IF NOT EXISTS "users" (
    "id"                          BIGSERIAL PRIMARY KEY,
    "email"                       VARCHAR(255) NOT NULL UNIQUE,
    "password"                    VARCHAR(255) NOT NULL,
    "name"                        VARCHAR(255) NOT NULL,
    "last_name"                   VARCHAR(255),
    "authorities"                 TEXT,
    "enabled"                     BOOLEAN,
    "account_non_expired"         BOOLEAN,
    "account_non_locked"          BOOLEAN,
    "credentials_non_expired"     BOOLEAN,
    "create_date"                 TIMESTAMP DEFAULT NULL,
    "update_date"                 TIMESTAMP DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS "todos" (
    "id"              BIGSERIAL PRIMARY KEY,
    "description"     VARCHAR(255) NOT NULL,
    "due_date"        TIMESTAMP NOT NULL,
    "check_mark"      BOOLEAN NOT NULL,
    "completion_date" TIMESTAMP,
    "create_date"     TIMESTAMP DEFAULT NULL,
    "update_date"     TIMESTAMP DEFAULT NULL,
    "user_id"         BIGINT,
    CONSTRAINT "fk_todo_user_id" FOREIGN KEY ("user_id") REFERENCES "users" ("id")
);
