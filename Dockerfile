# --- Stage 1: Build the Spring Boot application using Gradle ---
FROM gradle:8.12.1-jdk21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy only the Gradle wrapper files to leverage Docker caching
COPY gradlew gradle/ ./

# Ensure the Gradle wrapper script has execution permissions
RUN chmod +x gradlew

# Copy the rest of the project files
COPY . .

# Build the application using Gradle (excluding tests for faster builds)
RUN ./gradlew build -x test

# --- Stage 2: Create a minimal runtime image ---
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Define the startup command
ENTRYPOINT ["java", "-jar", "app.jar"]
