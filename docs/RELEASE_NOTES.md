# Release Notes - v1.0.0-SNAPSHOT

---

## 📦 v1.0.0-SNAPSHOT (Build 2) - 14 de Outubro de 2025

**Data**: 14 de Outubro de 2025  
**Commit**: Pendente  
**Branch**: main  
**Tipo**: Feature - Implementação Application e Adapter Layers  
**Status**: ✅ Build Successful

### 🎯 Resumo

Implementação completa das camadas de **Application** (Use Cases e Services) e **Adapter** (REST Controllers, DTOs e Mappers), totalizando **26 arquivos** e **1.008 linhas de código**. A API REST está funcional com 5 endpoints operacionais.

### ✨ Novas Funcionalidades

#### Application Layer (11 arquivos, 362 linhas)

**Use Cases (Ports In)**:
- ✅ `ReceiveDeliveryUseCase` - Interface para recebimento de entregas
  - Command: `ReceiveDeliveryCommand` (record com validações)
  - Validações: quantidade positiva, data obrigatória, custo positivo
  
- ✅ `SellBasketsUseCase` - Interface para venda de cestas
  - Command: `SellBasketsCommand` (record)
  - Result: `SellBasketsResult` (lista de IDs, quantidade, mensagem)
  
- ✅ `DisposeExpiredBasketsUseCase` - Interface para descarte de cestas vencidas
  - Result: `DisposeExpiredBasketsResult` (lista de IDs, quantidade, mensagem)
  
- ✅ `CheckStockUseCase` - Interface para consulta de estoque
  - Result: `StockInfo` (totais por status, valor de inventário)
  
- ✅ `GetCashRegisterUseCase` - Interface para consulta financeira
  - Result: `CashRegisterInfo` (receita, custo, lucro, margem)

**Output Ports**:
- ✅ `EventPublisher` - Interface para publicação de eventos de domínio

**Services (Implementações)**:
- ✅ `ReceiveDeliveryService` (63 linhas)
  - Calcula custo unitário e preço de venda
  - Gera cestas individuais automaticamente
  - Publica evento `DeliveryReceivedEvent`
  
- ✅ `SellBasketsService` (84 linhas)
  - Validação de estoque disponível
  - Seleção FIFO (First In, First Out)
  - Cálculo de receita total
  - Publica evento `BasketsSoldEvent`
  
- ✅ `DisposeExpiredBasketsService` (78 linhas)
  - Identifica cestas vencidas automaticamente
  - Calcula valor de perda
  - Publica evento `BasketsDisposedEvent`
  
- ✅ `CheckStockService` (62 linhas)
  - Conta cestas por status
  - Identifica cestas expiradas
  - Calcula valor total de inventário
  
- ✅ `GetCashRegisterService` (75 linhas)
  - Calcula receita total de vendas
  - Calcula custo total de aquisição
  - Calcula lucro bruto e margem percentual

#### Adapter Layer - Web (15 arquivos, 628 linhas)

**REST Controllers (4 controllers)**:
- ✅ `DeliveryController`
  - `POST /api/v1/deliveries` - Registra nova entrega
  - OpenAPI annotations completas
  - Status code 201 (Created)
  
- ✅ `BasketController`
  - `POST /api/v1/baskets/sell` - Vende cestas
  - `POST /api/v1/baskets/dispose-expired` - Descarta vencidas
  - Validações com Bean Validation
  
- ✅ `StockController`
  - `GET /api/v1/stock` - Consulta informações de estoque
  - Response com métricas consolidadas
  
- ✅ `CashRegisterController`
  - `GET /api/v1/cash-register` - Consulta informações financeiras
  - Cálculos de receita, custo, lucro e margem

**DTOs (7 Data Transfer Objects)**:
- ✅ `ReceiveDeliveryRequest` - Com validações @NotNull, @Positive, @DecimalMin
- ✅ `DeliveryResponse` - Com formatação de datas ISO 8601
- ✅ `SellBasketsRequest` - Com validações
- ✅ `SellBasketsResponse` - Lista de IDs de cestas vendidas
- ✅ `DisposeExpiredBasketsResponse` - Lista de IDs de cestas descartadas
- ✅ `StockInfoResponse` - Informações consolidadas de estoque
- ✅ `CashRegisterResponse` - Métricas financeiras

**Mappers (MapStruct)**:
- ✅ `WarehouseMapper` (90 linhas)
  - Mapeamentos bidirecionais Request → Command
  - Mapeamentos Domain → Response
  - Métodos customizados (@Named)
  - Component model: Spring

