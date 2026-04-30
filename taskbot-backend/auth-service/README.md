# Authentication Service

Microservicio de autenticación para TaskBot Backend. Proporciona funcionalidades de login, validación de tokens JWT y gestión de usuarios.

## Descripción

El servicio de autenticación es responsable de:
- Autenticar usuarios y generar tokens JWT
- Validar tokens JWT
- Gestionar usuarios (creación, actualización, desactivación)
- Integración con Oracle Database mediante Wallet

## Tecnologías

- **Java 17**
- **Spring Boot 4.0.5**
- **Spring Security**
- **Spring Data JPA**
- **JWT (JSON Web Tokens)** - jjwt 0.11.5
- **Oracle JDBC Driver**
- **Lombok**
- **Swagger/OpenAPI 3.0**

## Estructura del Proyecto

```
auth-service/
├── src/main/java/dev/sammy_ulfh/authentication/
│   ├── AuthenticationApplication.java       # Clase principal
│   ├── config/
│   │   └── SecurityConfig.java             # Configuración de seguridad
│   ├── controller/
│   │   └── AuthenticationController.java   # Endpoints REST
│   ├── service/
│   │   └── AuthenticationService.java      # Lógica de negocio
│   ├── model/
│   │   ├── entity/
│   │   │   └── User.java                   # Entidad de usuario
│   │   └── dto/
│   │       ├── AuthRequest.java            # DTO de login
│   │       └── AuthResponse.java           # DTO de respuesta
│   ├── repository/
│   │   └── UserRepository.java             # Acceso a datos
│   ├── security/
│   │   └── JwtTokenProvider.java           # Proveedor de JWT
│   └── exception/
│       ├── ErrorResponse.java              # DTO de error
│       └── GlobalExceptionHandler.java     # Manejo de excepciones
├── src/main/resources/
│   ├── application.properties               # Configuración de la aplicación
│   └── Wallet/                              # Credenciales de Oracle
├── pom.xml                                  # Dependencias Maven
├── mvnw / mvnw.cmd                          # Maven Wrapper
└── README.md                                # Este archivo
```

## Requisitos Previos

- Java 17 o superior
- Maven 3.8.0 o superior (opcional si usas mvnw)
- Oracle Wallet configurado en `src/main/resources/Wallet/`

## Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd taskbot-backend/auth-service
```

### 2. Configurar las propiedades
Edita `src/main/resources/application.properties`:

```properties
# Oracle Database
oracle.wallet.path="/ruta/a/wallet"
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# JWT Secret (cambiar en producción)
jwt.secret=tu_secret_key_aqui
jwt.expiration=86400000
```

### 3. Compilar el proyecto
```bash
./mvnw clean install
```

O con Maven instalado globalmente:
```bash
mvn clean install
```

## Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8081`

## Endpoints Principales

### 1. Login
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "contraseña"
}
```

**Respuesta exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "usuario",
  "email": "usuario@email.com"
}
```

### 2. Validar Token
```bash
POST /api/v1/auth/validate
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Respuesta exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "username": "usuario",
  "email": "usuario@email.com"
}
```

### 3. Health Check
```bash
GET /api/v1/auth/health
```

**Respuesta (200):**
```
Authentication Service is running
```

## Documentación API (Swagger)

Accede a la documentación interactiva en:
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Autenticación

El servicio utiliza **JWT (JSON Web Tokens)** para autenticación. Incluye el token en el header:

```
Authorization: Bearer <token>
```

## Manejo de Errores

El servicio devuelve errores con la siguiente estructura:

```json
{
  "message": "Usuario no encontrado",
  "error": "Invalid username or password",
  "status": 401,
  "timestamp": 1618000000000
}
```

### Códigos de Estado Comunes
- `200 OK` - Operación exitosa
- `400 Bad Request` - Parámetros inválidos
- `401 Unauthorized` - Credenciales inválidas o token expirado
- `500 Internal Server Error` - Error del servidor

## Base de Datos

### Tabla USERS
```sql
CREATE TABLE USERS (
    ID NUMBER PRIMARY KEY,
    USERNAME VARCHAR2(50) NOT NULL UNIQUE,
    PASSWORD VARCHAR2(255) NOT NULL,
    EMAIL VARCHAR2(100) NOT NULL UNIQUE,
    FIRST_NAME VARCHAR2(50),
    LAST_NAME VARCHAR2(50),
    IS_ACTIVE CHAR(1) DEFAULT 'Y',
    CREATED_AT NUMBER,
    UPDATED_AT NUMBER
);
```

## Configuración de Oracle Wallet

1. Descarga la Wallet desde Oracle Cloud Console
2. Coloca los archivos en `src/main/resources/Wallet/`
3. Configura la ruta en `application.properties`
4. Asegúrate de que `ojdbc.properties` tenga la configuración correcta

## Construcción y Despliegue

### Construir JAR
```bash
./mvnw clean package
```

El JAR se generará en: `target/authentication-0.0.1-SNAPSHOT.jar`

### Ejecutar JAR
```bash
java -jar target/authentication-0.0.1-SNAPSHOT.jar
```

### Construir imagen Docker (opcional)
```bash
docker build -t auth-service:1.0 .
docker run -p 8081:8081 auth-service:1.0
```

## Pruebas

```bash
./mvnw test
```

## Contribución

Por favor, sigue estas pautas al contribuir:
1. Crea un branch para tu feature: `git checkout -b feature/nombre`
2. Commit tus cambios: `git commit -am 'Agrega nueva feature'`
3. Push al branch: `git push origin feature/nombre`
4. Abre un Pull Request

## Licencia

Este proyecto es propiedad de Tecnológico de Monterrey.

## Contacto

Para preguntas o soporte, contacta al equipo de desarrollo del backend.

---

**Última actualización:** Abril 2026
