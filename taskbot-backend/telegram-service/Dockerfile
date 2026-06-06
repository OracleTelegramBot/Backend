# =========================================================
# Multi-stage build — telegram-service
# Java 17 LTS, Maven builder, JRE Alpine runtime (~180 MB final)
# =========================================================

# ===== Stage 1: builder =====
FROM maven:3-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# Layer caching: pom.xml primero para cachear deps cuando solo cambia src/
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ===== Stage 2: runtime =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Wildcard matchea .jar (auth, ai, telegram, task) y .war (kpi)
COPY --from=builder /build/target/*.[jw]ar app.archive

# JVM tuning para containers — respeta los memory limits que K8s asigne
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.archive"]
