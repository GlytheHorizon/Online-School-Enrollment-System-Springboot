# Online School Enrollment System — Spring Boot

A Spring Boot web application for managing student enrollment, courses, and tuition payments.  
Migrated from the original Java Swing desktop application (MVC + DAO → Spring Boot + JPA + Thymeleaf).

## Group 2

- Cruz, Jerwin E.
- Matiga, John Michael
- Villabroza, Clark Daren
- Lazaro, Natalie
- Layos, Joland

## Tech Stack

- **Java 17** + Spring Boot 3.2.5
- **Spring Data JPA** (replaces JDBC + DAO layer)
- **Spring MVC** with **Thymeleaf** templating (replaces Swing UI)
- **MySQL** (same schema as original)
- **Maven** build
- **Railway**-ready deployment

## Project Structure

```
src/main/java/com/school/enrollment/
├── SchoolEnrollmentApplication.java      # Entry point
├── entity/                               # JPA entities (replaces model/)
├── repository/                           # Spring Data JPA (replaces dao/ + daoimpl/)
├── service/                              # Business logic (replaces Swing controllers)
└── controller/                           # Spring MVC controllers (web endpoints)

src/main/resources/
├── application.properties                # Config (env vars for Railway)
├── static/css/style.css                  # Styles
└── templates/
    ├── index.html                        # Dashboard
    ├── students.html                     # Student list + registration form
    ├── student-details.html              # Student detail/edit view
    ├── courses.html                      # Course management
    ├── enrollments.html                  # Enrollment + audit log
    ├── payments.html                     # Payment processing + history
    └── fragments/header.html             # Shared layout (sidebar, alerts, footer)
```

## Setup Database

Run `schema.sql` to create the `school_enrollment` database and seed data.

## How to Run Locally

```bash
mvn clean package
java -jar target/enrollment-1.0.0.jar
```

Set environment variables (or edit `application.properties`):
- `DB_URL` — MySQL JDBC URL (default: `jdbc:mysql://localhost:3306/school_enrollment`)
- `DB_USERNAME` — MySQL user (default: `root`)
- `DB_PASSWORD` — MySQL password (default: empty)
- `PORT` — Server port (default: `8080`)

## Railway Deployment

The project is pre-configured for Railway:
- `system.properties` — declares Java 17 runtime
- `Procfile` / `Railway.json` — start command using `$PORT`
- Environment variables for DB credentials
- `application.properties` reads `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`

## Features

- **Student Registration** — Add, update, search, deactivate/reactivate (soft-delete)
- **Course Management** — Add, update, search, deactivate (blocked if active enrollments exist)
- **Enrollment** — Enroll in multiple courses at once, drop (blocked if paid), audit log
- **Tuition Payment** — Cash/Bank Transfer/Check, proportional distribution across enrollments
- All validations and business logic preserved from original Swing application

## Original Swing Project

The original Java Swing implementation is in the `activity15` folder.