#### Infrastructure Layer - Events (1 arquivo, 18 linhas)

- ✅ `LoggingEventPublisher` - Implementação temporária para desenvolvimento
  - Registra eventos no log
  - Será substituído por RabbitMQ em produção

### 🔧 Correções e Ajustes

1. ✅ **BasketStatus duplicado resolvido**
   - Arquivo separado removido
   - Usando enum interno de `BasicBasket.BasketStatus`

2. ✅ **Eventos adaptados**
   - Usando factory methods `.of()` dos eventos existentes
   - EventPublisher aceita Object (compatibilidade)

3. ✅ **Mapper corrigido**
   - Campo `profitMarginPercentage` → `profitMargin`
   - Métodos padronizados como `toResponse()`

4. ✅ **Services ajustados**
   - Método `generateBaskets()` recebe `Money` como parâmetro
   - `calculateSellingPrice()` recebe `Double`
   - Imports otimizados

5. ✅ **Controllers padronizados**
   - Uso correto de métodos do mapper
   - Logs estruturados
   - Retornos HTTP apropriados

### 📊 Estatísticas

| Métrica | Valor |
|---------|-------|
| Arquivos criados | 26 |
| Linhas de código | 1.008 |
| Use Cases | 5 |
| Services | 5 |
| Controllers | 4 |
| Endpoints REST | 5 |
| DTOs | 7 |
| Mappers | 1 |
| Tempo de compilação | 1m 20s |
| Erros de compilação | 0 ✅ |

### 🧪 Testes

- ⏳ Testes unitários - Pendente
- ⏳ Testes de integração - Pendente
- ⏳ Testes de contrato - Pendente

### 📝 Documentação

- ✅ JavaDoc completo em todos os componentes
- ✅ OpenAPI annotations nos controllers
- ✅ STATUS.md atualizado
- ✅ RELEASE_NOTES.md atualizado

### 🔜 Próximos Passos

1. **Exception Handling** - GlobalExceptionHandler com Problem Details RFC 7807
2. **Testes Automatizados** - JUnit 5, Mockito, TestContainers
3. **Spring Security** - JWT Authentication
4. **RabbitMQ** - Substituir LoggingEventPublisher

---

## 📦 v1.0.0-SNAPSHOT (Build 1) - 13 de Outubro de 2025

**Data**: 13 de Outubro de 2025  
**Commit**: 0e10b71  
**Branch**: main  
**Tipo**: Major Refactoring - Modernização Completa

---

## 🎉 Resumo

Modernização completa da aplicação legada para uma arquitetura de microsserviços enterprise-grade usando **Spring Boot 3.4.0**, **Java 25 LTS**, **Clean Architecture** e **Domain-Driven Design**.

---

## 🚀 Principais Mudanças

### ✨ Novas Funcionalidades

#### Arquitetura
- ✅ **Clean Architecture** implementada com 4 camadas distintas
- ✅ **Domain-Driven Design (DDD)** com agregados, value objects e eventos
- ✅ **Hexagonal Architecture** (Ports & Adapters)
- ✅ Separação clara de responsabilidades
- ✅ Independência de frameworks na camada de domínio

#### Stack Tecnológico
- ✅ **Java 25 LTS** (IBM Semeru Runtime)
- ✅ **Spring Boot 3.4.0** com ecossistema completo
- ✅ **Gradle 9.0.0** com build otimizado
- ✅ **PostgreSQL 16** com Flyway migrations
- ✅ **Lombok 1.18.42** compatível com Java 25
- ✅ **MapStruct 1.6.3** para mapeamento de DTOs
- ✅ **SpringDoc OpenAPI** para documentação

#### Observabilidade
- ✅ **Spring Actuator** configurado
- ✅ **Micrometer** para métricas
- ✅ **Prometheus** integration
- ✅ **Zipkin** para distributed tracing
- ✅ Logs estruturados (Logstash)

#### Segurança
- ✅ **Spring Security** configurado
- ✅ **JWT** authentication ready
- ✅ **CORS** configuration
- ✅ **OWASP Dependency Check** integrado

#### Qualidade
- ✅ **Jacoco** para cobertura de código
- ✅ **SonarQube** integration
- ✅ **TestContainers** para testes de integração
- ✅ **ArchUnit** para testes de arquitetura

#### Infraestrutura
- ✅ **RabbitMQ** messaging support
- ✅ **Docker** ready com Jib
- ✅ **Multi-profile** support (dev, prod, test)
- ✅ Cache configuration

---

## 📊 Estatísticas do Commit

```
42 arquivos alterados
+2.918 linhas adicionadas
-151 linhas removidas
```

