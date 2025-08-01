# Use a Maven image with Java 17+
FROM maven:3.9-eclipse-temurin-17 as builder

# Set working directory
WORKDIR /workspace/server

# Copy only the server folder (Spring Boot app)
COPY server /workspace/server

# Download dependencies ahead of time
RUN mvn dependency:go-offline

# Run tests
CMD ["mvn", "test", "-q"]
