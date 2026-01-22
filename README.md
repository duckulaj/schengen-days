# Schengen Days (Spring Boot)

A production-shaped Spring Boot app to track Schengen stays and compute remaining days under the 90/180 rule.

## Features
- DB-backed users (H2 file DB) with registration & login
- Remember-me (persistent tokens stored in DB)
- Password reset (token stored hashed; email pluggable)
- Stays CRUD with overlap validation
- Pagination + inline row editing UI
- Supports future stays and future reference dates
- Flyway migrations

## Run

```bash
mvn spring-boot:run
```

Open:
- App: http://localhost:8080
- H2 console: http://localhost:8080/h2 (JDBC URL: jdbc:h2:file:./data/schengen)

## Seed users
Controlled by `app.seed.*` in `application.yml`.
Default seed:
- admin / (ADMIN_PASSWORD or change-me)
- alice / (ALICE_PASSWORD or change-me)

## Mail
- In non-prod profile, reset emails are logged.
- In prod profile, `SmtpMailer` uses Spring Boot mail settings.

## Notes
This project uses H2 file DB for a more production-like experience. For real production, point the datasource to Postgres; Flyway migrations will still apply.
