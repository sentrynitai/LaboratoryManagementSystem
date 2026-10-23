# ========================================================
# Dockerfile for Laboratory Management System (Java MVC)
# Tailored for Deployment on Railway / Render / Cloud
# ========================================================

# Step 1: Build the Maven Project with Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and fetch dependencies first for efficient layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project source code
COPY src ./src

# Build the production WAR package
RUN mvn clean package -DskipTests

# Step 2: Runtime environment using Apache Tomcat 10 on Java 21
FROM tomcat:10.1-jdk21-temurin
WORKDIR /usr/local/tomcat

# Remove default Tomcat demo webapps to save resources and prevent conflicts
RUN rm -rf webapps/*

# Copy built WAR as ROOT.war (served at '/') and as LaboratoryManagementSystem.war (served at '/LaboratoryManagementSystem')
COPY --from=build /app/target/LaboratoryManagementSystem.war webapps/ROOT.war
COPY --from=build /app/target/LaboratoryManagementSystem.war webapps/LaboratoryManagementSystem.war

# Copy and setup entrypoint script to support Railway's dynamic $PORT
COPY entrypoint.sh /entrypoint.sh
RUN sed -i 's/\r$//' /entrypoint.sh && chmod +x /entrypoint.sh

# Default port
EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
