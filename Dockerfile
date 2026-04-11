# === Stage 1: Build JAR with Maven ===
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build JAR
RUN mvn clean package -DskipTests

# === Stage 2: Run application ===
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy JAR from builder stage ✅
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8003

ENTRYPOINT ["java","-jar","app.jar"]