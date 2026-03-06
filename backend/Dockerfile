# Step 1: Build the Spring Boot app using Maven and Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
# Copy the source code
COPY src ./src

# Build the application (skipping tests to make it faster)
RUN mvn clean package -DskipTests

# Step 2: Run the app using a lightweight Java 17 environment
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built .jar file from the 'build' step above
COPY --from=build /app/target/*.jar app.jar

# Open port 8080 for the backend
EXPOSE 8080

# Command to start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]