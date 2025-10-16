# 🛒 Storefront Module - E-commerce Frontend Service

![Java](https://img.shields.io/badge/Java-25-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-orange?logo=rabbitmq)
![Architecture](https://img.shields.io/badge/Architecture-DDD%20%2B%20Hexagonal-purple)

## 📋 Visão Geral

**Storefront** é o módulo de **frente de loja** (e-commerce) da arquitetura de microserviços Warehouse. Responsável por gerenciar o catálogo de produtos, carrinhos de compras, clientes e pedidos. Implementado com **Domain-Driven Design (DDD)** e **Hexagonal Architecture (Ports & Adapters)**.

### 🎯 Funcionalidades Principais

- 📦 **Catálogo de Produtos**: Listagem, busca e categorização
- 🛒 **Carrinho de Compras**: Adicionar, remover, atualizar quantidades
- 👤 **Gestão de Clientes**: Cadastro e consultas
- 📋 **Pedidos**: Checkout, acompanhamento de status
- 🔄 **Comunicação Assíncrona**: RabbitMQ para integração com Warehouse
- 📊 **Observabilidade**: Metrics, Tracing, Health Checks

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                    STOREFRONT MODULE                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              INFRASTRUCTURE LAYER                        │  │
│  │                                                          │  │
│  │  [REST Controllers] → [DTOs] → [Mappers]               │  │
│  │  [JPA Repositories] → [RabbitMQ Publisher]             │  │
│  │  [Exception Handlers] → [Configurations]                │  │
│  └────────────────┬─────────────────────────────────────────┘  │
│                   │ Adapters (driving & driven)                │
│  ┌────────────────▼─────────────────────────────────────────┐  │
│  │              APPLICATION LAYER                          │  │
│  │                                                          │  │
│  │  [Use Cases (Ports In)] ←→ [Services]                   │  │
│  │  [Ports Out: EventPublisher, StockValidator]            │  │
│  └────────────────┬─────────────────────────────────────────┘  │
│                   │ Business Logic                              │
│  ┌────────────────▼─────────────────────────────────────────┐  │
│  │                  DOMAIN LAYER                           │  │
│  │                                                          │  │
│  │  [Entities] → [Value Objects] → [Aggregates]           │  │
│  │  [Domain Events] → [Repositories] → [Exceptions]        │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 📦 Estrutura de Pacotes

```
br.com.dio.storefront/
├── domain/                      # Camada de Domínio (72 arquivos)
│   ├── model/                   # Entidades & Aggregates
│   │   ├── Produto              # Aggregate Root: Catálogo
│   │   ├── Cliente              # Aggregate Root: Cliente
│   │   ├── CarrinhoCompras      # Aggregate Root: Carrinho
│   │   ├── ItemCarrinho         # Entity
│   │   ├── Pedido               # Aggregate Root: Pedido
│   │   └── ItemPedido           # Entity
│   ├── valueobject/             # Value Objects Imutáveis
│   │   ├── Dinheiro             # Monetary values
│   │   ├── Endereco             # Brazilian address
│   │   └── InformacaoPagamento  # Payment info
│   ├── repository/              # Repository Interfaces
│   │   ├── ProdutoRepository
│   │   ├── ClienteRepository
│   │   ├── CarrinhoComprasRepository
│   │   └── PedidoRepository
│   ├── event/                   # Domain Events
│   │   ├── PedidoCriadoEvent
│   │   ├── CarrinhoFinalizadoEvent
│   │   └── ClienteCadastradoEvent
│   └── exception/               # Domain Exceptions
│       ├── ProdutoNaoEncontradoException
│       └── EstoqueInsuficienteException
│
├── application/                 # Camada de Aplicação
│   ├── port/
│   │   ├── in/                  # Driving Ports (Use Cases)
│   │   │   ├── AdicionarProdutoAoCarrinhoUseCase
│   │   │   ├── FinalizarCarrinhoUseCase
│   │   │   └── BuscarProdutoUseCase
│   │   └── out/                 # Driven Ports
│   │       ├── PublicarEventoPort
│   │       └── ValidarEstoquePort
│   └── service/                 # Service Implementations
│       ├── CarrinhoService
│       ├── ProdutoService
│       ├── ClienteService
│       └── PedidoService
│
└── infrastructure/              # Camada de Infraestrutura
    ├── web/
    │   ├── controller/          # REST Controllers
    │   │   ├── CarrinhoController
    │   │   ├── ProdutoController
    │   │   ├── ClienteController
    │   │   └── PedidoController
    │   ├── dto/                 # DTOs (Request/Response)
    │   ├── mapper/              # Domain ↔ DTO Mappers
    │   └── exception/           # Global Exception Handler
    ├── persistence/             # JPA Repositories
    │   ├── JpaProdutoRepository
    │   └── JpaClienteRepository
    ├── messaging/               # RabbitMQ
    │   └── RabbitMQEventPublisher
    ├── service/
    │   └── EstoqueValidator
    └── config/                  # Spring Configurations
        ├── RabbitMQConfig
        └── OpenAPIConfig
```

---

## 🚀 API REST Endpoints

### Base URL: `http://localhost:8080/api/v1`

### 📦 Produtos

```http
GET    /produtos                      # Lista produtos (paginado)
GET    /produtos/{id}                 # Busca por ID
GET    /produtos/categoria/{cat}      # Filtra por categoria
GET    /produtos/buscar?termo=X       # Busca por nome
```

**Exemplo de resposta:**
```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "nome": "Notebook Dell Inspiron 15",
  "descricao": "Intel Core i7, 16GB RAM, 512GB SSD",
  "preco": { "valor": 3499.99, "moeda": "BRL" },
  "categoria": "Eletrônicos",
  "quantidadeEstoque": 50,
  "ativo": true
}
```

### 👤 Clientes

```http
POST   /clientes                      # Cadastrar cliente
GET    /clientes/{id}                 # Buscar por ID
GET    /clientes/email/{email}        # Buscar por email
GET    /clientes/cpf/{cpf}            # Buscar por CPF
```

**Exemplo de requisição (POST /clientes):**
```json
{
  "nomeCompleto": "Franklin Canduri",
  "email": "franklin@example.com",
  "cpf": "12345678901",
  "telefone": "11987654321",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apt 45",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01234567"
  }
}
```

### 🛒 Carrinho de Compras

```http
GET    /clientes/{id}/carrinho                              # Obter carrinho
POST   /clientes/{id}/carrinho/itens                        # Adicionar produto
DELETE /clientes/{id}/carrinho/itens/{produtoId}            # Remover produto
PUT    /clientes/{id}/carrinho/itens/{produtoId}/quantidade # Atualizar qtd
POST   /clientes/{id}/carrinho/finalizar                    # Checkout
```

**Exemplo de adicionar produto (POST /clientes/{id}/carrinho/itens):**
```json
{
  "produtoId": "11111111-1111-1111-1111-111111111111",
  "quantidade": 2
}
```

**Exemplo de checkout (POST /clientes/{id}/carrinho/finalizar):**
```json
{
  "enderecoEntrega": {
    "logradouro": "Rua Augusta",
    "numero": "500",
    "bairro": "Consolação",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01305000"
  },
  "informacaoPagamento": {
    "numeroCartao": "4532123456789012",
    "nomeTitular": "Franklin Canduri",
    "mesValidade": 12,
    "anoValidade": 2028,
    "cvv": "123"
  }
}
```

### 📋 Pedidos

```http
GET    /pedidos/{id}              # Buscar por ID
GET    /pedidos/numero/{numero}   # Buscar por número
GET    /pedidos/cliente/{id}      # Listar por cliente (paginado)
```

---

## 🔄 Comunicação RabbitMQ

### Exchange: `storefront.events` (Topic)

| Evento | Routing Key | Destino | Descrição |
|--------|-------------|---------|-----------|
| `PedidoCriadoEvent` | `storefront.pedido.criado` | Warehouse | Pedido criado, inicia separação |
| `CarrinhoFinalizadoEvent` | `storefront.carrinho.finalizado` | Analytics | Checkout concluído |
| `ClienteCadastradoEvent` | `storefront.cliente.cadastrado` | CRM | Novo cliente registrado |
| `ProdutoAdicionadoAoCarrinhoEvent` | `storefront.produto.adicionado.ao.carrinho` | Analytics | Item adicionado ao carrinho |

**Exemplo de evento publicado (PedidoCriadoEvent):**
```json
{
  "eventId": "e1234567-89ab-cdef-0123-456789abcdef",
  "occurredOn": "2025-10-15T14:30:00",
  "pedidoId": "p1111111-1111-1111-1111-111111111111",
  "numeroPedido": "PED-20251015-00001",
  "clienteId": "c1111111-1111-1111-1111-111111111111",
  "valorTotal": { "valor": 4349.88, "moeda": "BRL" },
  "enderecoEntrega": { ... },
  "itens": [ ... ]
}
```

---

## 💾 Banco de Dados (PostgreSQL)

### Tabelas Principais

| Tabela | Descrição | Registros Seed |
|--------|-----------|----------------|
| `produtos` | Catálogo de produtos | 10 produtos |
| `clientes` | Cadastro de clientes | 3 clientes |
| `carrinhos_compras` | Carrinhos ativos/finalizados | 2 carrinhos ativos |
| `itens_carrinho` | Itens em cada carrinho | 5 itens |
| `pedidos` | Pedidos realizados | 2 pedidos |
| `itens_pedido` | Itens de cada pedido | 6 itens |

### Migrations (Flyway)

```
backend/src/main/resources/db/migration/
├── V1__create_warehouse_tables.sql      # Warehouse module (existente)
├── V2__create_storefront_tables.sql     # Storefront tables + triggers
└── V3__seed_storefront_data.sql         # Dados de exemplo
```

---

## ⚙️ Configuração & Execução

### Pré-requisitos

- ☕ Java 25
- 🐘 PostgreSQL 16+
- 🐰 RabbitMQ 3.13+
- 🐳 Docker & Docker Compose (opcional)

### 1️⃣ Usando Docker Compose (Recomendado)

```bash
# Subir PostgreSQL + RabbitMQ
docker-compose up -d postgres rabbitmq

# Aguardar serviços iniciarem
docker-compose logs -f postgres rabbitmq
```

### 2️⃣ Build & Run

```bash
# Build do projeto
./gradlew clean build

# Executar aplicação
./gradlew bootRun

# Ou executar o JAR
java -jar backend/build/libs/backend.jar
```

### 3️⃣ Acessar Aplicação

- **API REST**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api-docs
- **Actuator**: http://localhost:8080/actuator
- **Health Check**: http://localhost:8080/actuator/health

### 4️⃣ RabbitMQ Management

```bash
# Acessar UI do RabbitMQ
http://localhost:15672
# Usuário: guest
# Senha: guest
```

---

## 🧪 Testes

```bash
# Executar todos os testes
./gradlew test

# Relatório de cobertura (JaCoCo)
./gradlew jacocoTestReport
# Relatório: backend/build/reports/jacoco/test/html/index.html

# Testes de arquitetura (ArchUnit)
./gradlew test --tests "*ArchitectureTest"

# Testes de integração (Testcontainers)
./gradlew integrationTest
```

---

## 📊 Métricas & Observabilidade

### Prometheus Metrics

```http
GET /actuator/prometheus
```

**Métricas customizadas:**
- `storefront_pedidos_criados_total` - Total de pedidos criados
- `storefront_carrinho_itens_adicionados_total` - Itens adicionados ao carrinho
- `storefront_clientes_cadastrados_total` - Novos clientes

### Health Checks

```http
GET /actuator/health
```

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "rabbitMQ": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

---

## 📈 Estatísticas do Projeto

### Módulo Storefront

| Métrica | Valor |
|---------|-------|
| **Arquivos Java** | 72 arquivos |
| **Linhas de Código** | 5,225 linhas |
| **Migrations SQL** | 3 arquivos |
| **Controllers REST** | 4 controllers |
| **Use Cases** | 10 interfaces |
| **Services** | 4 implementações |
| **Domain Entities** | 6 entidades |
| **Value Objects** | 3 objetos |
| **Domain Events** | 8 eventos |
| **DTOs** | 13 classes |
| **Endpoints REST** | 20+ endpoints |

### Projeto Completo

| Métrica | Valor |
|---------|-------|
| **Total Java Files** | ~100 arquivos |
| **Total Lines of Code** | ~9,452 linhas |
| **Modules** | 2 (Warehouse + Storefront) |

---

## 🛡️ Segurança

- ✅ Bean Validation em todos DTOs
- ✅ CPF validation com algoritmo de Luhn
- ✅ Card number validation
- ✅ SQL Injection protection (JPA/Hibernate)
- ✅ Exception handling com RFC 7807 Problem Details
- ⏳ JWT Authentication (TODO)
- ⏳ Rate Limiting (TODO)

---

## 🔮 Próximos Passos

- [ ] Implementar autenticação JWT
- [ ] Rate limiting nos endpoints públicos
- [ ] Cache distribuído (Redis)
- [ ] Frontend React + TypeScript
- [ ] CI/CD pipeline
- [ ] Deploy Kubernetes

---

## 📚 Referências

- **Domain-Driven Design**: Eric Evans
- **Hexagonal Architecture**: Alistair Cockburn
- **Clean Architecture**: Robert C. Martin
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **RabbitMQ Tutorials**: https://www.rabbitmq.com/tutorials

---

## 👨‍💻 Autor

**Franklin Canduri**
- GitHub: [@CanduriFranklin](https://github.com/CanduriFranklin)
- Email: franklin@example.com

---

## 📄 Licença

Este projeto é parte do desafio DIO - Digital Innovation One.

---

**⭐ Se este projeto foi útil, considere dar uma estrela no GitHub!**
