# 1. Etapa de build (Java + Ant)
FROM eclipse-temurin:23-jdk AS build
WORKDIR /app

# Instalamos ant
RUN apt-get update && apt-get install -y ant

# Copiamos el proyecto
COPY . .

# Entramos a la carpeta donde está build.xml
WORKDIR /app/asistencia_remota

# Compilamos con ANT
RUN ant jar

# 2. Etapa de ejecución
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# Copiamos el jar generado por ANT
COPY --from=build /app/asistencia_remota/dist/*.jar app.jar

ENV SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQL_DATABASE}
ENV SPRING_DATASOURCE_USERNAME=${MYSQLUSER}
ENV SPRING_DATASOURCE_PASSWORD=${MYSQLPASSWORD}

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
