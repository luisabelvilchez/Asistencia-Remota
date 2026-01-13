# 1. Etapa de compilación
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .

# Instalamos herramientas básicas y compilamos
RUN apt-get update && apt-get install -y findutils && rm -rf /var/lib/apt/lists/*
RUN cd asistencia/servidor && chmod +x gradlew && ./gradlew bootJar --no-daemon

# 2. Etapa de ejecución
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
