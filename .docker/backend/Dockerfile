FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app

COPY webapp/target/webapp-0.0.1-SNAPSHOT.jar ./app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/app.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080