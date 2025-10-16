# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [1.1.0] - 2025-10-16

### 🐳 Otimizações Docker

#### Adicionado

- **Resource Limits** configurados para todos os serviços Docker
  - PostgreSQL: 1.0 CPU / 512M RAM (limit), 0.5 CPU / 256M RAM (reservation)
  - RabbitMQ: 1.0 CPU / 512M RAM (limit) + memory watermark de 256MB
  - pgAdmin: 0.5 CPU / 256M RAM (limit), apenas com profile `tools`
  - Frontend Dev: 1.0 CPU / 512M RAM (limit), profile `dev`
  - Frontend Prod: 0.5 CPU / 128M RAM (limit), profile `prod`

- **Profiles de Execução** no Docker Compose
  - Profile `dev` - Frontend em modo desenvolvimento (hot reload com Vite)
  - Profile `prod` - Frontend em modo produção (Nginx otimizado)
  - Profile `tools` - Ferramentas de administração (pgAdmin)
  - Sem profile - Apenas infraestrutura básica (PostgreSQL + RabbitMQ)

- **Multi-Stage Build para Frontend** (`frontend/Dockerfile.prod`)
  - Stage 1: node:20-alpine para build da aplicação React
  - Stage 2: nginx:1.27-alpine para servir arquivos estáticos
  - Redução de **96%** no tamanho da imagem (de ~500MB para ~20MB)

- **Nginx Otimizado** (`frontend/nginx.conf`)
  - Gzip compression para reduzir bandwidth (60-80% menor)
  - Security headers (X-Frame-Options, X-XSS-Protection, X-Content-Type-Options)
  - Cache de assets estáticos por 1 ano (JS, CSS, imagens, fonts)
  - Suporte a React Router (SPA com client-side routing)
  - Health check endpoint em `/health`

- **Frontend .dockerignore**
  - Exclui node_modules, dist, .env*, coverage, IDE files
  - Redução do build context de ~500MB para ~50MB
  - Builds 50-60% mais rápidos

- **Health Checks** padronizados
  - PostgreSQL: `pg_isready`
  - RabbitMQ: `rabbitmq-diagnostics ping`
  - Frontend Prod: `wget` check no Nginx

- **Documentação Completa**
  - `docs/DOCKER_OPTIMIZATION.md` - Detalhes técnicos de todas as otimizações
  - Benchmarks de performance (tempo de build, startup, consumo)
  - Comparações antes/depois
  - Guias de uso para diferentes cenários

#### Modificado

- **docker-compose.yml**
  - Adicionados resource limits em todos os serviços existentes
  - Implementados profiles para execução seletiva
  - Adicionados serviços frontend-dev e frontend-prod
  - RabbitMQ memory watermark configurado (256MB)
  - pgAdmin movido para profile `tools` (não inicia por padrão)

- **.gitignore**
  - Adicionadas ~100 novas entradas
  - Frontend: node_modules, dist, .env files, npm logs
  - Database: pgdata, *.db, *.sqlite, data directories
  - Docker: volumes, mnesia, docker-data
  - Logs: *.log, logs/, *.pid files
  - Sensíveis: application-prod.yml, jwt-secret.txt, certificados
  - Cloud: .terraform, *-credentials.json

- **README.md**
  - Adicionada seção "Otimizações Docker (16/10/2025)"
  - Tabela de consumo de recursos por profile
  - Exemplos de comandos com profiles
  - Informações sobre multi-stage build e Nginx

- **docs/DOCKER_SETUP.md**
  - Documentação atualizada com profiles
  - Tabela de serviços expandida (inclui frontend)
  - Consumo de recursos por serviço
  - Guia de uso para desenvolvimento vs produção

- **docs/STATUS.md**
  - Data atualizada para 16/10/2025
  - Versão atualizada para 1.1.0-SNAPSHOT
  - Seção de otimizações Docker adicionada
  - Resultados mensuráveis (benchmarks)

### 📊 Melhorias de Performance

| Métrica | Antes | Depois | Melhoria |
|---------|-------|--------|----------|
| **Imagem Frontend** | 512 MB | 20 MB | **96% menor** |
| **Build Time (Frontend)** | 3-5 min | 1-2 min | **50-60% mais rápido** |
| **Startup Time (Frontend)** | 15-20s | 5-8s | **60% mais rápido** |
| **RAM Total (Dev)** | Sem limite | 1792 MB | **Controlado** |
| **RAM Total (Prod)** | Sem limite | 1152 MB | **Controlado** |
| **Build Context** | 500 MB | 50 MB | **90% menor** |

### 🔒 Segurança

- Security headers no Nginx (proteção contra XSS, clickjacking, MIME sniffing)
- Redução de superfície de ataque (menos pacotes na imagem final)
- Arquivos sensíveis adicionados ao .gitignore
- Container Nginx roda com usuário não-root

