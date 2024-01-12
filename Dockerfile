# Use the official OpenJDK image to run the JAR file.
FROM openjdk:17-jdk-slim

# Expose the port the application will run on
EXPOSE 8100

# Set the working directory in the container
WORKDIR /app

# Copy the pre-built JAR file into the container
COPY build/libs/secret-recipe-api-latest.jar app.jar

# Define the command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]