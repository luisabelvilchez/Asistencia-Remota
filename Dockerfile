# 1. Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Buscamos el pom.xml y compilamos
RUN find . -name "pom.xml" -exec mvn -f {} clean package -DskipTests \;

# 2. Etapa de ejecución
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Esta línea busca CUALQUIER .jar generado en cualquier subcarpeta y lo trae aquí
# Esto es necesario porque tienes muchos proyectos juntos
RUN find /app -name "*.jar" -exec cp {} ./app.jar \;

EXPOSE 8080

# Comando de inicio corregido
ENTRYPOINT ["java", "-jar", "app.jar"]
