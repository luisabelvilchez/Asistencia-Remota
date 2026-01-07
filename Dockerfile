# 1. Etapa de compilación
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .

# Forzamos el uso de Java 21 del sistema y saltamos detección de toolchain
RUN cd asistencia/servidor && \
    chmod +x gradlew && \
    ./gradlew bootJar --no-daemon \
    -Porg.gradle.java.installations.auto-download=false \
    -Porg.gradle.java.installations.auto-detect=false \
    -Dorg.gradle.java.home=/opt/java/openjdk

# 2. Etapa de ejecución
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
# Límites de memoria para que el plan gratuito de Render no lo mate
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
