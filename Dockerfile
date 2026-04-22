# Stage 1: Build the JAR

FROM gradle:9.1.0-jdk21 AS builder

WORKDIR /app

# Copy project files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY src/ src/

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# GitHub credentials - build arguments  
ARG GITHUB_ACTOR=aisolutions-dev-bot
ARG GITHUB_TOKEN
ARG GIT_BRANCH=main
ARG QUARKUS_PLUGIN_ID=io.quarkus
ARG QUARKUS_PLUGIN_VERSION=3.28.4

# Create build script with proper env handling
RUN cat > /app/build.sh << 'EOFBUILD'
#!/bin/bash
set -e
export GITHUB_ACTOR="${GITHUB_ACTOR}"
export GITHUB_TOKEN="${GITHUB_TOKEN}"

if [ "${GIT_BRANCH}" = "staging" ]; then
  ./gradlew build \
    -Dquarkus.package.jar.type=uber-jar \
    -Dquarkus.profile=staging \
    -DquarkusPluginId="${QUARKUS_PLUGIN_ID}" \
    -DquarkusPluginVersion="${QUARKUS_PLUGIN_VERSION}" \
    -x test
else
  ./gradlew build \
    -Dquarkus.package.jar.type=uber-jar \
    -Dquarkus.profile=prod \
    -DquarkusPluginId="${QUARKUS_PLUGIN_ID}" \
    -DquarkusPluginVersion="${QUARKUS_PLUGIN_VERSION}" \
    -x test
fi
EOFBUILD

RUN chmod +x /app/build.sh && /app/build.sh

# Stage 2: Lightweight runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/project-management-backend-0.0.1-runner.jar /app/

# Expose Quarkus port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java","-jar","/app/project-management-backend-0.0.1-runner.jar"]
