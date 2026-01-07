# 1. Etapa de compilación
FROM openjdk:24-slim AS build
WORKDIR /app
COPY . .

# Instalamos findutils para que Gradle funcione bien en imágenes slim
RUN apt-get update && apt-get install -y findutils && rm -rf /var/lib/apt/lists/*

# Forzamos la compilación ignorando la restricción de toolchain
RUN cd asistencia/servidor && \
    chmod +x gradlew && \
    ./gradlew bootJar --no-daemon -Porg.gradle.java.installations.auto-download=false
    
# 2. Etapa de ejecución
FROM openjdk:24-slim
WORKDIR /app
COPY --from=build /app/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
