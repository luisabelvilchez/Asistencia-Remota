# 1. Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Buscamos el pom.xml y compilamos
RUN find . -name "pom.xml" -exec mvn -f {} clean package -DskipTests \;

# 2. Etapa de ejecución
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Copiamos el archivo .jar generado a la carpeta actual como app.jar
# Usamos un comodín para encontrarlo sin importar el nombre original
COPY --from=build /app/**/target/*.jar app.jar

EXPOSE 8080

# IMPORTANTE: Sin espacios extra dentro de las comillas
ENTRYPOINT ["java", "-jar", "app.jar"]
