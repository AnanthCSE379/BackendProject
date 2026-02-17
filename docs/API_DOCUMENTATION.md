# API Documentation

Base URL: `http://localhost:8080`

Auth type for protected endpoints:
- Header: `Authorization: Bearer <JWT>`

## Auth APIs

### 1) Register
- Method: `POST`
- Path: `/api/auth/register`
- Body:
```json
{
  "name": "Demo User",
  "email": "demo@example.com",
  "password": "StrongPass1"
}
```
- Success: `201 Created`
- Success body:
```json
{
  "token": "<JWT>",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```
- Errors: `400 Bad Request` validation failure, `409 Conflict` email already registered

### 2) Login
- Method: `POST`
- Path: `/api/auth/login`
- Body:
```json
{
  "email": "demo@example.com",
  "password": "StrongPass1"
}
```
- Success: `200 OK`
- Success body:
```json
{
  "token": "<JWT>",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```
- Errors: `400 Bad Request` validation failure, `401 Unauthorized` invalid credentials

## Student APIs (Protected)

### 3) Create Student
- Method: `POST`
- Path: `/api/students`
- Body:
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
- Success: `201 Created`
- Errors: `400 Bad Request` validation failure, `401 Unauthorized` missing/invalid JWT, `409 Conflict` duplicate `studentId` or `email`

### 4) List Students
- Method: `GET`
- Path: `/api/students`
- Success: `200 OK`
- Example:
```json
[]
```

### 5) Get Student by ID
- Method: `GET`
- Path: `/api/students/{id}`
- Success: `200 OK`
- Errors: `401 Unauthorized`, `404 Not Found`

### 6) Update Student
- Method: `PUT`
- Path: `/api/students/{id}`
- Body: same as Create Student
- Success: `200 OK`
- Errors: `400 Bad Request`, `401 Unauthorized`, `404 Not Found`, `409 Conflict`

### 7) Delete Student
- Method: `DELETE`
- Path: `/api/students/{id}`
- Success: `204 No Content`
- Errors: `401 Unauthorized`, `404 Not Found`

## Postman
Import this collection:
- `/Users/ananth/Documents/Hyrup project/postman/HYRUP-Student-Management.postman_collection.json`

Recommended run order:
1. `Auth > Register`
2. `Auth > Login`
3. `Students (Protected)` requests
