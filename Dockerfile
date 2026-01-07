# 1. Etapa de compilación
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .

# Instalamos findutils por si acaso
RUN apt-get update && apt-get install -y findutils && rm -rf /var/lib/apt/lists/*

# COMANDO CLAVE: Forzamos a Gradle a usar Java 21 e ignorar el toolchain de Java 24
RUN cd asistencia/servidor && \
    chmod +x gradlew && \
    ./gradlew bootJar --no-daemon \
    -Porg.gradle.java.installations.auto-download=false \
    -Dorg.gradle.java.home=/opt/java/openjdk
    
# 2. Etapa de ejecución
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
# Límites de memoria para el plan gratuito
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
