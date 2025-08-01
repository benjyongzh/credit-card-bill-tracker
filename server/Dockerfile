# Use Maven with Java 21
FROM maven:3.9-eclipse-temurin-21 as builder

# Set working directory inside container
WORKDIR /app

# Copy current directory contents into container (just /server)
COPY . .

# Preload dependencies for offline use
RUN mvn dependency:go-offline

# Default command: run tests
CMD ["mvn", "test", "-q"]