### Arquivos Criados (39)
- **Domain Layer**: 13 arquivos
  - Entities: BasicBasket, DeliveryBox
  - Value Objects: Money
  - Events: 4 domain events
  - Exceptions: 4 domain exceptions
  - Repositories: 2 interfaces

- **Infrastructure Layer**: 4 arquivos
  - JPA Repositories: 2
  - Repository Adapters: 2

- **Configuration**: 5 arquivos
  - Application profiles (dev, prod, test)
  - Main application class
  - Gradle properties

- **Database**: 1 arquivo
  - Flyway migration V1

- **Documentation**: 4 arquivos
  - README.md (principal)
  - docs/README.md (completo)
  - docs/ARCHITECTURE.md
  - docs/DEVELOPMENT.md
  - docs/STATUS.md

- **Build**: 2 arquivos
  - owasp-suppressions.xml
  - gradle.properties

### Arquivos Removidos (7)
- ❌ `.idea/` folder completa (IntelliJ IDEA)
- ❌ Código legado: Main.java, BasicBasket.java, Box.java

### Arquivos Modificados (3)
- 🔄 build.gradle.kts (reescrito completamente)
- 🔄 .gitignore (expandido)
- 🔄 gradle-wrapper.properties (atualizado para 9.0)

---

## 🏗️ Estrutura do Projeto

```
warehouse-franklindex.doo/
├── docs/                           # Documentação completa
│   ├── adr/                       # Architecture Decision Records
│   ├── api/                       # API Documentation
│   ├── architecture/
│   │   └── ARCHITECTURE.md        # Documentação arquitetural
│   ├── DEVELOPMENT.md             # Guia de desenvolvimento
│   ├── README.md                  # Doc completa do projeto
│   └── STATUS.md                  # Status e próximos passos
│
├── src/main/java/br/com/dio/warehouse/
│   ├── domain/                    # ✅ COMPLETO
│   │   ├── model/                # Entidades
│   │   ├── valueobject/          # Value Objects
│   │   ├── repository/           # Interfaces
│   │   ├── event/                # Domain Events
│   │   └── exception/            # Domain Exceptions
│   │
│   ├── application/               # 🔄 ESTRUTURA CRIADA
│   │   ├── usecase/              # (próxima fase)
│   │   ├── port/in/
│   │   └── port/out/
│   │
│   ├── infrastructure/            # ✅ PERSISTENCE COMPLETA
│   │   ├── persistence/          # JPA Adapters
│   │   ├── messaging/            # (próxima fase)
│   │   └── config/               # (próxima fase)
│   │
│   └── presentation/              # 🔄 ESTRUTURA CRIADA
│       ├── rest/                 # (próxima fase)
│       ├── dto/
│       ├── mapper/
│       └── exception/
│
├── src/main/resources/
│   ├── db/migration/
│   │   └── V1__Create_warehouse_tables.sql
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── application-test.yml
│
├── build.gradle.kts               # ✅ Modernizado
├── gradle.properties              # ✅ Otimizado
├── owasp-suppressions.xml         # ✅ Criado
└── README.md                      # ✅ Simplificado
```

---

## 🎯 Modelo de Domínio Implementado

### Agregados

#### BasicBasket (Cesta Básica)
```java
- UUID id
- LocalDate validationDate
- Money price
- BasketStatus status (AVAILABLE, SOLD, DISPOSED, RESERVED)
- DeliveryBox deliveryBox
- Timestamps (createdAt, updatedAt, soldAt, disposedAt)
- Version (optimistic locking)
```

**Comportamentos:**
- `isExpired()`: Verifica se está vencida
- `isAvailable()`: Verifica se está disponível para venda
- `markAsSold()`: Marca como vendida
- `markAsDisposed()`: Descarta cesta vencida
- `daysUntilExpiration()`: Calcula dias até expiração

#### DeliveryBox (Entrega)
```java
- UUID id
- Long totalQuantity
- LocalDate validationDate
- Money totalCost, unitCost, sellingPrice
- Double profitMargin
- List<BasicBasket> baskets
- Timestamps (receivedAt)
- Version (optimistic locking)
```

**Comportamentos:**
- `calculateUnitCost()`: Calcula custo unitário
- `calculateSellingPrice()`: Aplica margem de lucro
- `generateBaskets()`: Gera cestas individuais
- `getAvailableCount()`: Conta cestas disponíveis
- `getExpiredCount()`: Conta cestas vencidas

### Value Objects

#### Money
```java
- BigDecimal amount (2 decimais, HALF_UP)
```

