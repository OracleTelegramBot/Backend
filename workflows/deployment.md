# Deployment — TaskBot Backend

## Arquitectura de despliegue

El backend corre en una **VPS** con **Apache como proxy reverso**, levantado mediante **Docker Compose**.
El pipeline de CI/CD es un GitHub Actions workflow que se dispara en cada push a `main`.

```
Developer → push → GitHub (main)
                       │
               GitHub Actions
                       │ SSH
                       ▼
                      VPS
              ┌────────────────┐
              │  Apache :80/443│  ← proxy reverso
              └───────┬────────┘
                      │
              Docker Compose (red broker-kafka)
              ├── kpi-service       :8080
              ├── telegram-service  :8081
              ├── auth-service      :8082
              ├── ai-service        :8083
              ├── task-service      :8084
              ├── kafka             :9092
              ├── zookeeper         :2181
              └── kafdrop           :19000
```

---

## Pipeline — `.github/workflows/deploy.yml`

**Trigger:** push a `main`

**Pasos:**
1. Conectarse a la VPS por SSH
2. `git pull origin main` — actualiza el código
3. `docker-compose down` — tumba los contenedores actuales
4. `docker-compose up -d --build` — reconstruye imágenes y levanta todo

**Action utilizada:** `appleboy/ssh-action@v1.0.3`

---

## Secrets requeridos en GitHub

Configurar en `Settings → Secrets and variables → Actions`:

| Secret | Descripción |
|---|---|
| `VPS_HOST` | IP o dominio de la VPS |
| `VPS_USER` | Usuario SSH (ej. `ubuntu`, `root`) |
| `VPS_SSH_KEY` | Clave privada SSH (contenido del `.pem` o `id_rsa`) |
| `VPS_PROJECT_PATH` | Ruta absoluta del repo en la VPS (ej. `/home/ubuntu/taskbot`) |

---

## Variables de entorno — Docker Compose

El `docker-compose.yml` en `taskbot-backend/` espera un archivo `.env` en esa misma carpeta con las siguientes variables:

| Variable | Usado por |
|---|---|
| `DB_URL` | auth-service, kpi-service, ai-service, task-service |
| `DB_USERNAME` | auth-service, kpi-service, ai-service, task-service |
| `DB_PASSWORD` | auth-service, kpi-service, ai-service, task-service |
| `JWT_SECRET` | auth-service, kpi-service |
| `OPENAI_API_KEY` | ai-service |
| `TELEGRAM_BOT_TOKEN` | telegram-service |

El archivo `.env` **no se versiona** (está en `.gitignore`) y debe existir manualmente en la VPS antes del primer despliegue.

---

## Rutas públicas (Apache → servicios)

| Ruta | Servicio |
|---|---|
| `/` | frontend |
| `/api/v1/auth` | auth-service:8082 |
| `/api/webhook/telegram` | telegram-service:8081 |
| `/api/anuncios` | telegram-service:8081 |
| `/api/ai` | ai-service:8083 |
| `/api/tasks` | task-service:8084 |
| `/api/sprints` | task-service:8084 |
| `/api` (catch-all) | kpi-service:8080 |
| `/swagger-auth` | auth-service:8082 |
| `/swagger-ia` | ai-service:8083 |
| `/swagger-tasks` | task-service:8084 |
| `/swagger-ui` | kpi-service:8080 |

---

## Notas

- El Wallet de Oracle (`Wallet/`) debe estar presente en `taskbot-backend/Wallet/` en la VPS. Se monta como volumen en cada contenedor que se conecta a la Autonomous DB.
- `docker-compose down` elimina los contenedores pero **no los volúmenes**, por lo que no hay pérdida de datos persistidos localmente.
- Kafdrop (`:19000`) es una UI de administración de Kafka. En producción se recomienda no exponerlo públicamente vía Apache.
