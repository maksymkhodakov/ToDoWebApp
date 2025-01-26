CREATE TABLE IF NOT EXISTS "user" (
    "id"              BIGSERIAL PRIMARY KEY,
    "email"           VARCHAR(255) NOT NULL UNIQUE,
    "username"        VARCHAR(255) NOT NULL,
    "password"        VARCHAR(255) NOT NULL,
    "user_role"       VARCHAR(100),
    "first_name"      VARCHAR(255),
    "last_name"       VARCHAR(255),
    "create_date"     TIMESTAMP DEFAULT NULL,
    "update_date"     TIMESTAMP DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS "todo" (
    "id"              BIGSERIAL PRIMARY KEY,
    "task"            VARCHAR(255) NOT NULL,
    "priority"        VARCHAR(100) NOT NULL,
    "create_date"     TIMESTAMP DEFAULT NULL,
    "update_date"     TIMESTAMP DEFAULT NULL,
    "user_id"         BIGINT,
    CONSTRAINT "fk_todo_user_id" FOREIGN KEY ("user_id") REFERENCES "user" ("id")
);
