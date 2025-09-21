# Base image with Java 21
FROM eclipse-temurin:21-jdk

# Copy the Spring Boot JAR into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","/app.jar"]