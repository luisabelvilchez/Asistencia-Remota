# 1. Etapa de compilación
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .

# Damos permisos de ejecución al gradlew y compilamos
RUN cd asistencia/servidor && chmod +x gradlew && ./gradlew bootJar --no-daemon

# 2. Etapa de ejecución
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
