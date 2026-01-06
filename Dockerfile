# 1. Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Buscamos y compilamos el proyecto
RUN find . -name "pom.xml" -exec mvn -f {} clean package -DskipTests \;

# 2. Etapa de ejecución
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Esta línea es la clave: busca CUALQUIER .jar generado y lo trae aquí
COPY --from=build /app/**/target/*.jar ./app.jar

EXPOSE 8080

# Comando de inicio corregido (SIN espacios extra)
ENTRYPOINT ["java", "-jar", "app.jar"]
