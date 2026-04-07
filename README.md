# 🏥 MediBook — Hospital Appointment Booking System

A Spring Boot + MySQL + Thymeleaf web application for booking hospital appointments, containerized with Docker and deployed on Kubernetes (kubeadm).

---

## 🛠️ Tech Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Backend     | Spring Boot 3.3.4, Spring Data JPA|
| Frontend    | Thymeleaf, HTML/CSS               |
| Database    | MySQL 8.0                         |
| Container   | Docker (multi-stage build)        |
| Orchestration | Kubernetes (kubeadm)            |

---

## 📦 Project Structure

```
hospital-appointment/
├── src/main/java/com/hospital/
│   ├── entity/          # Patient, Doctor, Appointment
│   ├── repository/      # JPA repositories
│   ├── service/         # Service interfaces + implementations
│   └── controller/      # HomeController, AdminController
├── src/main/resources/
│   ├── templates/       # Thymeleaf HTML pages
│   └── application.properties
├── kube_scripts/
│   ├── db-statefulset-svc.yml   # MySQL StatefulSet + ClusterIP Service
│   ├── app-deploy-svc.yml       # Spring Boot Deployment + NodePort Service
│   └── setup-storage.sh         # Creates hostPath PV for MySQL
├── Dockerfile
├── docker_build_push.sh
└── k8s-deploy.sh
```

---

## 🔑 Roles

| Role    | Login                      | Access                                            |
|---------|----------------------------|-------------------------------------------- ------|
| Admin   | admin / admin              | Dashboard, manage doctors, patients, appointments |
| Patient | registered email/password  | Book appointments, view & cancel own appointments |

---

## ▶️ Running Locally

### Prerequisites
- Java 17, Maven, MySQL running locally

### Steps
```bash
# 1. Update application.properties to point to localhost
spring.datasource.url=jdbc:mysql://localhost:3306/hospitaldb?createDatabaseIfNotExist=true

# 2. Build and run
./mvnw spring-boot:run
```

Access at: **http://localhost:8085**

---

## 🐳 Docker

```bash
# Build image
docker build -t your_dockerhub_username/hospital-appointment:latest .

# Run with Docker (needs MySQL container)
docker network create hospital-net

docker run -d --name mysql-service --network hospital-net \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -e MYSQL_DATABASE=hospitaldb \
  mysql:8.0

docker run -d --name hospital-app --network hospital-net \
  -p 8085:8085 \
  your_dockerhub_username/hospital-appointment:latest

# Or use the build+push script
bash docker_build_push.sh
```

---

## ☸️ Kubernetes (kubeadm)

```bash
# Update image name in kube_scripts/app-deploy-svc.yml first, then:
bash k8s-deploy.sh
```

App will be available at: **http://\<NodeIP\>:30085**

### Useful kubectl commands
```bash
kubectl get pods
kubectl get svc
kubectl logs deployment/hospital-appointment
kubectl describe pod <pod-name>
```

---

## 🗄️ Entities

- **Patient** — fullName, email, phone, gender, age, password
- **Doctor**  — fullName, specialization, qualification, phone, email, availableDays, availableTime
- **Appointment** — date, time, reason, status (PENDING/CONFIRMED/CANCELLED), FK to Patient & Doctor

---

## 📊 Monitoring Setup (Prometheus + Grafana)
**Add Dependencies**
```bash
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**Enable Metrics**
```bash
management.endpoints.web.exposure.include=*
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
```

**Verify Metrics**
```bash
http://<APP_SERVER_IP>:30085/actuator/prometheus
```

**Prometheus Configuration**
```bash
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['<APP_SERVER_IP>:30085']
```
## Prometheus Queries
```bash
up{job="spring-boot-app"}
```
```bash
http_server_requests_seconds_count
```
```bash
rate(http_server_requests_seconds_count[1m])
```
```bash
jvm_memory_used_bytes
```
```bash
jvm_threads_live_threads
```

**Node Exporter Configuration**
```bash
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'node-exporter'
    static_configs:
      - targets: ['<APP_SERVER_IP>:9100']

  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['<APP_SERVER_IP>:30085']
```

## Grafana Dashboard Queries

**Requests Per Second**
```bash
sum(rate(http_server_requests_seconds_count{job="spring-boot-app", uri!="/actuator/prometheus"}[1m]))
```
**Active Requests**
```bash
sum(http_server_requests_active_seconds_count{job="spring-boot-app"})
```
**Average Response Time**
```bash
sum(rate(http_server_requests_seconds_sum{job="spring-boot-app"}[1m])) /
sum(rate(http_server_requests_seconds_count{job="spring-boot-app"}[1m]))
```
**Error Rate**
```bash
sum(rate(http_server_requests_seconds_count{job="spring-boot-app", status!~"2.."}[1m]))
```
**JVM Heap Memory**
```bash
sum(jvm_memory_used_bytes{area="heap", job="spring-boot-app"})
```
**95th Percentile Latency**
```bash
histogram_quantile(0.95,
  sum(rate(http_server_requests_seconds_bucket{job="spring-boot-app"}[5m])) by (le)
)
```

🔄 Monitoring Flow
```bash
Spring Boot → Actuator → Prometheus → Grafana
```