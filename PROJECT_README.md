# 🏪 Warehouse & Storefront - E-Commerce Platform

Plataforma completa de e-commerce desenvolvida com **Domain-Driven Design (DDD)** e **Hexagonal Architecture**.

## 📋 Visão Geral

Este projeto consiste em um sistema de e-commerce moderno dividido em dois módulos principais:

- **Warehouse**: Gerenciamento de estoque e entregas
- **Storefront**: Loja virtual com catálogo, carrinho e pedidos

## 🏗️ Arquitetura

### Backend (Java/Spring Boot)
- ✅ **Domain-Driven Design (DDD)**
  - Aggregates, Value Objects, Entities, Domain Events
  - Repository Pattern
  - Domain Services
  
- ✅ **Hexagonal Architecture (Ports & Adapters)**
  - Ports In: Use Cases (interfaces)
  - Ports Out: PublicarEventoPort, ValidarEstoquePort
  - Adapters: REST Controllers, RabbitMQ, JPA Repositories

- ✅ **Event-Driven Architecture**
  - RabbitMQ para comunicação assíncrona
  - Domain Events (PedidoCriadoEvent, CarrinhoFinalizadoEvent, etc.)
  - TopicExchange com routing keys

### Frontend (React/TypeScript)
- ✅ **React 18** com hooks modernos
- ✅ **TypeScript** para type-safety
- ✅ **Context API** para gerenciamento de estado
- ✅ **React Router** para navegação
- ✅ **TailwindCSS 4.x** para estilização

## 🛠️ Stack Tecnológica

### Backend
- **Java 25** (preview features)
- **Spring Boot 3.5.6**
- **PostgreSQL 16+** (database)
- **RabbitMQ 3.13+** (message broker)
- **Flyway** (database migrations)
- **MapStruct** (DTO mapping)
- **Lombok** (boilerplate reduction)
- **OpenAPI/Swagger** (API documentation)
- **Gradle 8.x** (build tool)

### Frontend
- **React 18**
- **TypeScript 5.x**
- **Vite 7.x**
- **TailwindCSS 4.x**
- **React Router 6.x**
- **Axios** (HTTP client)
- **React Icons**

## 📁 Estrutura do Projeto

```
warehouse-franklindex.doo/
├── backend/
│   ├── src/main/java/br/com/dio/
│   │   ├── warehouse/           # Módulo Warehouse
│   │   └── storefront/          # Módulo Storefront ⭐
│   │       ├── domain/          # Camada de Domínio (29 arquivos)
│   │       │   ├── valueobject/ # Dinheiro, Endereco, InformacaoPagamento
│   │       │   ├── entity/      # Produto, Cliente, CarrinhoCompras, Pedido
│   │       │   ├── repository/  # Interfaces dos repositórios
│   │       │   ├── event/       # Domain Events
│   │       │   └── exception/   # Domain Exceptions
│   │       ├── application/     # Camada de Aplicação (16 arquivos)
│   │       │   ├── port/in/     # Use Cases (10 interfaces)
│   │       │   ├── port/out/    # Ports de saída (2 interfaces)
│   │       │   └── service/     # Implementação dos Use Cases
│   │       └── infrastructure/  # Camada de Infraestrutura (27 arquivos)
│   │           ├── controller/  # REST Controllers
│   │           ├── dto/         # Data Transfer Objects
│   │           ├── mapper/      # StorefrontMapper (MapStruct)
│   │           ├── messaging/   # RabbitMQEventPublisher
│   │           ├── persistence/ # JPA Repositories
│   │           ├── service/     # EstoqueValidator
│   │           └── config/      # RabbitMQ, OpenAPI configs
│   ├── src/main/resources/
│   │   ├── db/migration/
│   │   │   ├── V2__create_storefront_tables.sql
│   │   │   └── V3__seed_storefront_data.sql
│   │   ├── application.yml
│   │   └── application-storefront.yml
│   ├── build.gradle.kts
│   └── gradlew
├── frontend/                    # Frontend React ⭐
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/         # Button, Card, Loading, Input
│   │   │   └── layout/         # Header, Footer, Layout
│   │   ├── pages/              # HomePage, ProdutosPage, CarrinhoPage
│   │   ├── services/           # API services
│   │   ├── context/            # CarrinhoContext
│   │   ├── types/              # TypeScript interfaces
│   │   ├── utils/              # formatters
│   │   ├── App.tsx             # Rotas e setup
│   │   └── main.tsx            # Entry point
│   ├── package.json
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   └── .env
├── docker-compose.yml
└── README.md
```

## 📊 Estatísticas do Projeto

### Backend - Storefront Module
- **72 arquivos Java** (~5.225 linhas)
- **Domain Layer**: 29 arquivos (Value Objects, Entities, Events, Exceptions)
- **Application Layer**: 16 arquivos (Use Cases, Services)
- **Infrastructure Layer**: 27 arquivos (Controllers, DTOs, Repositories, Messaging)
- **Database**: 6 tabelas + migrations
- **API REST**: 20+ endpoints

### Frontend
- **15+ componentes React**
- **3 páginas principais**
- **4 serviços de API**
- **1 Context para estado global**
- **Bundle**: 288 KB JS (93 KB gzipped) + 17 KB CSS (4 KB gzipped)

