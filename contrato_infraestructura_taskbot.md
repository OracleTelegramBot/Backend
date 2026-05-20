# Contrato de Infraestructura — TaskBot

**Versión:** 0.2
**Fecha:** 19 de mayo de 2026
**Owner del documento:** Leo (Persona 1)
**Estado:** Capa base de Leo aplicada parcialmente (compartment + IAM + networking). Pendientes: Vault, Container Registry, Load Balancers, Observabilidad.

Este documento centraliza todos los nombres, decisiones y OCIDs reales del proyecto. Cualquier cambio requiere acuerdo del equipo y se versiona en el [changelog](#changelog) al final.

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
| Bucket de state | `taskbot-terraform-state` (namespace: `ax5o32ww5jyq`) |
| Bootstrap del bucket | Carpeta `bootstrap/` con state local. Ya aplicado |
| Versión mínima de Terraform / OpenTofu | 1.6 |
| Provider OCI | `oracle/oci ~> 6.0` |

### 1.3 Carga de secretos al Vault

Los valores reales de los secretos **no** se gestionan desde Terraform.

| Acción | Quién | Cómo |
|---|---|---|
| Crear el contenedor del Secret en Vault | Leo (Terraform) | Recurso con placeholder, lifecycle ignora cambios al contenido |
| Cargar el valor real | Leo (manual) | OCI CLI o consola, una sola vez por secret |
| Sincronizar Vault → Kubernetes Secret | Lili | A definir: External Secrets Operator, job de Helm, o init container |

### 1.4 Ownership de recursos no explícitos en el documento original

| Recurso | Justificación |
|---|---|
| Container Registry (`taskbot-registry`) | Lo consumen DevOps Service y OKE |
| Artifact Registry (manifests YAML) | Misma razón |
| Object Storage bucket para state Terraform | Prerrequisito de toda la cadena |

---

## 2. Tabla maestra de nombres acordados

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

## 3. Tabla de OCIDs

Status `creado` = recurso ya provisionado y verificado. `pendiente` = no creado aún. `por verificar` = existe en OCI pero falta capturar el OCID exacto.

### Tenancy y bootstrap

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Tenancy | `ocid1.tenancy.oc1..aaaaaaaave7wm7hohju22j2hrazrrvp7jgwcty25dwosrffoefpufjxbgmfq` | creado | (heredado) |
| State bucket Terraform | `taskbot-terraform-state` (namespace `ax5o32ww5jyq`) | creado | Leo |

### Compartment del proyecto

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Compartment `taskbot-compartment` | `ocid1.compartment.oc1..aaaaaaaasc7skqcllhtzaohqv2ee4c7qznox74hxo6bftrknasa232p2lloq` | creado | Leo |

### Networking

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| VCN `taskbot-vcn` | `ocid1.vcn.oc1.mx-queretaro-1.amaaaaaazghwoyqapug2d5yxmqrmcl327xruyi5jdsk2djzmhbnsqxd42kzq` | creado | Leo |
| Subnet pública | `ocid1.subnet.oc1.mx-queretaro-1.aaaaaaaah24t7t2n77yq6adg4szmozoe244mu5qpeym2blxtklqwpifocvrq` | creado | Leo |
| Subnet privada | `ocid1.subnet.oc1.mx-queretaro-1.aaaaaaaamz6ymy22vkwxqzr6ipsygg42xcnoqdavfdh7nadazhtjaz5ghsta` | creado | Leo |
| Internet Gateway | `ocid1.internetgateway.oc1.mx-queretaro-1.aaaaaaaarq5d3kpmkm2nw7autwer7p4gbtexd2g3grm5opvkbt2h5rzggjgq` | creado | Leo |
| NAT Gateway | `ocid1.natgateway.oc1.mx-queretaro-1.aaaaaaaadif2dvfgt5xk6upmp6mettt26gn5ll7gaqtcfd2dnnpmwmkj5scq` | creado | Leo |
| Service Gateway | `ocid1.servicegateway.oc1.mx-queretaro-1.aaaaaaaasepcotulw3ez3gbbs2rjezxdqrxc2bmopfxyj444siaf2peau72a` | creado | Leo |

### IAM

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Dynamic Group DevOps | `ocid1.dynamicgroup.oc1..aaaaaaaao6bdgm6rvs7rzamuysa6tapyxfha4p4o7d6h32bu4reqrlsr7lzq` | creado | Leo |
| Dynamic Group OKE | `ocid1.dynamicgroup.oc1..aaaaaaaay5gtfewvwumq33763wjm76fj6cafobwu74gh2vhkojhkuc67ob2a` | creado | Leo |
| Dynamic Group Functions | `ocid1.dynamicgroup.oc1..aaaaaaaaqnkcp53m2mbebwcciqvrbk5ica6mahkfps7qykh2s5qo6dbh6mnq` | creado | Leo |
| Policy DevOps | (ver consola) | creado | Leo |
| Policy OKE | (ver consola) | creado | Leo |
| Policy Functions | (ver consola) | creado | Leo |
| Policy OKE service | (ver consola) | creado | Leo |

### Vault y secretos

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Vault `taskbot-vault` | `<TO_FILL>` | pendiente | Leo |
| Master Key | `<TO_FILL>` | pendiente | Leo |
| Secret DB creds | `<TO_FILL>` | pendiente | Leo |
| Secret wallet | `<TO_FILL>` | pendiente | Leo |
| Secret JWT | `<TO_FILL>` | pendiente | Leo |
| Secret OpenAI | `<TO_FILL>` | pendiente | Leo |
| Secret Telegram | `<TO_FILL>` | pendiente | Leo |
| Secret Jira | `<TO_FILL>` | pendiente | Leo |

### Container Registry

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Repo `ai-service` | `<TO_FILL>` | pendiente | Leo |
| Repo `auth-service` | `<TO_FILL>` | pendiente | Leo |
| Repo `kpi-service` | `<TO_FILL>` | pendiente | Leo |
| Repo `task-service` | `<TO_FILL>` | pendiente | Leo |
| Repo `telegram-service` | `<TO_FILL>` | pendiente | Leo |
| Repo `frontend` | `<TO_FILL>` | pendiente | Leo |

### Load Balancers

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Production LB `taskbot-prod-lb` | `<TO_FILL>` | pendiente | Leo |
| Test LB `taskbot-test-lb` | `<TO_FILL>` | pendiente | Leo |

### Observabilidad

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Log Group | `<TO_FILL>` | pendiente | Leo |
| Notification Topic (pipelines) | `<TO_FILL>` | pendiente | Leo |
| Notification Topic (Jira) | `<TO_FILL>` | pendiente | Leo |

### Externos y otras capas

| Recurso | OCID | Status | Owner |
|---|---|---|---|
| Autonomous DB (existente) | `<TO_FILL>` | por verificar | Leo |
| Cluster OKE | `<TO_FILL>` | pendiente | Lili |
| Node Pool | `<TO_FILL>` | pendiente | Lili |
| DevOps Project | `<TO_FILL>` | pendiente | Sofi |
| Functions Application | `<TO_FILL>` | pendiente | Sofi |
| Function Jira | `<TO_FILL>` | pendiente | Sofi |

---

## 4. Convención de tags OCI

| Tag | Valor | Notas |
|---|---|---|
| `Project` | `TaskBot` | Constante |
| `Sprint` | `Sprint-3` | Actualizar en sprints futuros |
| `Owner` | `Leo` / `Lili` / `Sofi` / `Diana` | Quien gestiona el recurso |
| `ManagedBy` | `terraform` / `helm` / `manual` | Cómo se creó |
| `Environment` | `shared` / `vs-blue` / `vs-green` | Para recursos de Kubernetes |

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

---

## 8. Limpieza pendiente antes de migrar (Diana)

- [ ] Eliminar `SPRING_KAFKA_BOOTSTRAP_SERVERS` y `KAFKA_BROKERCONNECT` de `.env` y `.env.example`
- [ ] Eliminar todo código que consuma Kafka en `kpi-service`
- [ ] Eliminar todo código que consuma Kafka en `telegram-service`
- [ ] Verificar que `Wallet/` está en `.gitignore` y revisar el historial Git
- [ ] Verificar que `.env` está en `.gitignore` y revisar el historial Git
- [ ] Confirmar que cada microservicio tiene su Dockerfile listo y compatible con el build de OCI
- [ ] Confirmar que cada `application.yml` lee variables de entorno (no las hardcodea)
- [ ] Separar el repo del frontend del backend (si todavía no lo está)

---

## 9. Decisiones aún pendientes

- [ ] Versión de Kubernetes para OKE (recomendado: la LTS más reciente disponible en `mx-queretaro-1`)
- [ ] Versión y distribución de Java en el Dockerfile base (`eclipse-temurin:17-jre`, `21-jre`, otra)
- [ ] Estrategia de tags de imágenes Docker: SHA del commit + `latest`, o también semver
- [ ] Shape y tamaño del Node Pool de OKE: 3 nodos `VM.Standard.E4.Flex` con 2 OCPU / 16 GB cada uno (uno por fault domain) — confirmar con Lili
- [ ] Network plugin del cluster (VCN-native vs flannel) — propuesta: VCN-native, confirmar con Lili
- [ ] Confirmar OCID y región de la Autonomous AI Database existente
- [ ] Quién hace el primer push de prueba a `taskbot-backend` y cuándo (sugerido: Diana, día 4)
- [ ] Estrategia de sincronización Vault → Kubernetes Secret (External Secrets Operator vs job manual)

---

## 10. Bloqueos cruzados

| Quien bloquea | A quién | Recursos críticos |
|---|---|---|
| Leo | Lili, Sofi, Diana | VCN, Vault, LBs, Container Registry, DNS, dynamic group de DevOps |
| Lili | Sofi, Diana | Cluster OKE, namespaces, Ingress, Secrets sincronizados |
| Sofi | Diana | URLs de Code Repos, plantillas de `build_spec.yaml` |
| Diana | (nadie) | Consumidora final |

---

## Changelog

| Fecha | Versión | Cambios | Autor |
|---|---|---|---|
| 2026-05-19 | 0.1 | Borrador inicial: nombres ratificables, decisiones cerradas sobre región, Terraform backend, ownership de Container Registry y Artifact Registry, mapeos y cronograma | Leo |
| 2026-05-19 | 0.2 | Aplicado bootstrap + compartment + IAM + networking (18 recursos creados). OCIDs reales en sección 3 para: tenancy, compartment, VCN, ambas subnets, IGW, NAT GW, Service GW, los 3 dynamic groups. Pendientes restantes de la capa de Leo: Vault, Container Registry, Load Balancers, Observabilidad | Leo |
