# Online School Enrollment System — Spring Boot

**⚠️ EXPERIMENTAL / PERSONAL TEST PROJECT**

This is a personal Spring Boot experiment and testing project by **Jerwin E. Cruz**.  
I have **no prior knowledge** in Spring Boot or Java web development — this entire codebase was **vibe-coded** with AI assistance.

---

## Group 2

- Cruz, Jerwin E.
- Matiga, John Michael
- Villabroza, Clark Daren
- Lazaro, Natalie
- Layos, Joland

## Tech Stack

- **Java 17** + Spring Boot 3.2.5
- **Spring Data JPA** (replaces JDBC + DAO layer)
- **Spring MVC** with **Thymeleaf** templating
- **MySQL** database
- **Maven** build
- **Railway**-ready deployment

## Project Structure

```
src/main/java/com/school/enrollment/
├── SchoolEnrollmentApplication.java      # Entry point
├── entity/                               # JPA entities
├── repository/                           # Spring Data JPA repositories
├── service/                              # Business logic
└── controller/                           # Spring MVC controllers

src/main/resources/
├── application.properties                # Config (env vars for Railway)
├── static/
│   ├── css/style.css                     # Styles
│   └── js/site.js                        # Interactive enhancements
└── templates/
    ├── info.html                         # About/info page
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

## Features

- **Student Registration** — Add, update, search, deactivate/reactivate, hard-delete
- **Course Management** — Add, update, search, deactivate, hard-delete
- **Enrollment** — Enroll in multiple courses at once, grouped per-student accordion view, drop (blocked if paid), audit log
- **Tuition Payment** — Cash/Bank Transfer/Check, proportional distribution across enrollments
- **Modern UI** — Responsive sidebar, modal dialogs, toast notifications, dark sidebar with gradients

## Notes

- This project was built for **educational / testing purposes only**
- The UI was enhanced with modern CSS and vanilla JS (no frontend framework)
- Not intended for production use
