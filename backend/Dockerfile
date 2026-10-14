# Stage 1: Build the Spring Boot Application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create non-root user for security
RUN groupadd -r medikiosk && useradd -r -g medikiosk medikiosk

# Create directory for file uploads
RUN mkdir -p /app/uploads && chown -R medikiosk:medikiosk /app/uploads

# Copy the built jar from the build stage
COPY --from=build /app/target/medikiosk-backend-1.0.0.jar app.jar
RUN chown medikiosk:medikiosk app.jar

USER medikiosk

# Default port (Railway injects $PORT at runtime)
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
