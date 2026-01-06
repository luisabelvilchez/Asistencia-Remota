# 1. Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Buscamos el pom.xml en cualquier nivel y compilamos
RUN find . -name "pom.xml" -exec mvn -f {} clean package -DskipTests \;

# 2. Etapa de ejecución
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Buscamos CUALQUIER .jar que se haya generado en cualquier carpeta target y lo traemos aquí
RUN find /app -name "*.jar" -not -path "*/target/*-sources.jar" -exec cp {} ./app.jar \;

EXPOSE 8080

# Comando de inicio limpio
ENTRYPOINT ["java", "-jar", "app.jar"]
