# University Platform

A microservices-based university management platform built with Spring Boot, PostgreSQL, Apache Kafka, and Kubernetes.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Running Locally with Podman](#running-locally-with-podman)
- [API Usage — Demo Scenario](#api-usage--demo-scenario)
- [Observability](#observability)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Environment Variables](#environment-variables)
- [Project Structure](#project-structure)

---

## Project Overview

University Platform is a backend system composed of four independently deployable services:

| Service | Responsibility |
|---|---|
| **auth-service** | User registration, JWT-based authentication, role management |
| **academic-service** | Courses, assignments, submissions, student–course enrollment |
| **notification-service** | Event-driven notification processing via Kafka |
| **audit-service** | Immutable audit log of all system events via Kafka |

---

## Architecture

```
┌────────────┐     JWT      ┌──────────────────┐
│   Client   │ ──────────── │   auth-service   │  :8081
└────────────┘              └──────────────────┘
      │
      │ Bearer Token
      ▼
┌─────────────┐   nginx LB  ┌──────────────────┐
│   :8082     │ ──────────► │ academic-service │  :8082 × 2 replicas
└─────────────┘             └────────┬─────────┘
                                     │ Kafka: academic-events
                          ┌──────────┴──────────┐
                          ▼                     ▼
             ┌─────────────────────┐  ┌──────────────────┐
             │ notification-service│  │  audit-service   │
             │       :8083         │  │      :8084       │
             └─────────────────────┘  └──────────────────┘
```

**Communication:**
- All external API calls are authenticated with a JWT issued by `auth-service`
- `academic-service` publishes domain events to the `academic-events` Kafka topic after every write operation
- `notification-service` and `audit-service` consume `academic-events` independently with separate consumer group IDs
- Each service owns its own PostgreSQL database

**Roles:** `STUDENT`, `PROFESSOR`

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 17 |
| Maven | 3.9+ |
| Podman | 4.0+ |
| podman-compose | 1.0+ |
| Minikube | 1.32+ |
| kubectl | 1.28+ |

---

## Running Locally with Podman

### 1. Build all JARs

```bash
mvn clean package -DskipTests
```

### 2. Build container images

```bash
podman build -f auth-service/Dockerfile        -t localhost/university-platform/auth-service:latest .
podman build -f academic-service/Dockerfile     -t localhost/university-platform/academic-service:latest .
podman build -f notification-service/Dockerfile -t localhost/university-platform/notification-service:latest .
podman build -f audit-service/Dockerfile        -t localhost/university-platform/audit-service:latest .
```

### 3. Start all services

```bash
podman-compose up -d
```

### 4. Service endpoints

| Service | URL |
|---|---|
| auth-service | http://localhost:8081 |
| academic-service (via nginx) | http://localhost:8082 |
| notification-service | http://localhost:8083 |
| audit-service | http://localhost:8084 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |
| Kafka UI | http://localhost:8088 |

### 5. Health checks

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

### 6. Stop all services

```bash
podman-compose down
```

---

## API Usage — Demo Scenario

All examples use `curl`. Token capture uses standard POSIX `sed`.

### Step 1 — Register a professor

```bash
curl -s -X POST http://localhost:8081/api/v1/auth/register/professor \
  -H "Content-Type: application/json" \
  -d '{
    "email": "prof@university.com",
    "password": "secret123",
    "firstName": "Alice",
    "lastName": "Smith"
  }'
```

### Step 2 — Register a student

```bash
curl -s -X POST http://localhost:8081/api/v1/auth/register/student \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@university.com",
    "password": "secret123",
    "firstName": "Bob",
    "lastName": "Jones"
  }'
```

### Step 3 — Login as professor and capture JWT

```bash
PROF_TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"prof@university.com","password":"secret123"}' \
  | sed 's/.*"token":"\([^"]*\)".*/\1/')
```

### Step 4 — Create a professor profile in academic-service

```bash
PROFESSOR_ID=$(curl -s -X POST http://localhost:8082/api/v1/professors \
  -H "Authorization: Bearer $PROF_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "prof@university.com",
    "firstName": "Alice",
    "lastName": "Smith"
  }' | sed 's/.*"id":"\([^"]*\)".*/\1/')
```

### Step 5 — Create a student profile in academic-service

```bash
STUDENT_ID=$(curl -s -X POST http://localhost:8082/api/v1/students \
  -H "Authorization: Bearer $PROF_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@university.com",
    "firstName": "Bob",
    "lastName": "Jones"
  }' | sed 's/.*"id":"\([^"]*\)".*/\1/')
```

### Step 6 — Create a course

```bash
COURSE_ID=$(curl -s -X POST http://localhost:8082/api/v1/courses \
  -H "Authorization: Bearer $PROF_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Introduction to Computer Science",
    "description": "CS101"
  }' | sed 's/.*"id":"\([^"]*\)".*/\1/')
```

### Step 7 — Enroll student in the course

```bash
curl -s -X POST http://localhost:8082/api/v1/courses/$COURSE_ID/students/$STUDENT_ID \
  -H "Authorization: Bearer $PROF_TOKEN"
```

### Step 8 — Create an assignment

```bash
ASSIGNMENT_ID=$(curl -s -X POST http://localhost:8082/api/v1/assignments/courses/$COURSE_ID \
  -H "Authorization: Bearer $PROF_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Homework 1",
    "description": "Solve exercises 1–5",
    "dueDate": "2026-12-31T23:59:59"
  }' | sed 's/.*"id":"\([^"]*\)".*/\1/')
```

### Step 9 — Login as student and capture JWT

```bash
STUDENT_TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"student@university.com","password":"secret123"}' \
  | sed 's/.*"token":"\([^"]*\)".*/\1/')
```

### Step 10 — Submit assignment

```bash
SUBMISSION_ID=$(curl -s -X POST http://localhost:8082/api/v1/submissions/assignments/$ASSIGNMENT_ID \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "My solution to homework 1"
  }' | sed 's/.*"id":"\([^"]*\)".*/\1/')
```

### Step 11 — Grade the submission

```bash
curl -s -X PATCH http://localhost:8082/api/v1/submissions/$SUBMISSION_ID/grade \
  -H "Authorization: Bearer $PROF_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"grade": 95}'
```

> After steps 6–11 each operation publishes an event to the `academic-events` Kafka topic.
> `notification-service` and `audit-service` process these events automatically in the background.

---

## Observability

### Prometheus

Prometheus scrapes all four services every 15 seconds via `/actuator/prometheus`.

- **UI:** http://localhost:9090/targets — all targets should show status `UP`

### Grafana

- **UI:** http://localhost:3000
- **Default credentials:** `admin` / `admin`
- Add a Prometheus data source at `http://prometheus:9090`

### Custom application metrics

| Metric | Description |
|---|---|
| `academic_courses_created_total` | Courses successfully created |
| `academic_assignments_created_total` | Assignments successfully created |
| `academic_submissions_created_total` | Submissions successfully created |
| `academic_students_registered_total` | Students enrolled in a course |
| `notifications_sent_total` | Notifications processed successfully |
| `notifications_failed_total` | Notifications that failed processing |
| `audit_events_saved_total` | Audit log entries persisted |

### Raw metrics endpoints

```bash
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8082/actuator/prometheus
curl http://localhost:8083/actuator/prometheus
curl http://localhost:8084/actuator/prometheus
```

---

## Kubernetes Deployment

Tested with Minikube and the Podman driver.

### 1. Start Minikube

```bash
minikube start --driver=podman --container-runtime=cri-o
```

### 2. Build JARs and images

```bash
mvn clean package -DskipTests

podman build -f auth-service/Dockerfile        -t localhost/university-platform/auth-service:latest .
podman build -f academic-service/Dockerfile     -t localhost/university-platform/academic-service:latest .
podman build -f notification-service/Dockerfile -t localhost/university-platform/notification-service:latest .
podman build -f audit-service/Dockerfile        -t localhost/university-platform/audit-service:latest .
```

### 3. Load images into Minikube

```bash
podman save localhost/university-platform/auth-service:latest        | minikube image load --overwrite=true -
podman save localhost/university-platform/academic-service:latest     | minikube image load --overwrite=true -
podman save localhost/university-platform/notification-service:latest | minikube image load --overwrite=true -
podman save localhost/university-platform/audit-service:latest        | minikube image load --overwrite=true -
```

### 4. Apply manifests

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmap.yaml

kubectl apply -f k8s/postgres/
kubectl apply -f k8s/kafka/

kubectl apply -f k8s/auth-service/
kubectl apply -f k8s/academic-service/
kubectl apply -f k8s/notification-service/
kubectl apply -f k8s/audit-service/
```

### 5. Verify pods

```bash
kubectl get pods -n university-platform
```

```
NAME                                    READY   STATUS    RESTARTS
auth-service-xxxxxxxxx-xxxxx            1/1     Running   0
academic-service-xxxxxxxxx-xxxxx        1/1     Running   0
academic-service-xxxxxxxxx-xxxxx        1/1     Running   0
notification-service-xxxxxxxxx-xxxxx    1/1     Running   0
audit-service-xxxxxxxxx-xxxxx           1/1     Running   0
kafka-xxxxxxxxx-xxxxx                   1/1     Running   0
postgres-auth-xxxxxxxxx-xxxxx           1/1     Running   0
postgres-academic-xxxxxxxxx-xxxxx       1/1     Running   0
postgres-notification-xxxxxxxxx-xxxxx   1/1     Running   0
postgres-audit-xxxxxxxxx-xxxxx          1/1     Running   0
```

### 6. Access services via port-forward

```bash
kubectl port-forward svc/auth-service         8081:8081 -n university-platform &
kubectl port-forward svc/academic-service      8082:8082 -n university-platform &
kubectl port-forward svc/notification-service  8083:8083 -n university-platform &
kubectl port-forward svc/audit-service         8084:8084 -n university-platform &
```

### 7. Tear down

```bash
kubectl delete namespace university-platform
minikube stop
```

---

## Environment Variables

| Variable | Description | Default (local dev) |
|---|---|---|
| `AUTH_DB_URL` | Auth service JDBC URL | `jdbc:postgresql://localhost:5432/auth_db` |
| `AUTH_DB_USER` | Auth DB username | `auth_user` |
| `AUTH_DB_PASSWORD` | Auth DB password | `auth_pass` |
| `ACADEMIC_DB_URL` | Academic service JDBC URL | `jdbc:postgresql://localhost:5433/academic_db` |
| `ACADEMIC_DB_USER` | Academic DB username | `academic_user` |
| `ACADEMIC_DB_PASSWORD` | Academic DB password | `academic_pass` |
| `NOTIFICATION_DB_URL` | Notification service JDBC URL | `jdbc:postgresql://localhost:5435/notification_db` |
| `NOTIFICATION_DB_USER` | Notification DB username | `notification_user` |
| `NOTIFICATION_DB_PASSWORD` | Notification DB password | *(required)* |
| `AUDIT_DB_URL` | Audit service JDBC URL | `jdbc:postgresql://localhost:5432/audit_db` |
| `AUDIT_DB_USER` | Audit DB username | `audit_user` |
| `AUDIT_DB_PASSWORD` | Audit DB password | `audit_pass` |
| `JWT_SECRET` | Secret for signing JWTs (min 32 chars) | `changeme-this-is-a-very-long-dev-secret-key-32chars` |
| `JWT_EXPIRATION_MS` | JWT validity in milliseconds | `86400000` (24 h) |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address | `localhost:9092` |
| `ACADEMIC_EVENTS_TOPIC` | Kafka topic for domain events | `academic-events` |
| `GRAFANA_PASSWORD` | Grafana admin password | `admin` |

> Replace all default credentials before deploying outside of a local environment.
> Generate a strong JWT secret with: `openssl rand -hex 32`

---

## Project Structure

```
university-platform/
├── auth-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── academic-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── notification-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── audit-service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── k8s/
│   ├── namespace.yaml
│   ├── secrets.yaml
│   ├── configmap.yaml
│   ├── postgres/
│   │   ├── postgres-auth.yaml
│   │   ├── postgres-academic.yaml
│   │   ├── postgres-notification.yaml
│   │   └── postgres-audit.yaml
│   ├── kafka/
│   │   └── kafka.yaml
│   ├── auth-service/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   ├── academic-service/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   ├── notification-service/
│   │   ├── deployment.yaml
│   │   └── service.yaml
│   └── audit-service/
│       ├── deployment.yaml
│       └── service.yaml
├── prometheus.yml
├── nginx.conf
├── podman-compose.yml
└── pom.xml
```
