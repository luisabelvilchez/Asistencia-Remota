# 1. Etapa de construcción
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY . .

WORKDIR /app/asistencia_remota
RUN mvn clean package -DskipTests

# 2. Etapa de ejecución
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

COPY --from=build /app/asistencia_remota/target/*.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQL_DATABASE}
ENV SPRING_DATASOURCE_USERNAME=${MYSQLUSER}
ENV SPRING_DATASOURCE_PASSWORD=${MYSQLPASSWORD}

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
