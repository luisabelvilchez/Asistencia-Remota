# 1. Etapa de compilación
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /home/gradle/project
COPY . .

# Ejecutamos la compilación
RUN gradle :asistencia:servidor:bootJar --no-daemon

# 2. Etapa de ejecución (Imagen actualizada)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /home/gradle/project/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
