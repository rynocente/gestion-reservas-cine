# Multi-stage build — ver payment-gateway-service/Dockerfile para explicación detallada

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:25-jdk AS runtime

# curl: necesario para health checks en Docker Compose y Kubernetes
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 50100
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]
