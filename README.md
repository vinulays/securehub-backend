# SecureHub Backend

SecureHub is a multi-tenant SaaS platform built using a microservices architecture. It is designed to support enterprise-grade authentication, scalable service separation, and event-driven communication.

This repository contains the backend services responsible for authentication, user management, project management, and platform-level infrastructure integration.

---

## Architecture Overview

SecureHub follows a distributed microservices architecture with clear domain boundaries and independent services.

Core components include:

- API Gateway for request routing and security enforcement
- Identity management using Keycloak (OAuth2 / OpenID Connect)
- Independent microservices for business domains
- PostgreSQL for persistent storage
- Docker-based local development environment

---

## Technology Stack

- Java 26
- Spring Boot 4.0.6
- Spring Security
- Spring Cloud Gateway
- Keycloak (Identity Provider)
- PostgreSQL
- Docker & Docker Compose
- Maven

---

## Services

The backend is composed of the following services:

- api-gateway: Entry point for all client requests, handles routing and authentication
- identity-service: Manages authentication-related integrations
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

### Running the Infrastructure

From the root directory:

```
docker compose up -d
```
This will start:

- PostgreSQL (application database)
- Keycloak (authentication server)
- Keycloak database

### Running a Service

Example (API Gateway):

```
cd api-gateway
./mvnw spring-boot:run
```