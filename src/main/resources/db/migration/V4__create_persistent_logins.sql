-- Flyway migration to create Spring Security remember-me table
-- Ref: org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl

CREATE TABLE IF NOT EXISTS persistent_logins (
    username           VARCHAR(100)    NOT NULL,
    series             VARCHAR(64)     NOT NULL,
    token              VARCHAR(64)     NOT NULL,
    last_used          TIMESTAMP       NOT NULL,
    CONSTRAINT pk_persistent_logins PRIMARY KEY (series)
);

-- Optional index to speed up lookups by username
CREATE INDEX IF NOT EXISTS idx_persistent_logins_username ON persistent_logins (username);
