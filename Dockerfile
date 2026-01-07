# 1. Etapa de compilación
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /home/gradle/project
COPY . .

# Entramos directamente a la carpeta del servidor y compilamos desde ahí
RUN cd asistencia/servidor && gradle bootJar --no-daemon

# 2. Etapa de ejecución
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# Buscamos el archivo JAR dentro de la carpeta build/libs del servidor
COPY --from=build /home/gradle/project/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
