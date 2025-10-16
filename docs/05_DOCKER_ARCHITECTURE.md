# 🐳 Docker Architecture - Warehouse Microservice

**Comprehensive guide to Docker setup, multi-stage builds, and container orchestration**

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Docker Compose Architecture](#docker-compose-architecture)
3. [Multi-Stage Dockerfile](#multi-stage-dockerfile)
4. [Dockerignore Optimization](#dockerignore-optimization)
5. [Volumes and Persistence](#volumes-and-persistence)
6. [Networking](#networking)
7. [Health Checks](#health-checks)
8. [Best Practices](#best-practices)

---

## 🎯 Overview

This project uses **Docker and Docker Compose** to create a complete development and production environment with:

- **PostgreSQL 16** - Relational database
- **RabbitMQ 3.12** - Message broker with management UI
- **pgAdmin 4** - Database management tool
- **Spring Boot Application** - Containerized microservice

### Benefits

✅ **Consistency** - Same environment across dev, test, and production  
✅ **Isolation** - Each service in its own container  
✅ **Scalability** - Easy horizontal scaling  
✅ **Portability** - Runs anywhere Docker runs  
✅ **Efficiency** - Multi-stage builds reduce image size by 75%  

---

## 🏗️ Docker Compose Architecture

### Network Diagram

```
┌────────────────────────────────────────────────────────────────┐
│                     warehouse-network (bridge)                  │
│                                                                 │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐      │
│  │ PostgreSQL   │   │  RabbitMQ    │   │   pgAdmin    │      │
│  │    :5432     │   │ :5672/:15672 │   │    :5050     │      │
│  │              │   │              │   │              │      │
│  │  Volume:     │   │  Volumes:    │   │  Volume:     │      │
│  │  postgres_   │   │  rabbitmq_   │   │  pgadmin_    │      │
│  │  data        │   │  data, logs  │   │  data        │      │
│  └──────────────┘   └──────────────┘   └──────────────┘      │
│         ▲                  ▲                   ▲               │
│         │                  │                   │               │
│         └──────────────────┴───────────────────┘               │
│                            │                                    │
└────────────────────────────┼────────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │  Spring Boot    │
                    │  Application    │
                    │    (Host)       │
                    │   Port: 8080    │
                    └─────────────────┘
```

### docker-compose.yml Structure

```yaml
version: '3.9'

services:
  postgres:
    # PostgreSQL 16 database
    
  rabbitmq:
    # RabbitMQ 3.12 message broker
    
  pgadmin:
    # pgAdmin 4 database management UI

volumes:
  postgres_data:
  rabbitmq_data:
  rabbitmq_logs:
  pgadmin_data:

networks:
  warehouse-network:
    driver: bridge
```

### Service Details

#### PostgreSQL Service

```yaml
postgres:
  image: postgres:16-alpine
  container_name: warehouse-postgres
  restart: unless-stopped
  environment:
    POSTGRES_DB: warehouse_db
    POSTGRES_USER: warehouse_user
    POSTGRES_PASSWORD: warehouse_secure_2025
    PGDATA: /var/lib/postgresql/data/pgdata
  ports:
    - "5432:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
    - ./init-scripts:/docker-entrypoint-initdb.d  # Optional
  networks:
    - warehouse-network
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U warehouse_user -d warehouse_db"]
    interval: 10s
    timeout: 5s
    retries: 5
    start_period: 10s
```

**Key Features:**
- ✅ **Alpine base** - Smaller image size (~80MB vs 200MB+)
- ✅ **Custom PGDATA** - Proper data directory structure
- ✅ **Health check** - Container won't be marked healthy until DB is ready
- ✅ **Init scripts** - Automatic SQL execution on first run
- ✅ **Restart policy** - Auto-restart unless explicitly stopped

#### RabbitMQ Service

```yaml
rabbitmq:
  image: rabbitmq:3.12-management-alpine
  container_name: warehouse-rabbitmq
  restart: unless-stopped
  user: rabbitmq:rabbitmq  # Fix permissions
  environment:
    RABBITMQ_DEFAULT_USER: guest
    RABBITMQ_DEFAULT_PASS: guest
    RABBITMQ_DEFAULT_VHOST: /
    RABBITMQ_ERLANG_COOKIE: warehouse_secret_cookie_2025
  ports:
    - "5672:5672"   # AMQP protocol
    - "15672:15672" # Management UI
  volumes:
    - rabbitmq_data:/var/lib/rabbitmq
    - rabbitmq_logs:/var/log/rabbitmq
  networks:
    - warehouse-network
  healthcheck:
    test: ["CMD", "rabbitmq-diagnostics", "ping"]
    interval: 30s
    timeout: 10s
    retries: 5
```

**Key Features:**
- ✅ **Management plugin** - Web UI included
- ✅ **Erlang cookie** - Required for clustering
- ✅ **User/group** - Fixes permission issues
- ✅ **Separate log volume** - Easier log management
- ✅ **Health check** - Validates RabbitMQ is fully operational

#### pgAdmin Service

```yaml
pgadmin:
  image: dpage/pgadmin4:latest
  container_name: warehouse-pgadmin
  restart: unless-stopped
  environment:
    PGADMIN_DEFAULT_EMAIL: admin@warehouse.com
    PGADMIN_DEFAULT_PASSWORD: admin
    PGADMIN_CONFIG_SERVER_MODE: 'False'
  ports:
    - "5050:80"
  volumes:
    - pgadmin_data:/var/lib/pgadmin
  networks:
    - warehouse-network
  depends_on:
    - postgres
```

**Key Features:**
- ✅ **Latest version** - Always up-to-date
- ✅ **Desktop mode** - No login required after initial setup
- ✅ **Persistent config** - Server connections saved
- ✅ **Depends on** - Waits for PostgreSQL to start

---

## 🎨 Multi-Stage Dockerfile

### Optimization Strategy

Our Dockerfile uses **3 stages** to minimize final image size:

```
Stage 1: Build     Stage 2: Dependencies    Stage 3: Runtime
  (800MB)              (200MB)                 (~200MB)
     │                    │                        │
     ▼                    ▼                        ▼
┌──────────┐        ┌──────────┐           ┌──────────┐
│  Compile │   →    │ Extract  │      →    │   Run    │
│   Java   │        │   Deps   │           │   App    │
└──────────┘        └──────────┘           └──────────┘
```

### Complete Dockerfile

```dockerfile
# ===================================================================
# Stage 1: Build Stage
# Purpose: Compile Java application
# Base Image: eclipse-temurin:25-jdk-alpine (build tools included)
# Size: ~800MB (not in final image)
# ===================================================================
FROM eclipse-temurin:25-jdk-alpine AS builder

# Install Gradle (if not using Gradle Wrapper)
WORKDIR /workspace/app

# Copy Gradle files first (better layer caching)
COPY gradle gradle
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build application (skip tests for faster builds)
RUN ./gradlew build -x test --no-daemon && \
    mkdir -p build/dependency && \
    (cd build/dependency; jar -xf ../libs/*.jar)

# ===================================================================
# Stage 2: Dependencies Extraction
# Purpose: Extract and organize dependencies
# Why: Separates dependencies from application code for better caching
# ===================================================================
FROM eclipse-temurin:25-jdk-alpine AS dependency

WORKDIR /workspace/app

COPY --from=builder /workspace/app/build/libs/*.jar application.jar

RUN mkdir -p /app/dependencies && \
    cd /app/dependencies && \
    jar -xf /workspace/app/application.jar

# ===================================================================
# Stage 3: Runtime Stage
# Purpose: Minimal image with just JRE and application
# Base Image: eclipse-temurin:25-jre-alpine (runtime only)
# Size: ~200MB (75% reduction!)
# ===================================================================
FROM eclipse-temurin:25-jre-alpine

# Metadata
LABEL maintainer="Franklin Canduri <canduri.franklin@example.com>"
LABEL version="1.0.0"
LABEL description="Warehouse Microservice - Modern inventory management"

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set working directory
WORKDIR /app

# Copy dependencies from stage 2
COPY --from=dependency /app/dependencies/BOOT-INF/lib /app/lib
COPY --from=dependency /app/dependencies/META-INF /app/META-INF
COPY --from=dependency /app/dependencies/BOOT-INF/classes /app

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-cp", ".:./lib/*", \
            "br.com.dio.warehouse.WarehouseApplication"]
```

### Build Stages Explained

#### Stage 1: Builder
- Uses full JDK (needed for compilation)
- Installs Gradle Wrapper
- Downloads dependencies (cached layer)
- Compiles Java code
- Creates executable JAR

#### Stage 2: Dependency
- Extracts JAR contents
- Organizes dependencies separately
- Prepares for efficient copying to runtime

#### Stage 3: Runtime
- Uses minimal JRE (no build tools)
- Non-root user (security)
- Only copies necessary files
- Configures JVM for containers
- Adds health check

### Build Commands

```bash
# Build with default tag
docker build -t warehouse-microservice:latest .

# Build with specific tag
docker build -t warehouse-microservice:1.0.0 .

# Build for specific platform
docker build --platform linux/amd64 -t warehouse-microservice:latest .

# Build without cache (force rebuild)
docker build --no-cache -t warehouse-microservice:latest .

# Build and immediately run
docker build -t warehouse-microservice:latest . && \
docker run -p 8080:8080 --env-file .env warehouse-microservice:latest
```

### Image Size Comparison

| Approach | Image Size | Layers | Build Time |
|----------|------------|--------|------------|
| **Single-stage (JDK)** | ~800MB | 8 | 3 min |
| **Multi-stage (JRE)** | ~200MB | 6 | 3 min |
| **Improvement** | **75% smaller** | 25% fewer | Same |

---

## 🚫 Dockerignore Optimization

### Purpose

`.dockerignore` tells Docker which files to **exclude** when building images, resulting in:

- ⚡ **Faster builds** - Less data to transfer
- 📦 **Smaller context** - Less memory usage
- 🔒 **Better security** - Sensitive files not copied

### Complete .dockerignore

```dockerignore
# ===================================================================
# .dockerignore - Warehouse Microservice
# Purpose: Exclude unnecessary files from Docker build context
# ===================================================================

# Build artifacts
build/
target/
out/
*.jar
*.war
*.class

# Gradle
.gradle/
gradle-*.lock
**/gradlew.bat

# Environment files (SECURITY!)
.env
.env.local
.env.*.local
*.pem
*.key
*.crt

# IDE files
.idea/
.vscode/
*.iml
*.ipr
*.iws
.classpath
.project
.settings/

# Git
.git/
.gitignore
.gitattributes

# Documentation (not needed in image)
docs/
*.md
!README.md  # Keep main README

# Logs
logs/
*.log

# Test files
src/test/
**/test/

# Docker files (don't copy into itself)
Dockerfile*
docker-compose*.yml
.dockerignore

# OS files
.DS_Store
Thumbs.db

# Temporary files
tmp/
temp/
*.tmp
*.swp
*~

# CI/CD
.github/
.gitlab-ci.yml
.travis.yml
Jenkinsfile
```

### File Categories

| Category | Purpose | Examples |
|----------|---------|----------|
| **Build artifacts** | Already compiled | `build/`, `*.jar` |
| **Development tools** | Not needed at runtime | `.gradle/`, `.idea/` |
| **Sensitive data** | Security risk | `.env`, `*.key` |
| **Documentation** | Not needed at runtime | `docs/`, `*.md` |
| **Test files** | Only for development | `src/test/` |

### Impact

```
Without .dockerignore:
┌──────────────────────────────────┐
│  Build Context: 500MB            │
│  - Source code: 50MB             │
│  - Build artifacts: 200MB        │
│  - .git: 100MB                   │
│  - node_modules: 150MB           │
└──────────────────────────────────┘

With .dockerignore:
┌──────────────────────────────────┐
│  Build Context: 50MB (-90%)      │
│  - Source code: 50MB             │
└──────────────────────────────────┘
```

---

## 💾 Volumes and Persistence

### Docker Volumes Overview

Volumes persist data **outside containers**, so data survives:
- Container restarts
- Container deletions
- Image updates

### Defined Volumes

```yaml
volumes:
  postgres_data:      # PostgreSQL database files
  rabbitmq_data:      # RabbitMQ messages and metadata
  rabbitmq_logs:      # RabbitMQ logs
  pgadmin_data:       # pgAdmin configuration
```

### Volume Management Commands

```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect warehouse_postgres_data

# Remove specific volume (CAUTION: Deletes data!)
docker volume rm warehouse_postgres_data

# Remove all unused volumes
docker volume prune

# Backup volume
docker run --rm -v warehouse_postgres_data:/data -v $(pwd):/backup \
  alpine tar czf /backup/postgres_backup.tar.gz /data

# Restore volume
docker run --rm -v warehouse_postgres_data:/data -v $(pwd):/backup \
  alpine tar xzf /backup/postgres_backup.tar.gz -C /
```

### Volume Locations

Volumes are stored in Docker's data directory:

- **Linux**: `/var/lib/docker/volumes/`
- **Windows (WSL2)**: `\\wsl$\docker-desktop-data\data\docker\volumes\`
- **Mac**: `~/Library/Containers/com.docker.docker/Data/vms/0/data/docker/volumes/`

---

## 🌐 Networking

### Network Configuration

```yaml
networks:
  warehouse-network:
    driver: bridge
```

### How It Works

1. **Isolated Network**: Services can communicate using container names
2. **DNS Resolution**: Docker provides internal DNS (`warehouse-postgres` resolves to container IP)
3. **Port Mapping**: External access via `localhost:port`

### Communication Patterns

```
Internal (Container ↔ Container):
  pgAdmin → warehouse-postgres:5432  ✅
  App → warehouse-postgres:5432      ✅
  App → warehouse-rabbitmq:5672      ✅

External (Host → Container):
  Host → localhost:5432              ✅
  Host → localhost:15672             ✅
  Host → localhost:5050              ✅
```

### Network Commands

```bash
# Inspect network
docker network inspect warehouse-network

# List connected containers
docker network inspect warehouse-network \
  --format '{{range .Containers}}{{.Name}}{{end}}'

# Disconnect container
docker network disconnect warehouse-network warehouse-postgres

# Reconnect container
docker network connect warehouse-network warehouse-postgres
```

---

## 🏥 Health Checks

### Why Health Checks?

- ✅ Know when services are **truly ready** (not just started)
- ✅ Automatic **restart** of unhealthy containers
- ✅ **Orchestration** waits for health before routing traffic

### PostgreSQL Health Check

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U warehouse_user -d warehouse_db"]
  interval: 10s
  timeout: 5s
  retries: 5
  start_period: 10s
```

**What it does:**
- Runs `pg_isready` every 10 seconds
- Waits 10 seconds before first check (start_period)
- Fails if command takes > 5 seconds
- Marks unhealthy after 5 consecutive failures

### RabbitMQ Health Check

```yaml
healthcheck:
  test: ["CMD", "rabbitmq-diagnostics", "ping"]
  interval: 30s
  timeout: 10s
  retries: 5
```

### Check Health Status

```bash
# View health status
docker ps

# Expected output:
# STATUS
# Up 2 minutes (healthy)

# Detailed health info
docker inspect --format='{{json .State.Health}}' warehouse-postgres | jq

# Health check logs
docker inspect warehouse-postgres | jq '.[].State.Health.Log'
```

---

## 🎯 Best Practices

### 1. **Use Specific Image Versions**

```yaml
# ❌ Bad (unpredictable updates)
image: postgres:latest

# ✅ Good (reproducible builds)
image: postgres:16-alpine
```

### 2. **Alpine Images for Smaller Size**

```yaml
# Regular: ~200MB
image: postgres:16

# Alpine: ~80MB (60% smaller)
image: postgres:16-alpine
```

### 3. **Non-Root User in Containers**

```dockerfile
# Create user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
```

### 4. **Health Checks for Reliability**

Always add health checks for critical services.

### 5. **Restart Policies**

```yaml
restart: unless-stopped  # Restart unless explicitly stopped
# restart: always        # Always restart
# restart: on-failure    # Only on errors
```

### 6. **Resource Limits**

```yaml
deploy:
  resources:
    limits:
      cpus: '0.5'
      memory: 512M
    reservations:
      cpus: '0.25'
      memory: 256M
```

### 7. **Environment Variables**

```yaml
environment:
  POSTGRES_PASSWORD: ${DB_PASSWORD}  # From .env file
```

### 8. **Volume Backups**

Regular backups of important volumes:

```bash
# Automated backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker run --rm \
  -v warehouse_postgres_data:/data \
  -v $(pwd)/backups:/backup \
  alpine tar czf /backup/postgres_$DATE.tar.gz /data
```

---

## 📊 Summary

### Benefits of This Docker Setup

| Feature | Benefit |
|---------|---------|
| **Multi-stage builds** | 75% smaller images |
| **.dockerignore** | 90% faster builds |
| **Health checks** | Automatic recovery |
| **Volumes** | Data persistence |
| **Networks** | Secure communication |
| **Alpine images** | Minimal attack surface |

### Resource Usage

```
Total Docker Footprint:
├── PostgreSQL:  ~100MB RAM, 5GB disk
├── RabbitMQ:    ~150MB RAM, 2GB disk
├── pgAdmin:     ~80MB RAM, 1GB disk
└── Application: ~300MB RAM, 200MB disk
──────────────────────────────────────
Total:           ~630MB RAM, 8.2GB disk
```

---

**Last Updated**: October 14, 2025  
**Docker Version**: 28.4.0  
**Docker Compose**: 3.9
