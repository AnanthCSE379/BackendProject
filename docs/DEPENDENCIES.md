# Dependency Notes

## Core Framework
- `spring-boot-starter-web`: REST controllers, JSON request/response handling
- `spring-boot-starter-data-jpa`: persistence and repository abstraction
- `spring-boot-starter-security`: authentication/authorization infrastructure
- `spring-boot-starter-validation`: bean validation for DTOs
- `spring-boot-starter-actuator`: health and metrics endpoints

## Database
- `postgresql`: PostgreSQL JDBC driver
- `flyway-core`: versioned SQL migrations

## Auth / Security
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`: JWT generation and validation
- `BCryptPasswordEncoder` (from Spring Security): password hashing

## API Documentation
- `springdoc-openapi-starter-webmvc-ui`: OpenAPI generation and Swagger UI

## Testing
- `spring-boot-starter-test`: JUnit 5 + AssertJ + Mockito baseline
- `spring-security-test`: security test support
