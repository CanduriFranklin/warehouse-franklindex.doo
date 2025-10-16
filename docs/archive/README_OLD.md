# Warehouse Microservice

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-9.0-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 📋 Visão Geral

Sistema de microsserviços para gerenciamento de armazém e estoque de cestas básicas em plataforma de e-commerce. Implementado seguindo princípios de **Clean Architecture**, **Domain-Driven Design (DDD)** e práticas **DevSecOps**.

## 🏗️ Arquitetura

### Clean Architecture em Camadas

```
warehouse/
├── domain/              # Camada de Domínio (Regras de Negócio)
│   ├── model/          # Entidades e Agregados
│   ├── valueobject/    # Value Objects
│   ├── repository/     # Interfaces de Repositório
│   ├── event/          # Eventos de Domínio
│   └── exception/      # Exceções de Domínio
│
├── application/         # Camada de Aplicação (Casos de Uso)
│   ├── usecase/        # Casos de Uso
│   ├── port/in/        # Portas de Entrada
│   └── port/out/       # Portas de Saída
│
├── infrastructure/      # Camada de Infraestrutura (Detalhes Técnicos)
│   ├── persistence/    # Adaptadores JPA
│   ├── messaging/      # RabbitMQ
│   └── config/         # Configurações
│
└── presentation/        # Camada de Apresentação (Interface)
    ├── rest/           # Controllers REST
    ├── dto/            # DTOs
    ├── mapper/         # MapStruct Mappers
    └── exception/      # Exception Handlers
```

### Princípios Aplicados

- ✅ **SOLID Principles**
- ✅ **Clean Architecture** (Hexagonal Architecture / Ports & Adapters)
- ✅ **Domain-Driven Design (DDD)**
- ✅ **CQRS Pattern** (Command Query Responsibility Segregation)
- ✅ **Event-Driven Architecture**
- ✅ **Repository Pattern**
- ✅ **Dependency Inversion**

## 🚀 Tecnologias

### Core
- **Java 25 LTS** (IBM Semeru Runtime)
- **Spring Boot 3.4.0**
- **Gradle 9.0.0**

### Frameworks & Libraries
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação JWT
- **Spring AMQP** - Mensageria com RabbitMQ
- **Spring Actuator** - Observabilidade e health checks
- **Flyway** - Migrations de banco de dados
- **PostgreSQL** - Banco de dados relacional
- **Lombok** - Redução de boilerplate
- **MapStruct** - Mapeamento de DTOs
- **SpringDoc OpenAPI** - Documentação de API

### Observabilidade
- **Micrometer** - Métricas
- **Prometheus** - Monitoring
- **Zipkin** - Distributed tracing
- **Logstash** - Logs estruturados

### Qualidade & Segurança
- **JUnit 5** - Testes unitários
- **TestContainers** - Testes de integração
- **Mockito** - Mocks
- **ArchUnit** - Testes de arquitetura
- **Jacoco** - Cobertura de código
- **SonarQube** - Análise de código
- **OWASP Dependency Check** - Análise de vulnerabilidades
- **Jib** - Containerização

## 📊 Modelo de Domínio

### Agregados

#### BasicBasket (Cesta Básica)
```java
- UUID id
- LocalDate validationDate
- Money price
- BasketStatus status (AVAILABLE, SOLD, DISPOSED, RESERVED)
- DeliveryBox deliveryBox
- Timestamps (createdAt, updatedAt, soldAt, disposedAt)
```

**Regras de Negócio:**
- Cesta não pode ser vendida se estiver vencida
- Cesta vencida deve ser descartada
- Preço sempre em formato monetário com 2 casas decimais

#### DeliveryBox (Entrega)
```java
- UUID id
- Long totalQuantity
- LocalDate validationDate
- Money totalCost, unitCost, sellingPrice
- Double profitMargin
- List<BasicBasket> baskets
```

**Regras de Negócio:**
- Preço de venda deve incluir margem de lucro configurável
- Gera cestas individuais automaticamente ao receber entrega
- Calcula custo unitário baseado no total

### Value Objects

#### Money
Representação imutável de valores monetários com operações aritméticas seguras:
- Arredondamento automático (HALF_UP)
- Operações: add, subtract, multiply, divide
- Comparações: isGreaterThan, isLessThan, isZero

## 🔧 Configuração

### Pré-requisitos
- Java 25 ou superior
- Gradle 9.0 ou superior
- PostgreSQL 16+
- RabbitMQ 3.13+
- Docker & Docker Compose (opcional)

