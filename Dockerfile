# === Stage 1: Build JAR with Maven ===
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven config & source
COPY pom.xml mvnw* ./
COPY src ./src

# Build application (skip tests for faster builds, enable tests in CI)
RUN mvn clean -DskipTests package

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]