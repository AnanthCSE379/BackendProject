# HYRUP Student Management API (Core Requirements Only)

Minimal Spring Boot project that implements only the mandatory assignment requirements.

## 1) Tech stack
- Java 17
- Spring Boot 3.2.x
- Spring Security (BCrypt + JWT)
- Spring Data JPA
- PostgreSQL

## 2) Implemented requirements
- Student model with identification, academic, and contact details
- Registration and login APIs
- Email validation
- Password hashing (BCrypt)
- JWT generation with claims (`userId`, `email`, `role`)
- JWT middleware to protect student routes
- REST APIs with proper status codes

## 3) Project structure
```text
src/main/java/com/hyrup/studentmanagement
├── AppUser.java
├── AppUserRepository.java
├── AuthController.java
├── JwtAuthFilter.java
├── JwtService.java
├── SecurityConfig.java
├── Student.java
├── StudentController.java
├── StudentManagementApplication.java
└── StudentRepository.java

src/main/resources
└── application.yml
```

## 4) Environment variables
Copy `.env.example` values into your shell or `.env`.

| Variable | Description |
|---|---|
| `SERVER_PORT` | App port |
| `DB_URL` | JDBC URL (example: `jdbc:postgresql://localhost:5432/hyrup`) |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | JWT secret (minimum 32 chars) |
| `JWT_EXPIRATION_SECONDS` | Access token expiry in seconds |

## 5) Database setup / migration approach
This project uses JPA auto-schema update for simplicity.
- Configure database connection in env vars
- Create database once:
```sql
CREATE DATABASE hyrup;
```
- On app start, tables are created/updated automatically via:
`spring.jpa.hibernate.ddl-auto=update`

## 6) Run locally
1. Start PostgreSQL.
2. Ensure env vars are set.
3. Run:
```bash
mvn spring-boot:run
```

## 7) API documentation

### Auth (public)
1. `POST /api/auth/register`
- Request:
```json
{
  "name": "Demo User",
  "email": "demo@example.com",
  "password": "StrongPass1#"
}
```
- Success: `201 Created`
- Errors: `400` (validation), `409` (email exists)

2. `POST /api/auth/login`
- Request:
```json
{
  "email": "demo@example.com",
  "password": "StrongPass1#"
}
```
- Success: `200 OK`
- Errors: `400` (validation), `401` (invalid credentials)

Auth success response:
```json
{
  "token": "<JWT>",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```

### Students (JWT required)
Add header: `Authorization: Bearer <JWT>`

1. `GET /api/students`
2. `GET /api/students/{id}`
3. `POST /api/students`
4. `PUT /api/students/{id}`
5. `DELETE /api/students/{id}`

Student request body (`POST`/`PUT`):
```json
{
  "studentId": "HYR-001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@college.edu",
  "course": "Computer Science",
  "academicYear": 3,
  "enrollmentDate": "2024-08-15",
  "gpa": 3.7,
  "phone": "+1-555-111-2222",
  "address": "12 Main St, Boston, MA",
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "+1-555-333-4444",
  "status": "ACTIVE"
}
```

## 8) Student model field explanation
- `studentId`: unique institution student identifier
- `firstName`, `lastName`, `email`: primary identity/contact
- `course`: enrolled program/course
- `academicYear`: current year of study
- `enrollmentDate`: official admission date
- `gpa`: current grade point average
- `phone`, `address`: student contact details
- `emergencyContactName`, `emergencyContactPhone`: emergency contact
- `status`: current lifecycle status (example: `ACTIVE`)

## 9) Assumptions / design decisions
- Single user role (`USER`) is enough for assignment scope.
- JWT is stateless; no refresh-token flow included.
- No optional features (Swagger, Docker, rate-limiting, pagination, search, tests) to keep project focused on required parts only.
