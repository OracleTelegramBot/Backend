# ==========================================
# ETAPA 1: Construcción (Build)
# Usa una imagen pesada que SÍ tiene Maven instalado
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia los archivos de configuración y el código fuente
COPY pom.xml .
COPY src ./src

# Compila el proyecto saltándose las pruebas para empaquetar más rápido
RUN mvn clean package -DskipTests

# ==========================================
# ETAPA 2: Ejecución (Run)
# Usa la imagen súper ligera solo con el entorno de Java
# ==========================================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia EL RESULTADO (.jar) de la Etapa 1 a esta nueva imagen limpia
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto (opcional, por convención)
EXPOSE 8080

# Levanta el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]
