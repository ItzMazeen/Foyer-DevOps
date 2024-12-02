# Use an OpenJDK image as the base
FROM openjdk:17-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file produced by Maven into the container
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8070

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