## 🚀 Como Executar

### Pré-requisitos
- Java 25 (ou Java 21+)
- Node.js 18+ e npm
- Docker e Docker Compose (para PostgreSQL e RabbitMQ)

### 1. Iniciar Dependências (PostgreSQL + RabbitMQ)

```bash
docker compose up -d postgres rabbitmq
```

### 2. Backend

```bash
cd backend
./gradlew bootRun
```

**Backend estará disponível em:**
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html
- RabbitMQ Management: http://localhost:15672 (guest/guest)

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

**Frontend estará disponível em:**
- App: http://localhost:5173

## 📡 API Endpoints (Storefront)

### Produtos
```
GET  /api/produtos                    # Lista produtos (paginado)
GET  /api/produtos/{id}               # Detalhes do produto
GET  /api/produtos/categoria          # Produtos por categoria
GET  /api/produtos/buscar             # Buscar por nome
```

### Clientes
```
POST /api/clientes                    # Cadastrar cliente
GET  /api/clientes/{id}               # Buscar por ID
GET  /api/clientes/email              # Buscar por email
GET  /api/clientes/cpf                # Buscar por CPF
```

### Carrinho
```
GET    /api/carrinho                  # Obter carrinho do cliente
POST   /api/carrinho/adicionar        # Adicionar produto
PUT    /api/carrinho/atualizar        # Atualizar quantidade
DELETE /api/carrinho/remover          # Remover produto
POST   /api/carrinho/finalizar        # Finalizar (criar pedido)
```

### Pedidos
```
GET /api/pedidos/{id}                 # Buscar pedido por ID
GET /api/pedidos/numero               # Buscar por número
GET /api/pedidos/cliente              # Pedidos do cliente (paginado)
```

## 🎯 Funcionalidades Implementadas

### Backend (Storefront)
- ✅ Catálogo de produtos com busca e filtros
- ✅ Carrinho de compras persistente
- ✅ Gestão de clientes com validação de CPF
- ✅ Criação de pedidos com estado de máquina
- ✅ Eventos de domínio publicados no RabbitMQ
- ✅ Validação de estoque
- ✅ Cálculo de preços com Value Object Dinheiro
- ✅ Endereços com validação de CEP
- ✅ Múltiplas formas de pagamento
- ✅ Migrations com seed data
- ✅ Documentação OpenAPI/Swagger

### Frontend
- ✅ Página inicial com produtos em destaque
- ✅ Lista de produtos com busca e paginação
- ✅ Carrinho de compras funcional
- ✅ Adicionar/remover produtos do carrinho
- ✅ Atualizar quantidades
- ✅ Layout responsivo
- ✅ Context API para estado global
- ✅ Integração completa com backend REST

## 🗄️ Modelo de Dados (Storefront)

### Tabelas Principais
- **produtos**: Catálogo de produtos
- **clientes**: Cadastro de clientes
- **carrinhos_compras**: Carrinhos ativos/finalizados
- **itens_carrinho**: Itens no carrinho
- **pedidos**: Pedidos finalizados
- **itens_pedido**: Itens do pedido

### Value Objects
- **Dinheiro**: valor (BigDecimal)
- **Endereco**: rua, número, complemento, bairro, cidade, estado, CEP
- **InformacaoPagamento**: tipo, dados do cartão/PIX/boleto

## 📦 Build do Projeto

### Backend
```bash
cd backend
./gradlew clean build -x test
# Build SUCCESS em ~20-25s
# Artefato: build/libs/warehouse-backend-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm run build
# Build SUCCESS em ~10s
# Artefatos em dist/:
#  - index.html (0.46 KB)
#  - CSS (17.15 KB → 4.18 KB gzipped)
#  - JS (288.35 KB → 93.43 KB gzipped)
```

## 🧪 Testes

### Backend
```bash
cd backend
./gradlew test                # Unit tests
./gradlew integrationTest     # Integration tests
```

### Frontend
```bash
cd frontend
npm run test                  # Unit tests (se configurado)
```

## 📚 Documentação Adicional

- **Backend**: Ver `backend/STOREFRONT_README.md`
- **Frontend**: Ver `frontend/FRONTEND_README.md`
- **API**: http://localhost:8080/swagger-ui.html (após iniciar backend)

## 🔮 Próximas Funcionalidades

### Backend
- [ ] Autenticação e autorização (JWT)
- [ ] Integração de pagamento real
- [ ] Notificações por email
- [ ] Sistema de avaliações
- [ ] Gestão de promoções

### Frontend
- [ ] Página de detalhes do produto
- [ ] Checkout completo com formulários
- [ ] Página de pedidos do cliente
- [ ] Autenticação de usuário
- [ ] Filtros avançados
- [ ] Wishlist

## 👨‍💻 Desenvolvedor

**Franklin Canduri**
- GitHub: [@CanduriFranklin](https://github.com/CanduriFranklin)
- Email: canduri.franklin@example.com

## 📄 Licença

Este projeto é disponibilizado para fins educacionais e de portfólio.

---

⭐ **Projeto desenvolvido aplicando DDD, Hexagonal Architecture e Event-Driven Architecture** ⭐
