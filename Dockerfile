# 1. Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .

# Este comando busca CUALQUIER pom.xml en cualquier subcarpeta y lo compila
RUN find . -name "pom.xml" -exec mvn clean package -DskipTests -f {} \;

# 2. Etapa de ejecución
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Buscamos el .jar generado en cualquier carpeta target y lo traemos aquí
COPY --from=build /app/**/target/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]-
