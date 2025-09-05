# Build stage
FROM gradle:8.5.0-jdk21 AS build
WORKDIR /app
COPY . .

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
