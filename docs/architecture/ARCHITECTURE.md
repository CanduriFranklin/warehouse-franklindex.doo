# Arquitetura do Warehouse Microservice

## Visão Geral

Este projeto implementa uma arquitetura de microsserviços baseada em **Clean Architecture** e **Domain-Driven Design (DDD)**, seguindo princípios SOLID e padrões de design modernos.

## Estrutura de Camadas

```
┌─────────────────────────────────────────────┐
│         Presentation Layer (REST)           │
│  Controllers, DTOs, Mappers, Validators    │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│        Application Layer (Use Cases)        │
│   Business Logic, Orchestration, Ports     │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│          Domain Layer (Core)                │
│  Entities, Value Objects, Domain Events    │
└─────────────────────────────────────────────┘
                    ↑
┌─────────────────────────────────────────────┐
│      Infrastructure Layer (Adapters)        │
│   JPA, RabbitMQ, External Services         │
└─────────────────────────────────────────────┘
```

## Princípios Arquiteturais

### 1. Clean Architecture (Hexagonal)
- **Independência de frameworks**: Core do negócio não depende de tecnologias
- **Testabilidade**: Lógica de negócio isolada e testável
- **Independência de UI**: Múltiplas interfaces possíveis
- **Independência de Database**: Trocar banco de dados sem afetar regras de negócio

### 2. Domain-Driven Design (DDD)
- **Ubiquitous Language**: Linguagem comum entre desenvolvedores e domínio
- **Bounded Contexts**: Delimitação clara de contextos
- **Aggregates**: BasicBasket e DeliveryBox como raízes de agregados
- **Value Objects**: Money como objeto de valor imutável
- **Domain Events**: Comunicação entre agregados via eventos

### 3. SOLID Principles
- **S**ingle Responsibility
- **O**pen/Closed
- **L**iskov Substitution
- **I**nterface Segregation
- **D**ependency Inversion

## Modelo de Domínio

### Agregados

#### BasicBasket (Cesta Básica)
Representa uma cesta básica individual no estoque.

**Invariantes:**
- Sempre tem data de validade
- Preço sempre positivo
- Status consistente com ações realizadas

**Comportamentos:**
- `isExpired()`: Verifica se está vencida
- `markAsSold()`: Marca como vendida
- `markAsDisposed()`: Descarta cesta vencida

#### DeliveryBox (Entrega)
Representa uma entrega de cestas recebida pelo armazém.

**Invariantes:**
- Quantidade sempre positiva
- Preço de venda maior que custo unitário
- Margem de lucro aplicada

**Comportamentos:**
- `calculateUnitCost()`: Calcula custo por cesta
- `calculateSellingPrice()`: Aplica margem de lucro
- `generateBaskets()`: Gera cestas individuais

### Value Objects

#### Money
Objeto de valor imutável para representar valores monetários.

**Características:**
- Imutável
- Operações matemáticas seguras
- Arredondamento automático (2 casas decimais)

## Padrões de Design Aplicados

### Repository Pattern
Abstração da camada de persistência através de interfaces.

```
Domain Interface → Infrastructure Adapter → JPA Repository
```

### Ports & Adapters (Hexagonal)
- **Input Ports**: Use cases que a aplicação expõe
- **Output Ports**: Interfaces que a aplicação necessita
- **Adapters**: Implementações concretas dos ports

### Event-Driven Architecture
- Eventos de domínio para comunicação desacoplada
- Publicação via RabbitMQ para outros microsserviços

### CQRS (Command Query Responsibility Segregation)
- Separação entre comandos (write) e consultas (read)
- Otimização de cada tipo de operação

## Decisões Técnicas

### Banco de Dados
**PostgreSQL** escolhido por:
- ACID compliance
- Suporte robusto a transações
- Performance em consultas complexas
- Extensibilidade

### Mensageria
**RabbitMQ** escolhido por:
- Confiabilidade
- Suporte a diferentes padrões de mensageria
- Dead letter queues
- Retry policies

### Observabilidade
Stack completa:
- **Actuator**: Health checks e métricas
- **Micrometer**: Abstração de métricas
- **Prometheus**: Coleta de métricas
- **Zipkin**: Distributed tracing

## Fluxos Principais

### 1. Recebimento de Entrega
```
POST /deliveries
    ↓
CreateDeliveryUseCase
    ↓
Calculate unit cost & selling price
    ↓
Generate baskets
    ↓
Save to database
    ↓
Publish DeliveryReceivedEvent
```

### 2. Venda de Cestas
```
POST /baskets/sell
    ↓
SellBasketsUseCase
    ↓
Find cheapest available baskets
    ↓
Mark as sold
    ↓
Calculate total value
    ↓
Publish BasketsSoldEvent
```

### 3. Descarte de Vencidos
```
POST /stock/dispose
    ↓
DisposeExpiredBasketsUseCase
    ↓
Find expired baskets
    ↓
Mark as disposed
    ↓
Calculate loss
    ↓
Publish BasketsDisposedEvent
```

## Segurança

### Autenticação
- JWT (JSON Web Tokens)
- Refresh tokens para sessões longas

### Autorização
- Role-based access control (RBAC)
- Roles: ADMIN, USER, WAREHOUSE_MANAGER

### Proteção
- CORS configurado
- CSRF protection
- Rate limiting
- Input validation

## Escalabilidade

### Horizontal Scaling
- Stateless application
- Session management via JWT
- Database connection pooling

### Performance
- Caching de consultas frequentes
- Índices de banco otimizados
- Lazy loading de relacionamentos
- Batch processing

## Monitoramento

### Métricas
- Request rate
- Error rate
- Response time
- Database connections
- Memory usage

### Logs
- Structured logging (JSON)
- Correlation IDs
- Log levels por ambiente

### Health Checks
- Liveness probe
- Readiness probe
- Database connectivity
- RabbitMQ connectivity

## Referências

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [12 Factor App](https://12factor.net/)
