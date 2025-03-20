FROM ubuntu:latest AS build
LABEL authors="ortiz"

# Instalar dependencias
RUN apt-get update && apt-get install -y openjdk-17-jdk maven

# Copiar el código fuente y construir la aplicación con Maven
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Segunda etapa: imagen final para ejecutar el JAR
FROM openjdk:17-jdk-slim
EXPOSE 8080

# Copiar el JAR compilado desde la imagen de construcción
COPY --from=build /app/target/*.jar app.jar

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
