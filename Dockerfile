# 1. Etapa de construcción (Usamos JDK 23)
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY . .

# Compilamos buscando el pom.xml en tus subcarpetas (como asistencia_remota)
RUN find . -name "pom.xml" -exec mvn clean package -DskipTests -f {} \;

# 2. Etapa de ejecución (Usamos JDK 23)
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# Buscamos el .jar generado y lo traemos aquí como app.jar
COPY --from=build /app/**/target/*.jar ./app.jar

# Vinculamos tus variables de Render (MYSQLHOST, MYSQLUSER, etc.)
ENV SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQL_DATABASE}
ENV SPRING_DATASOURCE_USERNAME=${MYSQLUSER}
ENV SPRING_DATASOURCE_PASSWORD=${MYSQLPASSWORD}

EXPOSE 8080

# Ejecución con Java 23
ENTRYPOINT ["java", "-jar", "app.jar"]