### Variáveis de Ambiente

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=warehouse_db
DB_USERNAME=warehouse_user
DB_PASSWORD=warehouse_pass

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# Security
JWT_SECRET=your-256-bit-secret-key
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Application
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

### Build & Run

```bash
# Build
gradle build

# Run
gradle bootRun

# Build Docker Image
gradle jibDockerBuild

# Run Tests
gradle test

# Coverage Report
gradle jacocoTestReport

# Security Check
gradle dependencyCheckAnalyze
```

## 📡 API Endpoints

### Gestão de Estoque

```
GET    /api/v1/stock              - Listar estoque
GET    /api/v1/stock/summary      - Resumo do estoque
GET    /api/v1/stock/expired      - Cestas vencidas
POST   /api/v1/stock/dispose      - Descartar vencidas

GET    /api/v1/baskets            - Listar cestas
GET    /api/v1/baskets/{id}       - Buscar cesta
POST   /api/v1/baskets/sell       - Vender cestas

POST   /api/v1/deliveries         - Receber entrega
GET    /api/v1/deliveries         - Listar entregas
GET    /api/v1/deliveries/{id}    - Buscar entrega
```

### Documentação
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/v3/api-docs`

### Health & Metrics
- **Health**: `http://localhost:8080/api/actuator/health`
- **Metrics**: `http://localhost:8080/api/actuator/prometheus`

## 🔐 Segurança

- **Autenticação**: JWT (JSON Web Tokens)
- **Autorização**: Role-based (ADMIN, USER, WAREHOUSE_MANAGER)
- **CORS**: Configurável por ambiente
- **HTTPS**: Obrigatório em produção
- **OWASP**: Dependency scanning automatizado

## 📈 Observabilidade

### Métricas
- Tempo de resposta de endpoints
- Taxa de erro
- Throughput
- Uso de memória e CPU
- Pool de conexões do banco

### Logs Estruturados
```json
{
  "timestamp": "2025-10-13T10:30:45.123Z",
  "level": "INFO",
  "service": "warehouse-service",
  "trace_id": "abc123",
  "span_id": "xyz789",
  "message": "Basket sold successfully"
}
```

### Distributed Tracing
- Integração com Zipkin
- Rastreamento de requisições entre microsserviços
- Análise de latência

## 🧪 Testes

### Pirâmide de Testes
- **Unitários**: Lógica de domínio, casos de uso
- **Integração**: Repositórios, mensageria, API
- **E2E**: Fluxos completos
- **Arquitetura**: ArchUnit para validar camadas

### Cobertura de Código
- Meta: **>80%** de cobertura
- Relatórios: `build/reports/jacoco/test/html/index.html`

## 🐳 Docker

### Dockerfile Multi-Stage
```dockerfile
FROM eclipse-temurin:25-jre-alpine
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "/app/warehouse.jar"]
```

### Docker Compose
```bash
docker-compose up -d
```

Serviços:
- **warehouse-service**: Aplicação principal (porta 8080)
- **postgres**: Banco de dados (porta 5432)
- **rabbitmq**: Message broker (porta 5672, 15672)
- **prometheus**: Monitoring (porta 9090)
- **zipkin**: Tracing (porta 9411)

## 📚 Documentação Adicional

- [Architecture Decision Records (ADR)](docs/adr/)
- [API Documentation](docs/api/)
- [Development Guide](docs/development.md)
- [Deployment Guide](docs/deployment.md)
- [Contributing Guide](CONTRIBUTING.md)

## 🤝 Contribuindo

1. Fork o projeto
2. Crie sua feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está licenciado sob a Apache License 2.0 - veja o arquivo [LICENSE](LICENSE) para detalhes.

## ✨ Autor

**Franklin Canduri**
- GitHub: [@CanduriFranklin](https://github.com/CanduriFranklin)
- LinkedIn: [Franklin Canduri](https://linkedin.com/in/franklin-canduri)

## 🎯 Roadmap

- [x] Setup inicial do projeto
- [x] Configuração Spring Boot 3.4
- [x] Modelagem DDD
- [x] Camada de domínio
- [x] Persistência com JPA
- [ ] Casos de uso (Application Layer)
- [ ] API REST controllers
- [ ] Autenticação e autorização
- [ ] Mensageria RabbitMQ
- [ ] Testes automatizados
- [ ] Docker e Docker Compose
- [ ] CI/CD Pipeline
- [ ] Microsserviço Loja (Store)
- [ ] Documentação completa

---

⭐ **Star** este projeto se ele foi útil para você!
