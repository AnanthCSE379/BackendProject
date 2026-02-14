# HYRUP Student Management System (Spring Boot + PostgreSQL)

A production-style backend for the HYRUP technical assignment, implemented with Java + Spring Boot.

The project provides:
- Secure authentication (`register`, `login`, `refresh`) with hashed passwords and JWT
- Student management REST APIs with validation, pagination, search, and filtering
- PostgreSQL persistence with Flyway migrations
- API documentation (OpenAPI/Swagger)
- Dockerized deployment

## 1. Tech Stack

- Java 17
- Spring Boot 3.2.x
- Spring Security + JWT (`jjwt`)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway migrations
- Bean Validation (`jakarta.validation`)
- Spring Actuator
- Swagger UI (`springdoc-openapi`)
- Docker + Docker Compose

Dependency details are documented in `docs/DEPENDENCIES.md`.

## 2. Features

### Authentication & Security
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- BCrypt password hashing
- JWT access tokens + refresh tokens
- Refresh token rotation and revocation
- Rate limiting on login/register endpoints (in-memory, per IP, per minute)
- Protected routes for student APIs

### Student Management
- Rich student model with academic/contact/emergency details
- Create, read, update, delete students
- Pagination and sorting for list endpoint
- Search by text and filter by course/year/status
- Strong input validation and centralized error handling

### Operational Readiness
- Flyway SQL schema migration
- Profile-based config (`dev`, `prod`)
- Health endpoints (`/actuator/health`, `/actuator/info`)
- Dockerfile for deployment
- `docker-compose.yml` for app + PostgreSQL

## 3. API Base Path

`/api/v1`

## 4. Project Structure

```text
src/main/java/com/hyrup/studentmanagement
├── auth
│   ├── controller
│   ├── dto
│   └── service
├── common
│   ├── dto
│   └── exception
├── config
├── security
├── student
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   ├── service
│   └── specification
└── user
    ├── model
    ├── repository
    └── service
```

## 5. Environment Variables

Copy `.env.example` to `.env` and update values.

| Variable | Description | Default |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `SERVER_PORT` | App port | `8080` |
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/hyrup` |
| `DB_USERNAME` | PostgreSQL username | `postgres` |
| `DB_PASSWORD` | PostgreSQL password | `postgres` |
| `JWT_SECRET` | JWT signing secret (>=32 chars) | required |
| `JWT_ACCESS_EXPIRATION_SECONDS` | Access token TTL | `900` |
| `JWT_REFRESH_EXPIRATION_SECONDS` | Refresh token TTL | `604800` |
| `JWT_ISSUER` | JWT issuer claim | `hyrup-student-management` |
| `AUTH_RATE_LIMIT_PER_MINUTE` | Login/register limit per minute per IP | `10` |
| `POSTGRES_DB` | Docker compose DB name | `hyrup` |
| `POSTGRES_USER` | Docker compose DB user | `postgres` |
| `POSTGRES_PASSWORD` | Docker compose DB password | `postgres` |

## 6. Local Run (Without Docker)

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 14+

### Steps
1. Create database (example):
   ```sql
   CREATE DATABASE hyrup;
   ```
2. Configure `.env` values (or export variables in shell).
3. Start app:
   ```bash
   mvn spring-boot:run
   ```
4. Swagger UI:
   - [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Flyway migrations run automatically on startup.

## 7. Docker Deployment

### Prerequisites
- Docker Desktop (or Docker Engine) must be running

### Full stack (recommended)
```bash
docker compose up --build
```

For verbose build logs:
```bash
docker compose --progress plain build --no-cache
docker compose up
```

This starts:
- PostgreSQL container on `5432`
- Spring Boot app on `8080`

### Build image only
```bash
docker build -t hyrup-student-management:latest .
```

### Run app image
```bash
docker run --rm -p 8080:8080 --env-file .env hyrup-student-management:latest
```
Note: this requires a reachable PostgreSQL instance from the container (configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` accordingly).

## 8. API Endpoints

### Auth

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Register a user, return JWT tokens |
| POST | `/api/v1/auth/login` | Public | Authenticate user, return JWT tokens |
| POST | `/api/v1/auth/refresh` | Public | Rotate refresh token and issue new access token |

### Students

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/v1/students` | JWT | Create student |
| GET | `/api/v1/students/{id}` | JWT | Get student by ID |
| GET | `/api/v1/students` | JWT | List students with pagination/filter/search |
| PUT | `/api/v1/students/{id}` | JWT | Update student |
| DELETE | `/api/v1/students/{id}` | JWT | Delete student |

### Student list query params
- `page` (default `0`)
- `size` (default `10`, max `100`)
- `sortBy` (e.g. `createdAt`, `firstName`, `gpa`)
- `sortDirection` (`asc` or `desc`)
- `search` (matches first/last name, email, student code, major)
- `courseName`
- `academicYear`
- `status`

## 9. Student Model Field Explanation

| Field | Type | Why it exists |
|---|---|---|
| `studentCode` | String | Human-readable institutional identifier |
| `firstName`, `lastName` | String | Core identity fields |
| `email` | String | Primary digital contact and unique identity |
| `phone` | String | Direct contact |
| `dateOfBirth`, `gender` | Date/Enum | Demographic/student profile |
| `addressLine1/2`, `city`, `state`, `postalCode`, `country` | String | Location/contact records |
| `emergencyContactName/Phone/Relation` | String | Emergency handling support |
| `courseName`, `major` | String | Academic program information |
| `academicYear` | Integer | Student progression tracking (stored as `SMALLINT` in PostgreSQL) |
| `enrollmentDate`, `expectedGraduationDate` | Date | Lifecycle milestones |
| `gpa` | Decimal | Academic performance metric |
| `creditsCompleted` | Integer | Progress toward degree completion |
| `status` | Enum | Operational student state |
| `notes` | String | Additional administrative context |
| `createdAt`, `updatedAt` | Timestamp | Auditability |

## 10. Validation and Error Handling

- Request DTOs use Jakarta validation annotations
- Constraint violations return `400`
- Duplicate records return `409`
- Missing records return `404`
- Unauthorized access returns `401`
- Forbidden access returns `403`

Error response shape:
```json
{
  "timestamp": "2026-02-14T18:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/students",
  "validationErrors": {
    "email": "Email is invalid"
  }
}
```

## 11. Security Decisions

- Passwords are never stored as plain text; BCrypt hash only
- JWT contains user id/email/role/type claims
- Access and refresh tokens use separate token types
- Refresh token records are persisted and revocable
- New login revokes older active refresh tokens for that user
- Auth endpoints have lightweight rate limiting to reduce brute-force attempts

## 12. Database and Migration

- Flyway script: `src/main/resources/db/migration/V1__init_schema.sql`
- ER diagram: `docs/ERD.md`

## 13. API Collection

Import `postman/HYRUP-Student-Management.postman_collection.json` into Postman.

## 14. Tests

Included tests:
- `JwtServiceTest`
- `AuthServiceTest`

Run:
```bash
mvn test
```

## 15. Assumptions / Design Notes

- Single service API backend; no frontend included
- Registration assigns `USER` role by default
- Role claim is included in JWT and available for future RBAC expansion
- Rate limiting is in-memory (suitable for assignment scope); distributed stores can replace it for production clusters

## 16. Troubleshooting

- App restarts continuously:
  ```bash
  docker compose logs --tail=200 app
  ```
- Reset full local stack including Postgres data:
  ```bash
  docker compose down -v
  docker compose up --build
  ```
- Verify health:
  - [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
