# Use Eclipse Temurin slim base image for JDK 21
FROM eclipse-temurin:21-jdk-alpine as builder

# Set the working directory in the container
WORKDIR /app

# Install Maven in the Alpine image
RUN apk add --no-cache maven

# Copy Maven project files (pom.xml and src folder)
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a minimal runtime image for the final stage
FROM eclipse-temurin:21-jre-alpine

# Set the working directory in the runtime container
WORKDIR /app

# Copy the packaged application JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port (adjust if necessary)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]