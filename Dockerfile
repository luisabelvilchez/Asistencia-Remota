# 1. Etapa de compilación
FROM maven:3.8.5-openjdk-17 AS build
COPY . .

# Entramos a la carpeta del servidor y compilamos con Maven (que es lo que usa tu servidor)
RUN cd asistencia/servidor && ./mvnw clean package -DskipTests

# 2. Etapa de ejecución
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /asistencia/servidor/target/servidor-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
