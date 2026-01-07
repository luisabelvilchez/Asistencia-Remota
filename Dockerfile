# 1. Etapa de compilación
FROM gradle:7.6.0-jdk17 AS build
COPY . .

# Ejecutamos la compilación de Gradle apuntando al proyecto del servidor
RUN gradle :asistencia:servidor:bootJar --no-daemon

# 2. Etapa de ejecución
FROM openjdk:17-jdk-slim
# Buscamos el archivo generado por Gradle
COPY --from=build /home/gradle/project/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