---

## [1.0.0] - 2025-10-15

### ✅ Warehouse Microservice - Production Ready

#### Adicionado

- **Domain Layer** (Domain-Driven Design)
  - Agregados: `BasicBasket`, `DeliveryBox`
  - Value Objects: `Money`
  - Domain Events: `DeliveryReceivedEvent`, `BasketsSoldEvent`, etc.
  - Domain Exceptions: `InsufficientStockException`, `InvalidQuantityException`
  - Repository Interfaces (Port Out)

- **Application Layer** (Hexagonal Architecture)
  - 5 Use Cases implementados:
    - `ReceiveDeliveryUseCase` - Recebimento de entregas
    - `SellBasketsUseCase` - Venda de cestas básicas
    - `DisposeExpiredBasketsUseCase` - Descarte de vencidos
    - `CheckStockUseCase` - Consulta de estoque
    - `GetCashRegisterUseCase` - Caixa registradora
  - Input Ports (Commands e Results como records)
  - Output Ports (`EventPublisher`)
  - Services (implementações dos Use Cases)

- **Infrastructure Layer**
  - PostgreSQL 16 com Flyway migrations
  - RabbitMQ 3.13 para mensageria assíncrona
  - JPA Repositories (adaptadores de persistência)
  - `LoggingEventPublisher` (publicador de eventos temporário)

- **Adapter Layer (REST API)**
  - 4 Controllers REST:
    - `DeliveryController` - POST /api/v1/deliveries
    - `BasketController` - POST /api/v1/baskets/sell, /dispose-expired
    - `StockController` - GET /api/v1/stock
    - `CashRegisterController` - GET /api/v1/cash-register
  - 7 DTOs com Bean Validation
  - MapStruct Mapper para conversões
  - OpenAPI/Swagger documentation

- **Security**
  - JWT Authentication (HS512, 512 bits)
  - Role-based authorization (ADMIN, MANAGER, SALES)
  - 3 usuários padrão em ambiente dev

- **DevOps**
  - Docker Compose para infraestrutura local
  - Health checks (PostgreSQL, RabbitMQ)
  - Spring Boot Actuator para monitoramento

- **Testing**
  - 53 unit tests
  - Integration tests com Testcontainers
  - Cobertura de código

- **Documentation**
  - 4.273 linhas de documentação técnica
  - Swagger/OpenAPI spec completa
  - ADRs (Architecture Decision Records)
  - Guias de desenvolvimento e deployment

#### Validado

- ✅ Build bem-sucedido (0 erros de compilação)
- ✅ 7 cenários de teste executados com sucesso
- ✅ Todos os endpoints REST funcionais
- ✅ Eventos publicados corretamente
- ✅ Segurança JWT operacional
- ✅ Health checks respondendo

### 📋 Storefront Microservice - Planejado

- Planejamento completo documentado em `docs/STOREFRONT_MICROSERVICE_PLAN.md`
- Estimativa: 38-55 horas de desenvolvimento
- Arquitetura definida (DDD + Hexagonal Architecture)
- Comunicação síncrona (HTTP REST) e assíncrona (RabbitMQ) planejada

---

## [0.1.0] - 2025-10-13

### Adicionado

- **Configuração Inicial do Projeto**
  - Spring Boot 3.5.6
  - Java 25 (LTS)
  - Gradle 9.1.0
  - Docker e Docker Compose

- **Domain Layer Inicial**
  - Entidades básicas
  - Value Objects
  - Repository Interfaces

- **Infrastructure - Persistence**
  - PostgreSQL configuration
  - Flyway V1 migration
  - JPA Repositories

### Corrigido

- Incompatibilidade Lombok 1.18.36 com Java 25 (upgrade para 1.18.42)
- Versão do plugin OWASP (downgrade para 10.0.4)
- 422 erros de compilação resolvidos

### Organizado

- Centralização de documentação em `/docs`
- Remoção de código legado (Main.java, BasicBasket.java antigo, Box.java)
- Limpeza de diretórios não versionados (.idea, bin)

---

## Tipos de Mudanças

- **Adicionado** - Para novas funcionalidades
- **Modificado** - Para mudanças em funcionalidades existentes
- **Depreciado** - Para funcionalidades que serão removidas
- **Removido** - Para funcionalidades removidas
- **Corrigido** - Para correção de bugs
- **Segurança** - Para vulnerabilidades corrigidas

---

## Links

- [Documentação Completa](docs/README.md)
- [Guia de Docker](docs/DOCKER_SETUP.md)
- [Otimizações Docker](docs/DOCKER_OPTIMIZATION.md)
- [Status do Projeto](docs/STATUS.md)
- [Validação 15/10/2025](docs/PROJECT_VALIDATION_OCTOBER_15_2025.md)
