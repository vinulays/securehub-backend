# SecureHub Backend

SecureHub is a multi-tenant SaaS platform built using a microservices architecture. It is designed to support enterprise-grade authentication, scalable service separation, and event-driven communication.

This repository contains the backend services responsible for authentication, user management, project management, and platform-level infrastructure integration.

---

## Architecture Overview

SecureHub follows a distributed microservices architecture with clear domain boundaries and independent services.

Core components include:

- API Gateway for request routing and security enforcement
- Identity management using Keycloak (OAuth2 / OpenID Connect)
- Event-driven communication using Apache Kafka
- PostgreSQL for persistent storage
- Docker-based local development environment

---

## Technology Stack

- Java 25
- Spring Boot 4.0.6
- Spring Security + OAuth2 Resource Server
- Spring Cloud Gateway
- Spring Kafka
- Keycloak (Identity Provider)
- PostgreSQL
- Docker & Docker Compose
- MailHog (for local email testing)
- Maven

---

## Services

The backend is composed of the following services:

- api-gateway: Entry point for all client requests, handles routing and authentication
- eureka-server: Act as the service to discover microservices
- auth-service: Manages authentication-related integrations
- user-service: Handles users, organizations, and multi-tenant data
- project-service: Manages projects and tasks
- notification-service: Handles async notifications
- file-service: Manages file uploads via AWS S3

---

## Local Development Setup

### Prerequisites

- Java 26
- Docker & Docker Compose
- Maven (optional, wrapper included)

### Start Infrastructure

From the root directory:

```
docker compose up -d
```
This will start:

- PostgreSQL (application database)
- Keycloak (authentication server)
- Kafka (event streaming)
- MailHog (email testing tool)

### Kafka Setup (KRaft Mode)

SecureHub uses Apache Kafka in KRaft mode (without ZooKeeper) for event-driven communication.

#### Step 1: Generate Cluster ID

Kafka requires a unique cluster ID to initialize storage metadata.

Run the following command:

```dockerfile
docker run --rm confluentinc/cp-kafka:latest kafka-storage.sh random-uuid
```
This will output a value similar to:

```
J_5BbHaKSY-w1lYbb4KSVQ
```

#### Step 2: Configure Cluster ID in Kafka Service

Copy the generated value and set it in your Kafka service configuration.

In `docker-compose.yml`:

```dockerfile
kafka:
  image: confluentinc/cp-kafka:latest
  container_name: securehub-kafka

  environment:
    CLUSTER_ID: "J_5BbHaKSY-w1lYbb4KSVQ"
```

### Kafka Topics

- `user.created` - Event sent when a new user is created or invitation is resent.

### Running a Service

Example (API Gateway):

```
cd api-gateway
./mvnw spring-boot:run
```
Example (User Service):

```
cd user-service
./mvnw spring-boot:run
```

### Email Testing

MailHog is used for local email testing

- SMTP server - `localhost:1025`
- Web UI - `http://localhost:8025`