# Contrato de Infraestructura — TaskBot

**Versión:** 0.1 (borrador inicial)
**Fecha:** 19 de mayo de 2026
**Owner del documento:** Leo (Persona 1)
**Estado:** pendiente de ratificación en el kick-off

Este documento centraliza todos los nombres, decisiones y placeholders de OCIDs necesarios para que las cuatro personas del equipo trabajen en paralelo desde el día 1. Cualquier cambio a este contrato requiere acuerdo del equipo completo y se versiona en el [changelog](#changelog) al final.

---

## 1. Decisiones cerradas

### 1.1 Región y disponibilidad

| Aspecto | Valor |
|---|---|
| Región | Mexico Central (Querétaro) |
| Region identifier | `mx-queretaro-1` |
| Region key | `QRO` |
| Availability Domains | 1 |
| Fault Domains por AD | 3 |
| Estrategia HA | Distribución entre los 3 FDs de la AD única |
| DR cross-region | Fuera de alcance para Sprint 3. Peer natural futuro: `mx-monterrey-1` |

### 1.2 Terraform y state

| Aspecto | Decisión |
|---|---|
| Repositorio | `taskbot-infra` |
| Backend de state | OCI Object Storage (bucket dedicado) |
| Bootstrap del bucket | Carpeta `bootstrap/` con state local; ese mini-Terraform crea el bucket. El resto del código usa el bucket como backend remoto |
| Versión mínima de Terraform | 1.5 |
| Provider OCI | Fijado a versión específica en `versions.tf`, nunca `latest` |

### 1.3 Carga de secretos al Vault

Los valores reales de los secretos **no** se gestionan desde Terraform (evita exposición en el state file).

| Acción | Quién | Cómo |
|---|---|---|
| Crear el contenedor del Secret en Vault | Leo (Terraform) | Recurso vacío, listo para recibir el valor |
| Cargar el valor real | Leo (manual) | OCI CLI o consola, una sola vez por secret |
| Sincronizar Vault → Kubernetes Secret | Lili | A definir: External Secrets Operator, job de Helm, o init container |

### 1.4 Ownership de recursos no explícitos en el documento original

Estos recursos no aparecen asignados a ninguna persona en el reparto del doc; quedan asumidos por Leo como infraestructura base:

| Recurso | Justificación |
|---|---|
| Container Registry (`taskbot-registry`) | Lo consumen DevOps Service y OKE |
| Artifact Registry (manifests YAML) | Misma razón |
| Object Storage bucket para state Terraform | Prerrequisito de toda la cadena |

---

## 2. Tabla maestra de nombres acordados

Estos son los nombres definitivos. Cualquier referencia desde código, manifests, build_spec o tests usa exactamente estos strings.

### 2.1 Infraestructura base (Leo)

| Recurso | Nombre |
|---|---|
| Compartment | `taskbot-compartment` |
| VCN | `taskbot-vcn` |
| Subnet pública | `taskbot-public-subnet` |
| Subnet privada | `taskbot-private-subnet` |
| Vault | `taskbot-vault` |
| Container Registry namespace | `taskbot-registry` |
| Production Load Balancer | `taskbot-prod-lb` |
| Test Load Balancer | `taskbot-test-lb` |

### 2.2 Cluster y Kubernetes (Lili)

| Recurso | Nombre |
|---|---|
| Cluster OKE | `taskbot-oke-cluster` |
| Node Pool | `taskbot-oke-nodepool` |
| Namespace activo | `vs-blue` |
| Namespace inactivo | `vs-green` |
| Ingress Controller | `nginx-ingress` (namespace `ingress-nginx`) |

### 2.3 Kubernetes Secrets (Lili sincroniza desde Vault de Leo)

| Secret | Contenido |
|---|---|
| `autonomous-db-credentials` | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| `oracle-wallet` | Wallet completo, montable como volumen en `/app/Wallet` |
| `jwt-secret` | `JWT_SECRET` |
| `openai-credentials` | `OPENAI_API_KEY` |
| `telegram-credentials` | `TELEGRAM_BOT_TOKEN` |

### 2.4 DevOps Service (Sofi)

| Recurso | Nombre |
|---|---|
| DevOps Project | `taskbot-devops` |
| Code Repository (backend) | `taskbot-backend` |
| Code Repository (frontend) | `taskbot-frontend` |
| Code Repository (tests) | `taskbot-tests` |
| Code Repository (infra) | `taskbot-infra` |
| Build Pipeline (ai) | `bp-ai-service` |
| Build Pipeline (auth) | `bp-auth-service` |
| Build Pipeline (kpi) | `bp-kpi-service` |
| Build Pipeline (task) | `bp-task-service` |
| Build Pipeline (telegram) | `bp-telegram-service` |
| Build Pipeline (frontend) | `bp-frontend` |
| Build Pipeline (tests) | `bp-taskbot-tests` |
| Deployment Pipeline (ai) | `dp-ai-service` |
| Deployment Pipeline (auth) | `dp-auth-service` |
| Deployment Pipeline (kpi) | `dp-kpi-service` |
| Deployment Pipeline (task) | `dp-task-service` |
| Deployment Pipeline (telegram) | `dp-telegram-service` |
| Deployment Pipeline (frontend) | `dp-frontend` |
| OCI Functions Application | `taskbot-functions-app` |
| OCI Function (Jira) | `fn-jira-ticket-creator` |

### 2.5 Aplicación y externos

| Recurso | Valor |
|---|---|
| Dominio público | `sammy-ulfh.dev` |
| Proyecto Jira destino | `TASKBOT` |

---

## 3. Tabla de OCIDs (placeholders)

Tabla viva. Leo la actualiza conforme Terraform crea los recursos. Las demás personas referencian estos OCIDs en su código y manifests.

| Recurso | Nombre | OCID | Status | Owner |
|---|---|---|---|---|
| Tenancy | (heredado) | `<TO_FILL>` | pendiente | Leo |
| Compartment padre | (TBD) | `<TO_FILL>` | pendiente | Leo |
| Compartment proyecto | `taskbot-compartment` | `<TO_FILL>` | pendiente | Leo |
| VCN | `taskbot-vcn` | `<TO_FILL>` | pendiente | Leo |
| Subnet pública | `taskbot-public-subnet` | `<TO_FILL>` | pendiente | Leo |
| Subnet privada | `taskbot-private-subnet` | `<TO_FILL>` | pendiente | Leo |
| Internet Gateway | — | `<TO_FILL>` | pendiente | Leo |
| NAT Gateway | — | `<TO_FILL>` | pendiente | Leo |
| Vault | `taskbot-vault` | `<TO_FILL>` | pendiente | Leo |
| Master Key | — | `<TO_FILL>` | pendiente | Leo |
| Secret DB creds | — | `<TO_FILL>` | pendiente | Leo |
| Secret wallet | — | `<TO_FILL>` | pendiente | Leo |
| Secret JWT | — | `<TO_FILL>` | pendiente | Leo |
| Secret OpenAI | — | `<TO_FILL>` | pendiente | Leo |
| Secret Telegram | — | `<TO_FILL>` | pendiente | Leo |
| Secret Jira | — | `<TO_FILL>` | pendiente | Leo |
| Container Registry | `taskbot-registry` | `<TO_FILL>` | pendiente | Leo |
| Production LB | `taskbot-prod-lb` | `<TO_FILL>` | pendiente | Leo |
| Test LB | `taskbot-test-lb` | `<TO_FILL>` | pendiente | Leo |
| Log Group | — | `<TO_FILL>` | pendiente | Leo |
| Notification Topic (pipelines) | — | `<TO_FILL>` | pendiente | Leo |
| Notification Topic (Jira) | — | `<TO_FILL>` | pendiente | Leo |
| State bucket Terraform | — | `<TO_FILL>` | pendiente | Leo |
| Autonomous DB (existente) | (TBD) | `<TO_FILL>` | por verificar | Leo |
| Cluster OKE | `taskbot-oke-cluster` | `<TO_FILL>` | pendiente | Lili |
| Node Pool | `taskbot-oke-nodepool` | `<TO_FILL>` | pendiente | Lili |
| Dynamic Group DevOps | — | `<TO_FILL>` | pendiente | Leo |
| DevOps Project | `taskbot-devops` | `<TO_FILL>` | pendiente | Sofi |
| Functions Application | `taskbot-functions-app` | `<TO_FILL>` | pendiente | Sofi |
| Function Jira | `fn-jira-ticket-creator` | `<TO_FILL>` | pendiente | Sofi |

---

## 4. Convención de tags OCI

Todos los recursos provisionados llevan estos tags. Sin excepción. Sirven para cost tracking, limpieza al cerrar el proyecto y auditoría.

| Tag | Valor | Notas |
|---|---|---|
| `Project` | `TaskBot` | Constante |
| `Sprint` | `Sprint-3` | Actualizar en sprints futuros |
| `Owner` | `Leo` / `Lili` / `Sofi` / `Diana` | Quien gestiona el recurso |
| `ManagedBy` | `terraform` / `helm` / `manual` | Cómo se creó |
| `Environment` | `shared` / `vs-blue` / `vs-green` | Para recursos de Kubernetes |

Recomendación: crear un Tag Namespace de OCI (`taskbot`) y definir los tags ahí para que sean obligatorios vía policy.

---

## 5. Mapeo `.env` → Kubernetes

Espejo de la tabla 2.5 del documento de diseño. Fuente de verdad para Lili al crear Secrets y ConfigMaps.

| Variable `.env` | Tipo | Destino |
|---|---|---|
| `DB_URL` | Sensible | Secret `autonomous-db-credentials` |
| `DB_USERNAME` | Sensible | Secret `autonomous-db-credentials` |
| `DB_PASSWORD` | Sensible | Secret `autonomous-db-credentials` |
| `JWT_SECRET` | Sensible | Secret `jwt-secret` |
| `OPENAI_API_KEY` | Sensible | Secret `openai-credentials` |
| `TELEGRAM_BOT_TOKEN` | Sensible | Secret `telegram-credentials` |
| `Wallet/` (carpeta) | Sensible | Secret `oracle-wallet` montado en `/app/Wallet` |
| `TNS_ADMIN` | No sensible | ConfigMap: `/app/Wallet` |
| `ORACLE_WALLET_PATH` | No sensible | ConfigMap: `/app/Wallet` |
| `FEIGN_CLIENT_CONFIG_KPI_SERVICE_URL` | No sensible | ConfigMap: `http://kpi-service:8080` |
| `AUTH_SERVICE_URL` | No sensible | ConfigMap: `http://auth-service:8082` |
| `KPI_SERVICE_URL` | No sensible | ConfigMap: `http://kpi-service:8080` |
| `CORS_ALLOWED_ORIGINS` | No sensible | ConfigMap: `https://sammy-ulfh.dev` |

**Excluidas explícitamente del despliegue**: `SPRING_KAFKA_BOOTSTRAP_SERVERS` y `KAFKA_BROKERCONNECT`. Diana debe eliminarlas del `.env`, del `.env.example` y del código de `kpi-service` y `telegram-service` antes de la migración.

---

## 6. Mapeo Apache → NGINX Ingress

Espejo de la tabla 2.7 del documento de diseño. Fuente de verdad para Lili al escribir el Ingress Resource.

| Ruta pública | Destino Apache (hoy) | Destino Ingress (objetivo) |
|---|---|---|
| `/` | `/var/www/html/dist` | Service `frontend` |
| `/api/v1/auth` | `127.0.0.1:8082` | `auth-service:8082` |
| `/api/webhook/telegram` | `127.0.0.1:8081` | `telegram-service:8081` |
| `/api/anuncios` | `127.0.0.1:8081` | `telegram-service:8081` |
| `/api/ai` | `127.0.0.1:8083` | `ai-service:8083` |
| `/api/tasks` | `127.0.0.1:8084` | `task-service:8084` |
| `/api/sprints` | `127.0.0.1:8084` | `task-service:8084` |
| `/api` (catch-all) | `127.0.0.1:8080` | `kpi-service:8080` |
| `/swagger-auth` | `127.0.0.1:8082/swagger-ui/` | `auth-service:8082` |
| `/swagger-ia` | `127.0.0.1:8083/swagger-ui/` | `ai-service:8083` |
| `/swagger-tasks` | `127.0.0.1:8084/swagger-ui/` | `task-service:8084` |
| `/swagger-ui` | `127.0.0.1:8080/swagger-ui` | `kpi-service:8080` |
| `/v3/api-docs/*` | Por microservicio | Cada uno expone el suyo |

**Importante**: el orden de las reglas en el Ingress importa. La regla `/api` (catch-all) debe ir **después** de las rutas específicas de `/api/*`.

---

## 7. Cronograma y puntos de sincronización

| Día | Hito | Owners |
|---|---|---|
| 1 | **Sync obligatorio**: kick-off y ratificación de este contrato | Todos |
| 1-3 | Leo: VCN + Vault. Lili: manifests templates. Sofi: Code Repos vacíos. Diana: Dockerfiles + build_specs | Todos en paralelo |
| 4-5 | **Sync obligatorio**: integración inicial, primer despliegue end-to-end a `vs-blue` | Todos |
| 6-7 | Pulir Blue/Green, testing automatizado, integración Jira | Sofi + Diana |
| 8 | **Sync obligatorio**: migración de DNS de `sammy-ulfh.dev` al Production LB | Leo + Todos |
| 9 | Pruebas de rollback intencional, validación end-to-end | Todos |
| 10 | Grabación del video del Sprint | Diana |

Stand-ups diarios de 15 minutos entre los puntos de sync.

---

## 8. Limpieza pendiente antes de migrar (Diana)

Antes del primer push a `taskbot-backend`, el código debe estar listo:

- [ ] Eliminar `SPRING_KAFKA_BOOTSTRAP_SERVERS` y `KAFKA_BROKERCONNECT` de `.env` y `.env.example`
- [ ] Eliminar todo código que consuma Kafka en `kpi-service`
- [ ] Eliminar todo código que consuma Kafka en `telegram-service`
- [ ] Verificar que `Wallet/` está en `.gitignore` y revisar el historial Git por si se subió alguna vez
- [ ] Verificar que `.env` está en `.gitignore` y revisar el historial Git
- [ ] Confirmar que cada microservicio tiene su Dockerfile listo y compatible con el build de OCI
- [ ] Confirmar que cada `application.yml` lee variables de entorno (no las hardcodea)
- [ ] Separar el repo del frontend del backend (si todavía no lo está)

---

## 9. Decisiones aún pendientes (a cerrar en el kick-off)

- [ ] Compartment padre: ¿root o intermediario? Si la tenancy es compartida con otros proyectos, conviene un compartment padre dedicado
- [ ] Versión de Kubernetes para OKE (recomendado: la LTS más reciente disponible en `mx-queretaro-1`)
- [ ] Versión y distribución de Java en el Dockerfile base (`eclipse-temurin:17-jre`, `21-jre`, otra)
- [ ] Estrategia de tags de imágenes Docker: ¿solo SHA del commit + `latest`, o también semver?
- [ ] Shape y tamaño del Node Pool de OKE para Sprint 3 (sugerido mínimo: 3 nodos `VM.Standard.E4.Flex` con 2 OCPU / 16 GB cada uno, uno por fault domain)
- [ ] Confirmar OCID y región actual de la Autonomous AI Database existente
- [ ] Quién hace el primer push de prueba a `taskbot-backend` y cuándo (sugerido: Diana, día 4)
- [ ] Estrategia exacta de sincronización Vault → Kubernetes Secret (External Secrets Operator vs. job manual)

---

## 10. Bloqueos cruzados (referencia rápida)

| Quien bloquea | A quién | Recursos críticos que producen el bloqueo |
|---|---|---|
| Leo | Lili, Sofi, Diana | VCN, Vault, LBs, Container Registry, DNS, dynamic group de DevOps |
| Lili | Sofi, Diana | Cluster OKE, namespaces, Ingress, Secrets sincronizados |
| Sofi | Diana | URLs de Code Repos, plantillas de `build_spec.yaml` |
| Diana | (nadie) | Consumidora final |

Mitigación clave: este contrato existe precisamente para que cada persona pueda trabajar contra **nombres acordados** sin esperar a que los OCIDs reales existan.

---

## Changelog

| Fecha | Versión | Cambios | Autor |
|---|---|---|---|
| 2026-05-19 | 0.1 | Borrador inicial: nombres ratificables, decisiones cerradas sobre región, Terraform backend, ownership de Container Registry y Artifact Registry, mapeos y cronograma | Leo |
