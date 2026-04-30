# AI Service

Microservicio de inteligencia artificial para TaskBot Backend. Proporciona funcionalidades de procesamiento de lenguaje natural, análisis de datos y generación de respuestas inteligentes.

## Descripción

El servicio de IA es responsable de:
- Procesar consultas en lenguaje natural
- Generar respuestas automáticas
- Análisis de sentimientos y clasificación de texto
- Integración con APIs de IA (ej. OpenAI, etc.)

## Tecnologías

- **Java 17**
- **Spring Boot 3.3.2**
- **Spring Web & Data JPA**
- **OpenAI GPT-3.5-turbo** (gratuito con límites)
- **Oracle Database** (con Wallet)

## Puertos

- **8083** - Puerto por defecto del servicio

## Configuración

1. **API Key de OpenAI**: Ya configurada en `application.properties`
2. **Oracle Wallet**: Copiado desde `auth-service` y configurado automáticamente
3. **Credenciales Oracle**: Reemplaza `USER` y `PASSWORD` en `application.properties` con tus credenciales reales

## Endpoints

- `GET /health` - Verificar estado del servicio
- `POST /ai/analyze-dashboard` - Generar recomendaciones automáticas para el dashboard (envía SprintData)
- `POST /ai/chat` - Interactuar con el chatbot (envía mensaje como string)
- `POST /ai/analyze` - Endpoint de ejemplo para análisis de texto

## Funcionalidad de IA

### Asistente Automático
- Analiza tareas activas, tiempos estimados vs reales, carga de trabajo
- Genera recomendaciones de priorización, alertas de riesgo, detección de sobrecarga

### Chatbot Interactivo
- Responde preguntas sobre tareas, recomendaciones, estado del equipo
- Ejemplos: "¿Qué tarea debería hacer ahora?", "¿Por qué esta tarea está en riesgo?"

## Base de Datos

Conectado a **Oracle Database** usando Wallet para autenticación segura:
- **Wallet Path**: `/src/main/resources/Wallet`
- **Connection**: `jdbc:oracle:thin:@basegestiondetareas_tp`
- **JPA**: Configurado con Hibernate y dialecto Oracle

## Ejemplo de Uso

Para el dashboard, envía datos del sprint a `/ai/analyze-dashboard`:

```json
{
  "activeTasks": [
    {
      "id": "1",
      "name": "Implementar login",
      "status": "active",
      "assignedTo": "user1",
      "estimatedHours": 8,
      "actualHours": 6,
      "priority": "high"
    }
  ],
  "users": [
    {
      "id": "user1",
      "name": "Juan",
      "assignedTasks": [...],
      "totalEstimatedHours": 40,
      "totalActualHours": 30
    }
  ]
}
```