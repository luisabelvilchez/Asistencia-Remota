# 1. Etapa de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Buscamos el pom.xml y compilamos
RUN find . -name "pom.xml" -exec mvn -f {} clean package -DskipTests \;

# 2. Etapa de ejecución
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# Rescatamos el archivo .jar generado
RUN find /app -name "*.jar" -not -path "*/target/*-sources.jar" -exec cp {} ./app.jar \;

# Vinculamos tus variables de Render con Spring Boot
ENV SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQL_DATABASE}
ENV SPRING_DATASOURCE_USERNAME=${MYSQLUSER}
ENV SPRING_DATASOURCE_PASSWORD=${MYSQLPASSWORD}

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
