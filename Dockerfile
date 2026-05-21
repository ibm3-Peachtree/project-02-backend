# ===== Build Stage =====
FROM gradle:8.7-jdk21 AS builder

WORKDIR /build

COPY . .

RUN gradle clean bootJar --no-daemon

# ===== Run Stage =====
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]