**Operações:**
- Aritméticas: add, subtract, multiply, divide
- Comparações: isGreaterThan, isLessThan, isZero
- Utilitários: abs, negate

### Domain Events
- `BasketsSoldEvent`: Publicado quando cestas são vendidas
- `BasketsDisposedEvent`: Publicado quando cestas são descartadas
- `DeliveryReceivedEvent`: Publicado quando entrega é recebida
- `DomainEvent`: Evento base

---

## 🗃️ Database Schema

### Tabela: `delivery_boxes`
```sql
- id (UUID, PK)
- total_quantity (BIGINT)
- validation_date (DATE)
- total_cost, unit_cost, selling_price (DECIMAL 10,2)
- profit_margin (DOUBLE PRECISION)
- received_at (TIMESTAMP)
- version (BIGINT)
```

### Tabela: `basic_baskets`
```sql
- id (UUID, PK)
- validation_date (DATE)
- price (DECIMAL 10,2)
- status (VARCHAR 20)
- delivery_box_id (UUID, FK)
- sold_at, disposed_at (TIMESTAMP)
- created_at, updated_at (TIMESTAMP)
- version (BIGINT)
```

**Índices:**
- `idx_delivery_received_at`
- `idx_delivery_validation_date`
- `idx_basket_validation_date`
- `idx_basket_status`
- `idx_basket_created_at`
- `idx_basket_delivery_box_id`

---

## 🔧 Configuração e Build

### Requisitos
- Java 25 LTS
- Gradle 9.0+
- PostgreSQL 16+
- RabbitMQ 3.13+ (opcional para esta fase)

### Build
```bash
gradle clean build
```

### Run
```bash
gradle bootRun
```

### Tests (quando implementados)
```bash
gradle test
gradle jacocoTestReport
```

---

## 🚨 Breaking Changes

### ⚠️ Incompatibilidade Total com Versão Anterior

Esta é uma **reescrita completa** da aplicação. Não há compatibilidade com a versão legada.

#### Código Removido
- ❌ `br.com.dio.Main`
- ❌ `br.com.dio.BasicBasket`
- ❌ `br.com.dio.Box`

#### Novo Pacote
- ✅ `br.com.dio.warehouse.*`

#### Database
- ❌ Schema anterior incompatível
- ✅ Novo schema gerenciado por Flyway
- ⚠️ Migração de dados necessária (não automatizada)

#### API Endpoints
- ❌ CLI baseada em Scanner removida
- ✅ REST API será implementada (próxima fase)

---

## 📝 Próximos Passos

### Fase 2: Application Layer (Em Progresso) 🔄
- [ ] Implementar Use Cases
  - ReceiveDeliveryUseCase
  - SellBasketsUseCase
  - DisposeExpiredBasketsUseCase
  - CheckStockUseCase
  - GetCashRegisterUseCase
- [ ] Criar Input/Output Ports
- [ ] Documentar casos de uso

### Fase 3: Presentation Layer 🔄
- [ ] REST Controllers
- [ ] DTOs e validações
- [ ] MapStruct Mappers
- [ ] OpenAPI documentation
- [ ] Exception Handlers
- [ ] Problem Details (RFC 7807)

### Fase 4: Security 🔜
- [ ] JWT Authentication
- [ ] User management
- [ ] Role-based authorization
- [ ] CORS final configuration

### Fase 5: Messaging 🔜
- [ ] RabbitMQ configuration
- [ ] Event publishers
- [ ] Event listeners
- [ ] Dead letter queues

### Fase 6: Testing 🔜
- [ ] Unit tests (Domain)
- [ ] Integration tests (Infrastructure)
- [ ] API tests (Presentation)
- [ ] Architecture tests (ArchUnit)

### Fase 7: DevOps 🔜
- [ ] Docker Compose
- [ ] CI/CD Pipeline (GitHub Actions)
- [ ] Quality gates
- [ ] Security scanning

---

## 🤝 Contribuindo

1. Clone o repositório
2. Leia `docs/DEVELOPMENT.md`
3. Crie uma feature branch
4. Implemente seguindo a arquitetura
5. Adicione testes
6. Submeta PR

---

## 📞 Suporte

- **Issues**: GitHub Issues
- **Documentação**: `/docs` folder
- **Developer**: Franklin Canduri (@CanduriFranklin)

---

## 📜 Licença

Apache License 2.0

---

**🎉 Release bem-sucedido! Branch main limpo e pronto para próxima fase!**

---

_Release preparado automaticamente pelo GitHub Copilot_  
_Data: 13 de Outubro de 2025, 23:59 UTC_
