# 1. Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos todo el contenido
COPY . .

# Forzamos a Maven a buscar el pom.xml en cualquier nivel y construir desde ahí
RUN find . -name "pom.xml" -exec mvn -f {} clean package -DskipTests \;

# 2. Etapa de ejecución
# ... (mantén la etapa 1 de "build" igual ya que esa funcionó)

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
# Buscamos el .jar y lo copiamos forzadamente
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Verifica que no haya espacios extra aquí:
ENTRYPOINT ["java", "-jar", "app.jar"]
