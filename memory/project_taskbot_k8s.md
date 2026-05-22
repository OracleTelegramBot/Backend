---
name: project-taskbot-k8s
description: TaskBot monorepo Java Spring Boot - contexto Sprint 3 migración a Kubernetes en OCI
metadata:
  type: project
---

TaskBot es un monorepo Java Spring Boot con 5 microservicios corriendo en OCI. En Sprint 3 se preparó el código para migrar de docker-compose en VPS a Kubernetes.

**Servicios y puertos:** ai-service (8083), auth-service (8082), kpi-service (8080), task-service (8084), telegram-service (8081).

**Why:** La VPS actual usa docker-compose con Kafka. En K8s se elimina Kafka y el Wallet de Oracle se monta como Secret de Kubernetes.

**How to apply:** Antes de tocar estos servicios verificar que task-service sigue vacío (solo tenía `target/`), que kpi-service empaqueta como WAR (packaging war), y que los application.properties ya usan variables de entorno.

**Cambios realizados en Sprint 3:**
- Eliminado Kafka de kpi-service y telegram-service (KafkaConfig.java, WebKafkaConsumer.java, TelegramKafkaProducer.java, AnuncioController.java)
- Credenciales de BD movidas a env vars en kpi-service y auth-service (estaban hardcodeadas como USER/PASSWORD)
- URLs localhost movidas a env vars en telegram-service
- Conflict marker resuelto en ai-service/application.properties
- Creados build_spec.yaml y k8s/deployment.yaml en los 5 servicios
- namespace K8s: vs-blue, registry: mx-queretaro-1.ocir.io/ax5o32ww5jyq/taskbot-registry/

**Variables de entorno que debe proveer K8s (Secrets):**
- autonomous-db-credentials: DB_URL, DB_USERNAME, DB_PASSWORD
- jwt-secret: JWT_SECRET
- openai-credentials: OPENAI_API_KEY
- telegram-credentials: TELEGRAM_BOT_TOKEN
- oracle-wallet (Secret montado en /app/Wallet)
- ConfigMap taskbot-config para el resto
