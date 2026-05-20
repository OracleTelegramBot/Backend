# Contrato de Infraestructura — TaskBot

**Versión:** 0.3
**Fecha:** 19 de mayo de 2026
**Owner del documento:** Leo (Persona 1)
**Estado:** Capa de infraestructura de Leo **completa**. Pendientes: cargar valores reales a los Secrets del Vault, y verificación de conectividad VCN → Autonomous DB (requiere cluster OKE de Lili).

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
| DR cross-region | Fuera de alcance para Sprint 3 |

### 1.2 Terraform y state

| Aspecto | Decisión |
|---|---|
| Repositorio | `taskbot-infra` |
| Backend de state | OCI Object Storage (bucket `taskbot-terraform-state`, namespace `ax5o32ww5jyq`) |
| Versión mínima de Terraform / OpenTofu | 1.6 |
| Provider OCI | `oracle/oci ~> 6.0` |

### 1.3 Carga de secretos al Vault

Los valores reales de los secretos **no** se gestionan desde Terraform. Los contenedores de secrets están creados con un placeholder y el lifecycle de Terraform ignora cambios al contenido. Leo carga los valores reales con OCI CLI (ver [Apéndice](#apéndice-comandos-para-cargar-secretos-al-vault)).

### 1.4 Ownership de recursos no explícitos en el documento original

| Recurso | Justificación |
|---|---|
| Container Registry (`taskbot-registry`) | Lo consumen DevOps Service y OKE |
| Artifact Registry (manifests YAML) | Misma razón |
| Object Storage bucket para state Terraform | Prerrequisito de toda la cadena |

### 1.5 Adaptación arquitectónica: 1 solo Load Balancer

Por restricción de cuota del Always Free tier de OCI (`max-nlb-flexible-count = 1`, no aumentable sin upgrade a paid), el proyecto usa **un solo Network Load Balancer** en lugar de los dos contemplados originalmente. El esquema Blue/Green sigue siendo viable:

- El LB único (`taskbot-prod-lb`, IP `159.54.158.212`) apunta al NGINX Ingress del cluster
- El namespace activo lo determina el Ingress Resource de Lili (vía cambio de labels/selectors)
- Para validar el namespace **inactivo** antes del traffic shift, Lili escoge entre dos enfoques:
  1. **Host-based routing en NGINX Ingress**: `sammy-ulfh.dev` → namespace activo, `test.sammy-ulfh.dev` → namespace inactivo (ambos hosts atendidos por el mismo LB)
  2. **Tests internos al cluster**: el repositorio `taskbot-tests` corre como Kubernetes Job dentro del cluster, hitting Service DNS interno (`http://kpi-service.vs-green:8080`, etc.)

La decisión exacta queda en cancha de Lili durante su implementación.

Si en el futuro la cuenta se upgradea a paid y la cuota sube, se puede volver al esquema de 2 LBs reintroduciendo el resource `test` en `modules/load_balancers/main.tf`.

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
| Production Load Balancer | `taskbot-prod-lb` (LB único, ver sección 1.5) |

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
| Build Pipelines | `bp-ai-service`, `bp-auth-service`, `bp-kpi-service`, `bp-task-service`, `bp-telegram-service`, `bp-frontend`, `bp-taskbot-tests` |
| Deployment Pipelines | `dp-ai-service`, `dp-auth-service`, `dp-kpi-service`, `dp-task-service`, `dp-telegram-service`, `dp-frontend` |
| OCI Functions Application | `taskbot-functions-app` |
| OCI Function (Jira) | `fn-jira-ticket-creator` |

### 2.5 Aplicación y externos

| Recurso | Valor |
|---|---|
| Dominio público | `sammy-ulfh.dev` |
| Proyecto Jira destino | `TASKBOT` |

---

## 3. Tabla de OCIDs

### Tenancy y bootstrap

| Recurso | OCID | Status |
|---|---|---|
| Tenancy | `ocid1.tenancy.oc1..aaaaaaaave7wm7hohju22j2hrazrrvp7jgwcty25dwosrffoefpufjxbgmfq` | creado |
| State bucket Terraform | `taskbot-terraform-state` (namespace `ax5o32ww5jyq`) | creado |

### Compartment

| Recurso | OCID | Status |
|---|---|---|
| `taskbot-compartment` | `ocid1.compartment.oc1..aaaaaaaasc7skqcllhtzaohqv2ee4c7qznox74hxo6bftrknasa232p2lloq` | creado |

### Networking

| Recurso | OCID | Status |
|---|---|---|
| VCN `taskbot-vcn` | `ocid1.vcn.oc1.mx-queretaro-1.amaaaaaazghwoyqapug2d5yxmqrmcl327xruyi5jdsk2djzmhbnsqxd42kzq` | creado |
| Subnet pública | `ocid1.subnet.oc1.mx-queretaro-1.aaaaaaaah24t7t2n77yq6adg4szmozoe244mu5qpeym2blxtklqwpifocvrq` | creado |
| Subnet privada | `ocid1.subnet.oc1.mx-queretaro-1.aaaaaaaamz6ymy22vkwxqzr6ipsygg42xcnoqdavfdh7nadazhtjaz5ghsta` | creado |
| Internet Gateway | `ocid1.internetgateway.oc1.mx-queretaro-1.aaaaaaaarq5d3kpmkm2nw7autwer7p4gbtexd2g3grm5opvkbt2h5rzggjgq` | creado |
| NAT Gateway | `ocid1.natgateway.oc1.mx-queretaro-1.aaaaaaaadif2dvfgt5xk6upmp6mettt26gn5ll7gaqtcfd2dnnpmwmkj5scq` | creado |
| Service Gateway | `ocid1.servicegateway.oc1.mx-queretaro-1.aaaaaaaasepcotulw3ez3gbbs2rjezxdqrxc2bmopfxyj444siaf2peau72a` | creado |

### IAM

| Recurso | OCID | Status |
|---|---|---|
| Dynamic Group DevOps | `ocid1.dynamicgroup.oc1..aaaaaaaao6bdgm6rvs7rzamuysa6tapyxfha4p4o7d6h32bu4reqrlsr7lzq` | creado |
| Dynamic Group OKE | `ocid1.dynamicgroup.oc1..aaaaaaaay5gtfewvwumq33763wjm76fj6cafobwu74gh2vhkojhkuc67ob2a` | creado |
| Dynamic Group Functions | `ocid1.dynamicgroup.oc1..aaaaaaaaqnkcp53m2mbebwcciqvrbk5ica6mahkfps7qykh2s5qo6dbh6mnq` | creado |
| 4 policies (devops, oke, oke_service, functions) | (ver consola) | creado |

### Vault y secretos

| Recurso | OCID | Status |
|---|---|---|
| Vault `taskbot-vault` | `ocid1.vault.oc1.mx-queretaro-1.ibva4ab7aaeqe.abyxeljrekeapwwguoseckvii2eppnfaqikydmor5bxuvpppwl2milj4ffnq` | creado |
| Management endpoint del Vault | `https://ibva4ab7aaeqe-management.kms.mx-queretaro-1.oci.oraclecloud.com` | creado |
| Master Key | `ocid1.key.oc1.mx-queretaro-1.ibva4ab7aaeqe.abyxeljriatvjpbewhkosqjywe4sgcr3c3xqjl7jxsvfk6fsnkv7jaytccta` | creado |
| Secret `autonomous-db-credentials` | `ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqatvbdhgwiopabzlbquzvk73e2kg5u7drcupkj2s354a7q` | creado (con placeholder) |
| Secret `oracle-wallet` | `ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqa3slwucurc7vyd6e7u5rhohrmqgoaa3pvo4wcbehs2zuq` | creado (con placeholder) |
| Secret `jwt-secret` | `ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqapken2wekjymoautsskvl47poz4qd5csdus7bla73umiq` | creado (con placeholder) |
| Secret `openai-credentials` | `ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqavjdivrddaldshon7exjfkgmadav4anx3d7hz2yozohja` | creado (con placeholder) |
| Secret `telegram-credentials` | `ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqax3jbui6sd47w4gxjhtnrt3yma36nlwoyn24lbaxz6cca` | creado (con placeholder) |
| Secret `jira-token` | `ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqa7o4fc6lmdhi3ngoooyhrykeou7ipvm357ugfm235nyga` | creado (con placeholder) |

### Container Registry

| Recurso | OCID | Status |
|---|---|---|
| Repo `ai-service` | `ocid1.containerrepo.oc1.mx-queretaro-1.0.ax5o32ww5jyq.aaaaaaaabn3ivwmuvmwocbhkboydv2zdkw54qmkya537am2tmpy7k66uwzhq` | creado |
| Repo `auth-service` | `ocid1.containerrepo.oc1.mx-queretaro-1.0.ax5o32ww5jyq.aaaaaaaalphpofpvhsru3fkvpiriuzhjsbfg62apt7ysdsxpurepbiff77wq` | creado |
| Repo `kpi-service` | `ocid1.containerrepo.oc1.mx-queretaro-1.0.ax5o32ww5jyq.aaaaaaaay7ckotlxqlnczspx44bmzcbbdk6djh5til4uktncgaprgoepyvyq` | creado |
| Repo `task-service` | `ocid1.containerrepo.oc1.mx-queretaro-1.0.ax5o32ww5jyq.aaaaaaaa72ealu62cptdfvcrvnelcswqpxapubdu6vvphepgewmsnrzkcvwa` | creado |
| Repo `telegram-service` | `ocid1.containerrepo.oc1.mx-queretaro-1.0.ax5o32ww5jyq.aaaaaaaa4mmne2mden7rxvyjrmh6uv4oebtrwmvxv2xnaerph6fjzmdsalla` | creado |
| Repo `frontend` | `ocid1.containerrepo.oc1.mx-queretaro-1.0.ax5o32ww5jyq.aaaaaaaayhvxxmtddyfllbozs6pfx26uip26svhh7aph7csyuk3zhv2yahpa` | creado |

URL base para push de imágenes: `mx-queretaro-1.ocir.io/ax5o32ww5jyq/taskbot-registry/<service>:<tag>`

### Load Balancer

| Recurso | OCID / IP | Status |
|---|---|---|
| Production LB `taskbot-prod-lb` | `ocid1.networkloadbalancer.oc1.mx-queretaro-1.amaaaaaazghwoyqa624kdt2xfxn3fewajzlhqe7uad7i67bpdmdf5nvbu3nq` | creado |
| **IP pública del Production LB** | **`159.54.158.212`** | **el DNS de `sammy-ulfh.dev` apunta aquí (día 8)** |

### Observabilidad

| Recurso | OCID | Status |
|---|---|---|
| Log Group | `ocid1.loggroup.oc1.mx-queretaro-1.amaaaaaazghwoyqawnm6y6pd3v5zkxhfva54gdx54evxgu2j4uotfgkwq7fq` | creado |
| Notification Topic — pipelines | `ocid1.onstopic.oc1.mx-queretaro-1.amaaaaaazghwoyqaviboxmnipb4ojbzvwr263qvwlgw2j6kknvjliyy6i3za` | creado |
| Notification Topic — Jira | `ocid1.onstopic.oc1.mx-queretaro-1.amaaaaaazghwoyqablu7vnibnazqs57flf6db5l4rwiwx2wxepvews4jyyca` | creado |

### Externos y otras capas (pendientes)

| Recurso | Owner | Status |
|---|---|---|
| Autonomous DB (existente) | Leo (verificar) | OCID por verificar |
| Conectividad VCN → Autonomous DB | Leo | bloqueado por cluster de Lili |
| Cluster OKE | Lili | pendiente |
| Node Pool | Lili | pendiente |
| DevOps Project | Sofi | pendiente |
| Functions Application | Sofi | pendiente |
| Function Jira | Sofi | pendiente |

---

## 4. Convención de tags OCI

| Tag | Valor |
|---|---|
| `Project` | `TaskBot` |
| `Sprint` | `Sprint-3` |
| `Owner` | `Leo` / `Lili` / `Sofi` / `Diana` |
| `ManagedBy` | `terraform` / `helm` / `manual` |
| `Environment` | `shared` / `vs-blue` / `vs-green` |

---

## 5. Mapeo `.env` → Kubernetes

| Variable `.env` | Tipo | Destino |
|---|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Sensible | Secret `autonomous-db-credentials` |
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

**Excluidas explícitamente del despliegue**: `SPRING_KAFKA_BOOTSTRAP_SERVERS` y `KAFKA_BROKERCONNECT`. Diana las elimina del `.env`, del `.env.example` y del código de `kpi-service` y `telegram-service` antes de la migración.

---

## 6. Mapeo Apache → NGINX Ingress

| Ruta pública | Destino Ingress |
|---|---|
| `/` | Service `frontend` |
| `/api/v1/auth` | `auth-service:8082` |
| `/api/webhook/telegram` | `telegram-service:8081` |
| `/api/anuncios` | `telegram-service:8081` |
| `/api/ai` | `ai-service:8083` |
| `/api/tasks` | `task-service:8084` |
| `/api/sprints` | `task-service:8084` |
| `/api` (catch-all) | `kpi-service:8080` |
| `/swagger-auth` | `auth-service:8082` |
| `/swagger-ia` | `ai-service:8083` |
| `/swagger-tasks` | `task-service:8084` |
| `/swagger-ui` | `kpi-service:8080` |
| `/v3/api-docs/*` | Cada microservicio expone el suyo |

La regla `/api` (catch-all) debe ir **después** de las rutas específicas de `/api/*`.

---

## 7. Cronograma y puntos de sincronización

| Día | Hito | Owners |
|---|---|---|
| 1 | **Sync obligatorio**: kick-off y ratificación del contrato | Todos |
| 1-3 | Leo: capa de infra. Lili: manifests templates. Sofi: Code Repos vacíos. Diana: Dockerfiles + build_specs | Paralelo |
| 4-5 | **Sync obligatorio**: integración inicial, primer despliegue end-to-end a `vs-blue` | Todos |
| 6-7 | Pulir Blue/Green, testing automatizado, integración Jira | Sofi + Diana |
| 8 | **Sync obligatorio**: migración de DNS de `sammy-ulfh.dev` a `159.54.158.212` | Leo + Todos |
| 9 | Pruebas de rollback intencional, validación end-to-end | Todos |
| 10 | Grabación del video del Sprint | Diana |

---

## 8. Limpieza pendiente antes de migrar (Diana)

- [ ] Eliminar `SPRING_KAFKA_BOOTSTRAP_SERVERS` y `KAFKA_BROKERCONNECT` de `.env` y `.env.example`
- [ ] Eliminar todo código que consuma Kafka en `kpi-service` y `telegram-service`
- [ ] Verificar que `Wallet/` y `.env` están en `.gitignore` y nunca se subieron al repo
- [ ] Confirmar que cada microservicio tiene su Dockerfile listo
- [ ] Confirmar que cada `application.yml` lee variables de entorno (no las hardcodea)
- [ ] Separar el repo del frontend del backend (si todavía no lo está)

---

## 9. Decisiones aún pendientes

- [ ] Versión de Kubernetes para OKE (recomendado: la LTS más reciente disponible en `mx-queretaro-1`) — **Lili**
- [ ] Network plugin del cluster (propuesta: VCN-native con subnet privada `/24`) — **Lili**
- [ ] Shape y tamaño del Node Pool: 3 nodos `VM.Standard.E4.Flex` con 2 OCPU / 16 GB cada uno, uno por fault domain — **Lili confirma**
- [ ] Estrategia de validación del namespace inactivo en el Blue/Green (host-based routing vs Kubernetes Job) — **Lili decide**
- [ ] Versión y distribución de Java en el Dockerfile base — **Diana**
- [ ] Estrategia de tags de imágenes Docker (SHA del commit + `latest` o también semver) — **Sofi + Diana**
- [ ] OCID exacto y región de la Autonomous AI Database existente — **Leo**
- [ ] Estrategia de sincronización Vault → Kubernetes Secret (External Secrets Operator vs job manual) — **Lili**

---

## 10. Bloqueos cruzados

| Quien bloquea | A quién | Recursos críticos |
|---|---|---|
| Leo | (ya desbloqueó) | VCN, Vault, Container Registry, LB, dynamic groups — todos creados |
| Lili | Sofi, Diana | Cluster OKE, namespaces, Ingress, Secrets sincronizados |
| Sofi | Diana | URLs de Code Repos, plantillas de `build_spec.yaml` |
| Diana | (nadie) | Consumidora final |

**Leo ya no bloquea a nadie.** El equipo puede arrancar sus capas en paralelo a partir de esta v0.3.

---

## 11. Apéndice: comandos para cargar secretos al Vault

Los 6 secrets están creados con placeholder. Antes de que Lili levante el cluster y sincronice los secrets de Kubernetes, Leo debe cargar los valores reales con OCI CLI. El lifecycle de Terraform ignora cambios al contenido, así que estas operaciones manuales no se sobrescriben en futuros applies.

### Patrón general

```bash
oci vault secret update-base64 \
  --secret-id <OCID_DEL_SECRET> \
  --secret-content-content "$(echo -n 'VALOR_REAL' | base64 -w0)"
```

(En macOS reemplazar `base64 -w0` por `base64`.)

### `jwt-secret`

```bash
oci vault secret update-base64 \
  --secret-id ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqapken2wekjymoautsskvl47poz4qd5csdus7bla73umiq \
  --secret-content-content "$(echo -n 'EL_JWT_SECRET_DEL_ENV_LOCAL' | base64 -w0)"
```

### `openai-credentials`

```bash
oci vault secret update-base64 \
  --secret-id ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqavjdivrddaldshon7exjfkgmadav4anx3d7hz2yozohja \
  --secret-content-content "$(echo -n 'sk-...la_api_key_de_openai' | base64 -w0)"
```

### `telegram-credentials`

```bash
oci vault secret update-base64 \
  --secret-id ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqax3jbui6sd47w4gxjhtnrt3yma36nlwoyn24lbaxz6cca \
  --secret-content-content "$(echo -n 'TELEGRAM_BOT_TOKEN_VALOR' | base64 -w0)"
```

### `jira-token`

```bash
oci vault secret update-base64 \
  --secret-id ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqa7o4fc6lmdhi3ngoooyhrykeou7ipvm357ugfm235nyga \
  --secret-content-content "$(echo -n 'JIRA_API_TOKEN' | base64 -w0)"
```

### `autonomous-db-credentials` (JSON con 3 campos)

```bash
oci vault secret update-base64 \
  --secret-id ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqatvbdhgwiopabzlbquzvk73e2kg5u7drcupkj2s354a7q \
  --secret-content-content "$(cat <<EOF | base64 -w0
{
  "DB_URL": "jdbc:oracle:thin:@taskbotdb_high?TNS_ADMIN=/app/Wallet",
  "DB_USERNAME": "ADMIN",
  "DB_PASSWORD": "el_password_real"
}
EOF
)"
```

### `oracle-wallet` (archivo binario — el .zip del wallet de la Autonomous DB)

```bash
oci vault secret update-base64 \
  --secret-id ocid1.vaultsecret.oc1.mx-queretaro-1.amaaaaaazghwoyqa3slwucurc7vyd6e7u5rhohrmqgoaa3pvo4wcbehs2zuq \
  --secret-content-content "$(base64 -w0 < /ruta/local/al/Wallet.zip)"
```

### Verificar que un secret tiene contenido real

```bash
oci secrets secret-bundle get \
  --secret-id <OCID_DEL_SECRET> \
  --raw-output \
  --query 'data."secret-bundle-content".content' \
  | base64 -d
```

Esto te muestra el contenido decodificado del secret en su versión actual. Si te devuelve `PLACEHOLDER_REPLACE_VIA_OCI_CLI`, todavía tiene el placeholder original.

---

## Changelog

| Fecha | Versión | Cambios | Autor |
|---|---|---|---|
| 2026-05-19 | 0.1 | Borrador inicial | Leo |
| 2026-05-19 | 0.2 | Aplicado bootstrap + compartment + IAM + networking (18 recursos) | Leo |
| 2026-05-19 | 0.3 | Aplicada capa completa de Leo: Vault + 6 secrets + 6 OCIR repos + 1 LB + log group + 2 notification topics. Documentada adaptación a 1 solo Load Balancer por límite de cuota Always Free. Apéndice nuevo con comandos para cargar valores reales a los secrets. Leo ya no bloquea a nadie | Leo |
