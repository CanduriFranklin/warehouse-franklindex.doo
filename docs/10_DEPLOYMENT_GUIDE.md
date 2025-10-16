# 🚀 Deployment Guide - Warehouse Microservice

**Comprehensive guide for building, deploying, and running the Warehouse Microservice**

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Prerequisites](#prerequisites)
4. [Local Development Setup](#local-development-setup)
5. [Docker Deployment](#docker-deployment)
6. [Environment Configuration](#environment-configuration)
7. [Database Setup](#database-setup)
8. [RabbitMQ Configuration](#rabbitmq-configuration)
9. [Building the Application](#building-the-application)
10. [Running the Application](#running-the-application)
11. [Health Checks & Monitoring](#health-checks--monitoring)
12. [Troubleshooting](#troubleshooting)

---

## 📖 Overview

This microservice is a **modern warehouse management system** built with cutting-edge Java technologies, containerized with Docker, and designed for scalability and maintainability.

### Architecture Highlights

- **Language**: Java 25 LTS (IBM Semeru Runtime)
- **Framework**: Spring Boot 3.5.6 + Spring Cloud 2025.0.0
- **Build Tool**: Gradle 9.1.0
- **Containerization**: Docker + Docker Compose
- **Database**: PostgreSQL 16 (Alpine)
- **Message Broker**: RabbitMQ 3.12 (Management + Alpine)
- **API Documentation**: OpenAPI 3.0 (Swagger UI)

---

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 25 LTS | Programming Language |
| **Spring Boot** | 3.5.6 | Application Framework |
| **Spring Cloud** | 2025.0.0 | Distributed Systems Toolkit |
| **Gradle** | 9.1.0 | Build Automation |
| **PostgreSQL** | 16-alpine | Relational Database |
| **RabbitMQ** | 3.12-management-alpine | Message Broker |
| **Flyway** | 11.7.2 | Database Migrations |
| **Docker** | 28.4.0+ | Containerization |
| **Docker Compose** | 3.9 | Multi-container Orchestration |

### Key Features

✅ **Java 25 LTS** - Latest long-term support version with modern features  
✅ **Spring Boot 3.5.6** - Full compatibility with Java 25  
✅ **Spring Cloud 2025.0.0** - Latest microservices patterns  
✅ **Multi-stage Docker Builds** - Optimized image size (~200MB)  
✅ **RabbitMQ Event-Driven Architecture** - Async message processing  
✅ **PostgreSQL 16** - Latest database features  
✅ **Auto-configuration** - Spring Boot Docker Compose integration  

---

## 📦 Prerequisites

### Required Software

#### 1. **Java 25 LTS**

```bash
# Check Java version
java -version

# Expected output:
# openjdk version "25" 2025-09-16
# IBM Semeru Runtime Open Edition 25+36 (build 25+36-openj9-0.55.0)
```

**Installation:**
- Download from: https://adoptium.net/ or https://developer.ibm.com/languages/java/semeru-runtimes/

#### 2. **Gradle 9.1.0**

```bash
# Using SDKMAN! (Recommended)
sdk install gradle 9.1.0
sdk use gradle 9.1.0

# Verify installation
gradle --version
```

#### 3. **Docker Desktop**

- **Windows**: Download from https://www.docker.com/products/docker-desktop
- **Enable WSL2 Integration**: Settings → Resources → WSL Integration
- **Minimum Resources**: 4GB RAM, 2 CPUs

```bash
# Verify Docker
docker --version
# Docker version 28.4.0

# Verify Docker Compose
docker-compose --version
```

#### 4. **Git**

```bash
git --version
```

---

## 🏗️ Local Development Setup

### 1. Clone the Repository

```bash
git clone https://github.com/CanduriFranklin/warehouse-franklindex.doo.git
cd warehouse-franklindex.doo
```

### 2. Configure Environment Variables

```bash
# Create .env file from template
cp .env.example .env

# Edit .env with your values
nano .env  # or use your favorite editor
```

**Critical Environment Variables:**

```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=warehouse_db
DB_USERNAME=warehouse_user
DB_PASSWORD=warehouse_secure_2025

# RabbitMQ
RABBITMQ_ENABLED=true
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=rabbitmq_secure_2025

# JWT Security
JWT_SECRET=<generated-base64-secret>  # Use: openssl rand -base64 32

# Development Users
DEV_ADMIN_USERNAME=admin
DEV_ADMIN_PASSWORD=Admin@2025!Secure
```

### 3. Update Gradle Wrapper (if needed)

```bash
# Update to Gradle 9.1.0
./gradlew wrapper --gradle-version 9.1.0
```

---

## 🐳 Docker Deployment

### Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose Network                    │
│                     (warehouse-network)                      │
├──────────────────┬─────────────────┬────────────────────────┤
│  PostgreSQL 16   │   RabbitMQ 3.12 │      pgAdmin 4         │
│  Port: 5432      │   Ports: 5672,  │      Port: 5050        │
│  Volume: 5GB     │   15672         │      Volume: 1GB       │
│  Health: OK      │   Health: OK    │      Status: Running   │
└──────────────────┴─────────────────┴────────────────────────┘
           ▲                  ▲                   ▲
           └──────────────────┴───────────────────┘
                            │
                   Spring Boot Application
                        (Port 8080)
```

### Docker Files Structure

```
warehouse-franklindex.doo/
├── docker-compose.yml        # Multi-container orchestration
├── Dockerfile                # Multi-stage build (optimized)
├── .dockerignore            # Exclude unnecessary files
└── run.sh                   # Helper script with env loading
```

### Multi-Stage Dockerfile

Our Dockerfile uses **3 stages** for optimization:

1. **Build Stage** - Compile Java code
2. **Dependencies Stage** - Extract and cache dependencies
3. **Runtime Stage** - Minimal JRE with application

**Benefits:**
- 🔽 **Image Size**: ~200MB (vs 800MB+ without multi-stage)
- ⚡ **Build Speed**: Layer caching for dependencies
- 🔒 **Security**: Minimal attack surface (no build tools in runtime)

### Start Docker Services

```bash
# Start PostgreSQL, RabbitMQ, and pgAdmin
docker-compose up -d

# Verify containers are running
docker-compose ps

# Expected output:
# NAME                 STATUS
# warehouse-postgres   Up (healthy)
# warehouse-rabbitmq   Up (healthy)
# warehouse-pgadmin    Up
```

### Docker Compose Commands

```bash
# Start services in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services (keep volumes)
docker-compose stop

# Restart services
docker-compose restart

# Stop and remove containers (keep volumes)
docker-compose down

# Stop and REMOVE EVERYTHING (including data!)
docker-compose down -v

# View resource usage
docker stats

# Rebuild images
docker-compose build --no-cache
```

---

## ⚙️ Environment Configuration

### Configuration Profiles

| Profile | Purpose | Database | RabbitMQ | Security |
|---------|---------|----------|----------|----------|
| **dev** | Development | PostgreSQL (local) | Enabled | In-Memory Users |
| **test** | Integration Tests | H2 (in-memory) | Disabled | Mock |
| **prod** | Production | PostgreSQL (managed) | Enabled | Database Auth |

### Active Profile

```bash
# Set in .env file
SPRING_PROFILES_ACTIVE=dev

# Or via command line
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Spring Boot Docker Compose Integration

Spring Boot 3.5.6+ automatically manages Docker Compose lifecycle:

- ✅ Detects `docker-compose.yml`
- ✅ Starts containers if not running
- ✅ Waits for health checks
- ✅ Stops containers on shutdown (optional)

**Disable auto-management:**

```yaml
# application-dev.yml
spring:
  docker:
    compose:
      enabled: false  # Manage containers manually
```

---

## 🗄️ Database Setup

### PostgreSQL Configuration

**Docker Compose Configuration:**

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: warehouse-postgres
    environment:
      POSTGRES_DB: warehouse_db
      POSTGRES_USER: warehouse_user
      POSTGRES_PASSWORD: warehouse_secure_2025
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U warehouse_user -d warehouse_db"]
      interval: 10s
      timeout: 5s
      retries: 5
```

### Database Migrations (Flyway)

Migrations are located in: `src/main/resources/db/migration/`

```
db/migration/
└── V1__Create_warehouse_tables.sql
```

**Migration Process:**

1. Application starts
2. Flyway checks version in `flyway_schema_history` table
3. Applies pending migrations in order
4. Records successful migrations

**Flyway Commands:**

```bash
# Check migration status
./gradlew flywayInfo

# Migrate to latest version
./gradlew flywayMigrate

# Clean database (CAUTION: Deletes all data!)
./gradlew flywayClean
```

### Database Access

#### Via psql (Command Line)

```bash
# Connect from host
docker exec -it warehouse-postgres psql -U warehouse_user -d warehouse_db

# Common commands
\dt          # List tables
\d table     # Describe table
\l           # List databases
\q           # Quit
```

#### Via pgAdmin (Web UI)

1. **Access pgAdmin**: http://localhost:5050
2. **Login**:
   - Email: `admin@warehouse.com`
   - Password: `admin`
3. **Add Server**:
   - Host: `warehouse-postgres` (container name!)
   - Port: `5432`
   - Database: `warehouse_db`
   - Username: `warehouse_user`
   - Password: `warehouse_secure_2025`

---

## 🐰 RabbitMQ Configuration

### Event-Driven Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                 RabbitMQ Topic Exchange                      │
│                  (warehouse.events)                          │
└────────┬────────────────┬────────────────┬──────────────────┘
         │                │                │
    delivery.received  baskets.sold  baskets.disposed
         │                │                │
         ▼                ▼                ▼
   ┌──────────┐    ┌──────────┐    ┌──────────┐
   │  Queue   │    │  Queue   │    │  Queue   │
   │ delivery │    │  sold    │    │ disposed │
   └──────────┘    └──────────┘    └──────────┘
         │                │                │
         └────────────────┴────────────────┘
                          │
                  (Retry 3x with backoff)
                          │
                     ┌────▼────┐
                     │   DLQ   │ (Dead Letter Queue)
                     └─────────┘
```

### RabbitMQ Management UI

1. **Access**: http://localhost:15672
2. **Login**:
   - Username: `guest`
   - Password: `guest`

### Queues and Exchanges

| Type | Name | Purpose |
|------|------|---------|
| **Exchange** | `warehouse.events` | Main topic exchange for domain events |
| **Exchange** | `warehouse.dlx` | Dead letter exchange for failed messages |
| **Queue** | `warehouse.delivery` | Delivery received events |
| **Queue** | `warehouse.baskets.sold` | Baskets sold events |
| **Queue** | `warehouse.baskets.disposed` | Baskets disposed events |
| **Queue** | `warehouse.dlq` | Dead letter queue (failed after 3 retries) |

### Retry Policy

- **Max Attempts**: 3
- **Initial Interval**: 3000ms (3 seconds)
- **Multiplier**: 2.0 (exponential backoff)
- **Sequence**: 3s → 6s → 12s → DLQ

### RabbitMQ Docker Configuration

```yaml
services:
  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: warehouse-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
      RABBITMQ_ERLANG_COOKIE: warehouse_secret_cookie_2025
    ports:
      - "5672:5672"   # AMQP protocol
      - "15672:15672" # Management UI
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
      - rabbitmq_logs:/var/log/rabbitmq
```

---

## 🏗️ Building the Application

### Gradle Build Commands

```bash
# Clean build directory
./gradlew clean

# Compile Java sources
./gradlew compileJava

# Run tests (currently disabled due to Java 25 compatibility)
./gradlew test

# Build JAR (skip tests)
./gradlew build -x test

# Build with full verification
./gradlew clean build --no-daemon

# Build Docker image
./gradlew jibDockerBuild
```

### Build Output

```
build/
├── classes/              # Compiled .class files
├── resources/            # Application resources
├── libs/
│   └── warehouse.jar     # Executable JAR (~80MB)
└── reports/              # Test and coverage reports
```

### Multi-Stage Docker Build

```bash
# Build Docker image
docker build -t warehouse-microservice:latest .

# Build with specific platform
docker build --platform linux/amd64 -t warehouse-microservice:latest .

# Build with no cache
docker build --no-cache -t warehouse-microservice:latest .
```

**Image Layers:**

1. Base JRE (Eclipse Temurin 25-jre-alpine) ~150MB
2. Application dependencies ~40MB
3. Application classes ~10MB

**Total Image Size**: ~200MB

---

## ▶️ Running the Application

### Method 1: Using Helper Script (Recommended)

```bash
# Make script executable (first time only)
chmod +x run.sh

# Run application (loads .env automatically)
./run.sh
```

**What `run.sh` does:**

1. ✅ Loads environment variables from `.env`
2. ✅ Exports variables to shell
3. ✅ Runs `./gradlew bootRun`

### Method 2: Gradle Directly

```bash
# Load environment variables first
export $(grep -v '^#' .env | xargs)

# Run application
./gradlew bootRun
```

### Method 3: Docker Container

```bash
# Build and run with Docker Compose
docker-compose up -d app

# Or build image and run manually
docker build -t warehouse-app .
docker run -p 8080:8080 --env-file .env warehouse-app
```

### Application Startup Sequence

```
[1] Loading environment variables                  ✅ 0s
[2] Initializing Spring Boot context               ⏳ 5s
[3] Detecting Docker Compose file                  ✅ 1s
[4] Waiting for PostgreSQL health check            ⏳ 10s
[5] Waiting for RabbitMQ health check              ⏳ 5s
[6] Running Flyway migrations                      ⏳ 2s
[7] Initializing Hibernate ORM                     ⏳ 15s
[8] Creating RabbitMQ queues and exchanges         ✅ 2s
[9] Configuring Spring Security                    ✅ 1s
[10] Starting Tomcat on port 8080                  ✅ 2s
────────────────────────────────────────────────────────
Total startup time: ~45-60 seconds
```

### Access the Application

| Service | URL | Credentials |
|---------|-----|-------------|
| **Application** | http://localhost:8080 | N/A |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | N/A |
| **Actuator** | http://localhost:8080/actuator | N/A |
| **Health Check** | http://localhost:8080/actuator/health | N/A |
| **RabbitMQ UI** | http://localhost:15672 | guest / guest |
| **pgAdmin** | http://localhost:5050 | admin@warehouse.com / admin |

---

## 🏥 Health Checks & Monitoring

### Actuator Endpoints

```bash
# Overall health status
curl http://localhost:8080/actuator/health

# Detailed health with components
curl http://localhost:8080/actuator/health/details

# Application info
curl http://localhost:8080/actuator/info

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Health Check Response

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "version": "16.10"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    },
    "rabbit": {
      "status": "UP",
      "details": {
        "version": "3.12.14"
      }
    }
  }
}
```

### Docker Health Checks

```bash
# Check container health
docker ps

# View health check logs
docker inspect --format='{{json .State.Health}}' warehouse-postgres | jq
```

### Monitoring with Prometheus

Metrics are exposed at: `/actuator/prometheus`

**Key Metrics:**

- `http_server_requests_seconds` - HTTP request durations
- `jvm_memory_used_bytes` - JVM memory usage
- `process_cpu_usage` - CPU usage
- `rabbitmq_acknowledged_total` - RabbitMQ message acknowledgements

---

## 🐛 Troubleshooting

### Common Issues

#### 1. **Application fails to start: "Docker is not running"**

**Cause**: Docker Desktop is not running or WSL2 integration is disabled.

**Solution**:
```bash
# Check Docker status
docker ps

# Start Docker Desktop on Windows
# Enable: Settings → Resources → WSL Integration → Enable for your distro
```

#### 2. **Build error: "Unsupported class file major version 69"**

**Cause**: Gradle version doesn't support Java 25.

**Solution**:
```bash
# Update Gradle Wrapper
./gradlew wrapper --gradle-version 9.1.0

# Or use system Gradle
sdk use gradle 9.1.0
```

#### 3. **Spring Cloud compatibility error**

**Error**: `Spring Boot [3.5.6] is not compatible with this Spring Cloud release train`

**Solution**: Update Spring Cloud version in `build.gradle.kts`:
```kotlin
extra["springCloudVersion"] = "2025.0.0"  // Not 2024.0.0
```

#### 4. **Environment variables not loaded**

**Cause**: `.env` file not being read by Gradle.

**Solution**: Use `run.sh` script or export manually:
```bash
export $(grep -v '^#' .env | xargs)
./gradlew bootRun
```

#### 5. **PostgreSQL connection refused**

**Cause**: Container not ready or wrong credentials.

**Solution**:
```bash
# Check container status
docker-compose ps

# Check logs
docker-compose logs postgres

# Verify credentials in .env match docker-compose.yml
```

#### 6. **RabbitMQ queues not created**

**Cause**: RabbitMQ not healthy or application started before RabbitMQ.

**Solution**:
```bash
# Restart RabbitMQ
docker-compose restart rabbitmq

# Wait for health check
docker-compose ps

# Restart application
```

#### 7. **Port 8080 already in use**

**Solution**:
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Or change port in .env
SERVER_PORT=8081
```

### Debug Mode

```bash
# Run with debug logging
./gradlew bootRun --args='--debug'

# Or set in application-dev.yml
logging:
  level:
    root: DEBUG
```

### Clean Restart

```bash
# Stop everything
docker-compose down -v

# Clean Gradle cache
./gradlew clean

# Remove all Docker containers and volumes
docker system prune -a --volumes

# Start fresh
docker-compose up -d
./run.sh
```

---

## 📚 Additional Resources

- **README.md** - Project overview and quick start
- **DOCKER_SETUP.md** - Detailed Docker configuration
- **SECURITY.md** - Security best practices
- **SETUP.md** - Development environment setup
- **docs/SPRINT_3_RABBITMQ.md** - RabbitMQ implementation details
- **RABBITMQ_IMPLEMENTATION_SUMMARY.md** - RabbitMQ architecture

---

## 📞 Support

For issues, questions, or contributions:

- **GitHub Issues**: https://github.com/CanduriFranklin/warehouse-franklindex.doo/issues
- **Documentation**: See `docs/` directory
- **Logs**: Check `docker-compose logs` for container logs

---

**Last Updated**: October 14, 2025  
**Version**: 1.0.0  
**Java**: 25 LTS | **Spring Boot**: 3.5.6 | **Spring Cloud**: 2025.0.0
