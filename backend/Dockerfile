# Stage : Build the Spring Boot application

FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy the Gradle wrapper and build configuration first.
# This allows Docker to cache dependency-related layers.

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Copy the application source code
COPY src src

# Build hte executable Spring Boot JAR
RUN ./gradlew bootJar --no-daemon

#Stage 2: Run the application

FROM eclipse-temurin:17-jre

WORKDIR /app

# COPY only the executable JAR from the build stage.
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

