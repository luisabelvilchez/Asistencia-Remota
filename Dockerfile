# 1. Etapa de compilación
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .

# Usamos -Porg.gradle.java.installations.auto-download=false para evitar que intente bajar Java
RUN cd asistencia/servidor && \
    chmod +x gradlew && \
    ./gradlew bootJar --no-daemon -Porg.gradle.java.installations.auto-download=false

# 2. Etapa de ejecución
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/asistencia/servidor/build/libs/*.jar app.jar
EXPOSE 8080
# Agregamos parámetros de memoria para que no falle en el plan gratuito de Render
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
