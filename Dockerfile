# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .

COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring --create-home spring

COPY --from=builder /workspace/target/hyrup-student-management.jar /app/app.jar

EXPOSE 8080
USER spring

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
