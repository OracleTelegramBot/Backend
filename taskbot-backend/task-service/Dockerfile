FROM eclipse-temurin:17-jre-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR del proyecto
COPY target/*.jar app.jar

# Exponer el puerto
EXPOSE 8084

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
