# HYRUP Student Management API

## What this project includes
- Secure auth APIs: register and login
- Password hashing with BCrypt
- JWT token generation and validation
- Protected student CRUD APIs
- PostgreSQL persistence via Spring Data JPA
- Postman collection and API documentation
  
## Tech stack
- Java 17
- Spring Boot 3.2.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- JJWT

## Project structure
```text
/BackendProject/src/main/java/com/hyrup/studentmanagement
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

/BackendProject/src/main/resources
└── application.yml
```

## Environment variables
Use:
- `/BackendProject/.env`
- `/BackendProject/.env.example`

| Variable | Description |
|---|---|
| `SERVER_PORT` | App port |
| `DB_URL` | JDBC URL (`jdbc:postgresql://localhost:5432/hyrup`) |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | JWT secret (32+ chars) |
| `JWT_EXPIRATION_SECONDS` | Token expiry seconds |

## Setup and run

### 1) Start PostgreSQL
- Note that brew services is for macos, equivalent commands for windows and linux systems 
```bash
brew services start postgresql@16
pg_isready -h localhost -p 5432
```

### 2) Create database (one-time)
```bash
createdb -h localhost -U postgres hyrup
```
If it already exists, ignore the error.

### 3) Load environment variables
```bash
cd "/BackendProject/Hyrup project"
set -a
source .env
set +a
```

### 4) Verify JWT secret is loaded
```bash
echo -n "$JWT_SECRET" | wc -c
```
Expected: `32` or more.

### 5) Start Spring Boot app
```bash
mvn spring-boot:run
```
Keep this terminal running.

## Postman usage (step-by-step)

### 1) Open Postman
```bash
open -a Postman
```
Sign in (or use guest mode if shown).

### 2) Import collection
Import file:
- `/BackendProject/postman/HYRUP-Student-Management.postman_collection.json`

### 3) Set collection variable
In Postman collection variables:
- `baseUrl = http://localhost:8080`

### 4) How to enter request body in Postman
Inside a request tab:
1. Click `Body`
2. Select `raw`
3. Select `JSON` from right-side dropdown
4. Paste JSON
5. Click `Send`

### 5) Run requests in this exact order
1. `Auth > Register`
2. `Auth > Login` (auto-saves JWT to `token` variable)
3. `Students (Protected) > Create Student`
4. `Students (Protected) > List Students`
5. `Students (Protected) > Get Student By ID`
6. `Students (Protected) > Update Student`
7. `Students (Protected) > Delete Student`

## Core API summary

### Public auth endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`

### Protected student endpoints
- `GET /api/students`
- `GET /api/students/{id}`
- `POST /api/students`
- `PUT /api/students/{id}`
- `DELETE /api/students/{id}`

## Sample request bodies

### Register
```json
{
  "name": "Demo User",
  "email": "demo@example.com",
  "password": "StrongPass1"
}
```

### Login
```json
{
  "email": "demo@example.com",
  "password": "StrongPass1"
}
```

### Create/Update Student
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

## Expected responses (quick check)
- Register success: `201` + token JSON
- Login success: `200` + token JSON
- List students initially: `200` + `[]`
- Unauthorized protected request: `401`
- Create student success: `201`
- Delete student success: `204`

## Student model field explanation
- `studentId`: unique institution student identifier
- `firstName`, `lastName`, `email`: identity/contact
- `course`, `academicYear`, `enrollmentDate`, `gpa`: academic information
- `phone`, `address`: contact details
- `emergencyContactName`, `emergencyContactPhone`: emergency contact details
- `status`: student state (examples: `ACTIVE` , `CLOSED`)

## Additional docs
- API details: `/BackendProject/docs/API_DOCUMENTATION.md`
- Postman collection: `/BackendProject/postman/HYRUP-Student-Management.postman_collection.json`
