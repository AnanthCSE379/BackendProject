# Database Schema (ERD)

```mermaid
erDiagram
    APP_USERS ||--o{ REFRESH_TOKENS : "owns"

    APP_USERS {
        bigint id PK
        varchar full_name
        varchar email UNIQUE
        varchar password_hash
        varchar role
        timestamptz created_at
        timestamptz updated_at
    }

    REFRESH_TOKENS {
        bigint id PK
        bigint user_id FK
        varchar token UNIQUE
        timestamptz expires_at
        boolean revoked
        timestamptz created_at
    }

    STUDENTS {
        bigint id PK
        varchar student_code UNIQUE
        varchar first_name
        varchar last_name
        varchar email UNIQUE
        varchar phone
        date date_of_birth
        varchar gender
        varchar course_name
        smallint academic_year
        date enrollment_date
        numeric gpa
        varchar status
        timestamptz created_at
        timestamptz updated_at
    }
```
