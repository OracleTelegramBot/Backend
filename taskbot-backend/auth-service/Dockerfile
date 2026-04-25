FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR del proyecto
COPY target/authentication-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto
EXPOSE 8082

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
