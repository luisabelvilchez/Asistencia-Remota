FROM maven:3.8.5-openjdk-17 AS build
COPY . .
# Este comando ayuda a Maven a encontrar el pom.xml si está en una subcarpeta
RUN mvn clean package -DskipTests -f pom.xml || mvn clean package -DskipTests -f */pom.xml

FROM openjdk:17.0.1-jdk-slim
# Buscamos el archivo .jar donde sea que se haya generado
COPY --from=build /**/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
