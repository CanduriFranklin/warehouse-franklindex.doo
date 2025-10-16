# 🐳 Docker Setup Guide - Warehouse Microservice

Guia completo para configurar e executar o ambiente de desenvolvimento usando Docker e Docker Compose.

---

## 📋 Tabela de Conteúdos

1. [Pré-requisitos](#pré-requisitos)
2. [Serviços Disponíveis](#serviços-disponíveis)
3. [Configuração Rápida](#configuração-rápida)
4. [Comandos Úteis](#comandos-úteis)
5. [Otimizações Docker](#otimizações-docker)
6. [Troubleshooting](#troubleshooting)
7. [Produção](#produção)

---

## 🔧 Pré-requisitos

- **Docker** 24.0+ instalado ([Download](https://www.docker.com/products/docker-desktop))
- **Docker Compose** 2.0+ (incluído no Docker Desktop)
- **WSL2** (se estiver no Windows)
- **8GB RAM** disponível (mínimo 4GB)

### Verificar Instalação

```bash
docker --version
# Docker version 24.0.0 ou superior

docker-compose --version
# Docker Compose version v2.0.0 ou superior
```

---

## 🏗️ Serviços Disponíveis

| Serviço | Porta | Descrição | Interface Web | Profile |
|---------|-------|-----------|---------------|---------|
| **PostgreSQL** | 5432 | Banco de dados relacional | - | - |
| **RabbitMQ** | 5672 | Message broker (AMQP) | http://localhost:15672 | - |
| **RabbitMQ Management** | 15672 | Interface de gerenciamento | ✅ Sim | - |
| **pgAdmin** | 5050 | Interface web para PostgreSQL | http://localhost:5050 | tools |
| **Warehouse API** | 8080 | Aplicação Spring Boot | http://localhost:8080 | - |
| **Frontend Dev** | 5173 | React com Vite (hot reload) | http://localhost:5173 | dev |
| **Frontend Prod** | 80 | React + Nginx (otimizado) | http://localhost | prod |

### 📊 Consumo de Recursos por Serviço

| Serviço | CPU Limit | RAM Limit | CPU Reserved | RAM Reserved |
|---------|-----------|-----------|--------------|--------------|
| **PostgreSQL** | 1.0 | 512M | 0.5 | 256M |
| **RabbitMQ** | 1.0 | 512M | 0.5 | 256M |
| **pgAdmin** | 0.5 | 256M | 0.25 | 128M |
| **Frontend Dev** | 1.0 | 512M | 0.5 | 256M |
| **Frontend Prod** | 0.5 | 128M | 0.25 | 64M |

### Credenciais Padrão

**PostgreSQL:**
- Database: `warehouse_db`
- User: `warehouse_user`
- Password: `warehouse_pass`

**RabbitMQ:**
- User: `guest`
- Password: `guest`

**pgAdmin:**
- Email: `admin@warehouse.com`
- Password: `admin`

### 🚀 Frontend: Desenvolvimento vs Produção

#### **Frontend Dev** (Profile: `dev`)
- **Tecnologia**: Node.js 20 + Vite
- **Features**:
  - Hot Module Replacement (HMR)
  - Source maps para debugging
  - Fast refresh (React)
  - Mapeamento de volume (código local)
- **Uso**: Desenvolvimento local com auto-reload
- **Porta**: 5173
- **Comando**: `docker compose --profile dev up frontend-dev`

#### **Frontend Prod** (Profile: `prod`)
- **Tecnologia**: Nginx 1.27 Alpine
- **Features**:
  - Multi-stage build otimizado
  - Gzip compression (60-80% menor)
  - Security headers (XSS, clickjacking)
  - Cache de assets estáticos (1 ano)
  - React Router support (SPA)
  - Healthcheck integrado
- **Imagem**: ~20 MB (vs ~500 MB sem otimização)
- **Uso**: Teste de produção local ou deploy
- **Porta**: 80
- **Comando**: `docker compose --profile prod up --build frontend-prod`

---

## 🚀 Configuração Rápida

### 1. Criar Arquivo de Variáveis de Ambiente

```bash
# Copiar template
cp .env.example .env

# Editar valores (OBRIGATÓRIO para produção)
nano .env
```

**Variáveis importantes em `.env`:**
```env
DB_HOST=localhost          # Use 'postgres' se rodar app no Docker
DB_PORT=5432
DB_NAME=warehouse_db
DB_USERNAME=warehouse_user
DB_PASSWORD=warehouse_pass

RABBITMQ_HOST=localhost    # Use 'rabbitmq' se rodar app no Docker
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

JWT_SECRET=$(openssl rand -base64 64)  # Gerar segredo forte
```

### 2. Iniciar Serviços com Profiles

> **✨ NOVO**: Suporte a profiles para execução seletiva de serviços!

```bash
# Apenas infraestrutura básica (PostgreSQL + RabbitMQ)
docker compose up -d

# Infraestrutura + Frontend em modo desenvolvimento (hot reload)
docker compose --profile dev up frontend-dev

# Infraestrutura + Frontend em modo produção (Nginx otimizado)
docker compose --profile prod up --build frontend-prod

# Infraestrutura + Ferramentas de administração (pgAdmin)
docker compose --profile tools up pgadmin

# Combinar múltiplos profiles
docker compose --profile dev --profile tools up

# Verificar status
docker compose ps

# Ver logs
docker compose logs -f
```

**Profiles disponíveis:**

| Profile | Serviços Adicionais | Uso Recomendado |
|---------|---------------------|-----------------|
| **(nenhum)** | postgres, rabbitmq | Infraestrutura básica |
| **dev** | frontend-dev | Desenvolvimento com hot reload |
| **prod** | frontend-prod | Teste de produção local |
| **tools** | pgadmin | Administração de banco de dados |

**Consumo de recursos por profile:**

| Profile | CPU Total | RAM Total |
|---------|-----------|-----------|
| Nenhum | 2.0 cores | 1024 MB |
| dev | 3.0 cores | 1536 MB |
| prod | 2.5 cores | 1152 MB |
| tools | 2.5 cores | 1280 MB |

### 3. Aguardar Health Checks

```bash
# PostgreSQL
docker exec warehouse-postgres pg_isready -U warehouse_user

# RabbitMQ
docker exec warehouse-rabbitmq rabbitmq-diagnostics ping
```

### 4. Acessar Interfaces Web

- **RabbitMQ Management:** http://localhost:15672
  - Login: `guest` / `guest`
  - Visualizar queues, exchanges, mensagens

- **pgAdmin:** http://localhost:5050
  - Login: `admin@warehouse.com` / `admin`
  - Adicionar servidor: `postgres:5432`

### 5. Executar Aplicação Spring Boot

**Opção A: Rodar localmente (sem Docker)**
```bash
# Usar serviços Docker, app local
./gradlew bootRun
```

**Opção B: Rodar tudo no Docker**
```bash
# Descomentar seção 'warehouse-app' no docker-compose.yml
# Depois:
docker-compose up -d --build
```

---

## 📦 Comandos Úteis

### Gerenciamento de Containers

```bash
# Iniciar todos os serviços
docker-compose up -d

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (CUIDADO: apaga dados!)
docker-compose down -v

# Reiniciar serviço específico
docker-compose restart postgres
docker-compose restart rabbitmq

# Ver logs de um serviço
docker-compose logs -f postgres
docker-compose logs -f rabbitmq

# Ver logs das últimas 100 linhas
docker-compose logs --tail=100 rabbitmq
```

### Acesso aos Containers

```bash
# PostgreSQL CLI
docker exec -it warehouse-postgres psql -U warehouse_user -d warehouse_db

# RabbitMQ CLI
docker exec -it warehouse-rabbitmq rabbitmqctl status

# Shell do container
docker exec -it warehouse-postgres sh
```

### Limpeza e Manutenção

```bash
# Remover containers parados
docker container prune

# Remover imagens não utilizadas
docker image prune -a

# Remover volumes não utilizados (CUIDADO!)
docker volume prune

# Limpar tudo (MUITO CUIDADO!)
docker system prune -a --volumes

# Ver uso de espaço
docker system df
```

### Backup e Restore

```bash
# Backup PostgreSQL
docker exec warehouse-postgres pg_dump -U warehouse_user warehouse_db > backup_$(date +%Y%m%d).sql

# Restore PostgreSQL
docker exec -i warehouse-postgres psql -U warehouse_user -d warehouse_db < backup_20251014.sql

# Backup RabbitMQ (definitions)
curl -u guest:guest http://localhost:15672/api/definitions > rabbitmq_backup.json

# Restore RabbitMQ
curl -u guest:guest -H "Content-Type: application/json" \
  -X POST http://localhost:15672/api/definitions \
  -d @rabbitmq_backup.json
```

---

## ⚡ Otimizações Docker

### 1. Multi-Stage Build

O `Dockerfile` usa **multi-stage build** para:
- ✅ Reduzir tamanho da imagem (build 800MB → runtime 250MB)
- ✅ Separar dependências de build e runtime
- ✅ Usar JRE ao invés de JDK em produção
- ✅ Melhorar segurança (menos pacotes = menos vulnerabilidades)

**Estrutura:**
```dockerfile
# Stage 1: Build (eclipse-temurin:25-jdk-alpine)
# - Compilar código
# - Baixar dependências
# - Gerar JAR

# Stage 2: Runtime (eclipse-temurin:25-jre-alpine)
# - Copiar apenas artifacts necessários
# - Executar como usuário não-root
# - Imagem mínima
```

### 2. Dockerignore

O `.dockerignore` **exclui arquivos desnecessários**:
- ❌ Build outputs (`build/`, `target/`)
- ❌ IDE files (`.idea/`, `.vscode/`)
- ❌ Git history (`.git/`)
- ❌ Tests (`src/test/`)
- ❌ Documentation (`docs/`, `*.md`)
- ❌ Secrets (`.env`, `*.key`)

**Resultado:** Contexto de build reduzido de ~500MB para ~50MB

### 3. Layer Caching

```dockerfile
# 1. Copiar apenas arquivos Gradle (cache dependencies)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .

# 2. Baixar dependências (cached se build.gradle não mudar)
RUN ./gradlew dependencies

# 3. Copiar código (só invalida cache se código mudar)
COPY src src
```

### 4. Healthchecks

Todos os serviços têm healthchecks configurados:
```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U warehouse_user"]
  interval: 10s
  timeout: 5s
  retries: 5
```

**Benefícios:**
- ✅ Sabe quando serviço está pronto
- ✅ Reinicia automaticamente se falhar
- ✅ `depends_on` espera serviço ficar healthy

### 5. Volumes Nomeados

```yaml
volumes:
  postgres_data:
    name: warehouse_postgres_data
```

**Vantagens:**
- ✅ Dados persistem entre restarts
- ✅ Fácil backup/restore
- ✅ Performance melhor que bind mounts

---

## 🔍 Troubleshooting

### Problema: Porta já em uso

```bash
# Erro: Bind for 0.0.0.0:5432 failed: port is already allocated

# Solução 1: Encontrar processo usando a porta
lsof -i :5432
kill -9 <PID>

# Solução 2: Mudar porta no docker-compose.yml
ports:
  - "5433:5432"  # Porta externa diferente
```

### Problema: Container reinicia continuamente

```bash
# Ver logs completos
docker logs warehouse-postgres

# Verificar healthcheck
docker inspect warehouse-postgres | grep -A 10 Health

# Verificar recursos
docker stats
```

### Problema: RabbitMQ não aceita conexões

```bash
# Verificar status
docker exec warehouse-rabbitmq rabbitmqctl status

# Ver logs
docker logs warehouse-rabbitmq --tail 100

# Reiniciar com reset
docker-compose down
docker volume rm warehouse_rabbitmq_data
docker-compose up -d rabbitmq
```

### Problema: PostgreSQL não aceita conexões

```bash
# Verificar se está rodando
docker exec warehouse-postgres pg_isready

# Testar conexão
docker exec -it warehouse-postgres psql -U warehouse_user -d warehouse_db -c "SELECT 1;"

# Ver configurações de conexão
docker exec warehouse-postgres cat /var/lib/postgresql/data/postgresql.conf | grep listen_addresses
```

### Problema: Aplicação não conecta no banco

```bash
# Erro comum: Connection refused

# Se app roda FORA do Docker, use localhost
DB_HOST=localhost
DB_PORT=5432

# Se app roda DENTRO do Docker, use nome do serviço
DB_HOST=postgres
DB_PORT=5432

# Verificar network
docker network inspect warehouse-network
```

### Problema: Memória insuficiente

```bash
# Ver uso de memória
docker stats

# Aumentar memória no Docker Desktop
# Settings → Resources → Memory → 8GB

# Ou limitar containers
services:
  postgres:
    mem_limit: 2g
    mem_reservation: 1g
```

---

## 🌐 Produção

### 1. Usar Docker Secrets (Docker Swarm)

```yaml
secrets:
  db_password:
    external: true

services:
  postgres:
    secrets:
      - db_password
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
```

### 2. Usar Variáveis de Ambiente Externas

```bash
# NÃO fazer isso em produção
POSTGRES_PASSWORD=senha123

# Usar secrets management
# AWS Secrets Manager, Azure Key Vault, HashiCorp Vault
```

### 3. Habilitar TLS/SSL

```yaml
services:
  postgres:
    environment:
      POSTGRES_INITDB_ARGS: "--auth-host=scram-sha-256"
    command: >
      -c ssl=on
      -c ssl_cert_file=/var/lib/postgresql/server.crt
      -c ssl_key_file=/var/lib/postgresql/server.key
```

### 4. Limitar Recursos

```yaml
services:
  warehouse-app:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 2G
        reservations:
          cpus: '1.0'
          memory: 1G
```

### 5. Usar Registry Privado

```bash
# Build e tag
docker build -t myregistry.azurecr.io/warehouse:1.0.0 .

# Push
docker push myregistry.azurecr.io/warehouse:1.0.0

# Pull em produção
docker pull myregistry.azurecr.io/warehouse:1.0.0
```

---

## 📊 Monitoramento

### Ver Métricas

```bash
# CPU, Memória, Network, Disk I/O
docker stats

# Ver apenas warehouse services
docker stats warehouse-postgres warehouse-rabbitmq
```

### Logs Centralizados

```bash
# Enviar logs para arquivo
docker-compose logs > warehouse_logs_$(date +%Y%m%d).log

# Usar driver de log (Syslog, Fluentd, etc.)
services:
  warehouse-app:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

---

## 🎯 Próximos Passos

1. ✅ **Configurar ambiente local** - `docker-compose up -d`
2. ✅ **Acessar RabbitMQ Management** - http://localhost:15672
3. ✅ **Acessar pgAdmin** - http://localhost:5050
4. ✅ **Executar migrations** - Flyway automático
5. ✅ **Testar API** - http://localhost:8080/swagger-ui.html
6. 📝 **Configurar CI/CD** - Build e push automático de imagens
7. 🚀 **Deploy em Kubernetes** - Helm charts, ingress, auto-scaling

---

## 📚 Referências

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [PostgreSQL Docker](https://hub.docker.com/_/postgres)
- [RabbitMQ Docker](https://hub.docker.com/_/rabbitmq)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)

---

**Autor:** Warehouse Development Team  
**Data:** Outubro 2025  
**Versão:** 1.0.0
