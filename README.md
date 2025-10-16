# Criando um Microsserviço de Controle de Comércio Eletrônico 🛒

[![Java](https://img.shields.io/badge/Java-25_LTS-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![Gradle](https://img.shields.io/badge/Gradle-9.1.0-blue.svg)](https://gradle.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-FF6600.svg)](https://www.rabbitmq.com/)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-24.04_LTS-E95420.svg?logo=ubuntu&logoColor=white)](https://ubuntu.com/)
[![WSL2](https://img.shields.io/badge/WSL-2-0078D4.svg?logo=windows&logoColor=white)](https://docs.microsoft.com/windows/wsl/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Warehouse Status](https://img.shields.io/badge/Warehouse-Production_Ready-success.svg)](docs/PROJECT_VALIDATION_OCTOBER_15_2025.md)
[![Storefront Status](https://img.shields.io/badge/Storefront-Planned-yellow.svg)](docs/STOREFRONT_MICROSERVICE_PLAN.md)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Franklin_Canduri-0077B5.svg?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/franklin-david-canduri-presilla-b75956266/)

---

## 📋 Sobre o Projeto

> **⚠️ IMPORTANTE**: Este é um projeto de **DOIS MICROSSERVIÇOS** que se comunicam de forma **síncrona (HTTP/REST)** e **assíncrona (RabbitMQ)**.

**Plataforma de Comércio Eletrônico de Cestas Básicas** composta por dois microsserviços principais:

### 🏭 **1. Warehouse Microservice** ✅ (COMPLETO - Production Ready)
Responsável pelo gerenciamento de armazém e controle de estoque:
- Recebimento de entregas de fornecedores
- Controle de inventário em tempo real
- Venda de cestas básicas
- Descarte de produtos vencidos
- Publicação de eventos de estoque via RabbitMQ
- API REST para consultas e operações

**Status**: 🟢 **IMPLEMENTADO E VALIDADO** (15/10/2025)

### 🏪 **2. Storefront Microservice** 📋 (PLANEJADO)
Responsável pela interface de vendas e experiência do cliente:
- Catálogo de produtos (vitrine)
- Carrinho de compras
- Checkout e criação de pedidos
- Consulta de disponibilidade no Warehouse (HTTP síncrono)
- Consumo de eventos de estoque (RabbitMQ assíncrono)
- Gestão de clientes

**Status**: 🟡 **PLANEJADO** - [Ver Plano Completo](docs/STOREFRONT_MICROSERVICE_PLAN.md)

---

## 🏗️ Arquitetura do Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE (Frontend)                        │
│                    Angular/React/Vue                         │
└────────────────┬────────────────────────────────────────────┘
                 │ HTTP/REST
                 ▼
┌─────────────────────────────────────────────────────────────┐
│              🏪 STOREFRONT MICROSERVICE                      │
│                    (Porta 8081)                              │
│    Catálogo • Carrinho • Pedidos • Clientes                 │
└────────────┬────────────────────┬───────────────────────────┘
             │                    │
             │ HTTP/REST          │ RabbitMQ Events
             │ (Síncrono)         │ (Assíncrono)
             ▼                    ▼
┌─────────────────────────────────────────────────────────────┐
│              🏭 WAREHOUSE MICROSERVICE                       │
│                    (Porta 8080)                              │
│    Estoque • Entregas • Vendas • Descartes                  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   💾 INFRAESTRUTURA                          │
│  PostgreSQL (2 DBs) • RabbitMQ • Docker Compose             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 Desafio do Projeto

Construir **dois microsserviços principais** que se comunicam através de:

| Tipo | Tecnologia | Uso |
|------|-----------|-----|
| **Comunicação Síncrona** | HTTP/REST | Storefront consulta disponibilidade no Warehouse |
| **Comunicação Assíncrona** | RabbitMQ | Eventos de estoque, pedidos, e atualizações |

**Características principais:**
- ✅ Cada microsserviço tem seu próprio banco de dados (Database per Service)
- ✅ Arquitetura Hexagonal (Ports & Adapters) em ambos
- ✅ Domain-Driven Design (DDD) aplicado
- ✅ Event-Driven Architecture com RabbitMQ
- ✅ Segurança JWT compartilhada
- ✅ Containerização completa com Docker

---

## 📊 Status de Implementação

| Microsserviço | Status | Progresso | Documentação |
|---------------|--------|-----------|--------------|
| **Warehouse** | 🟢 Production Ready | 100% | [Validação](docs/PROJECT_VALIDATION_OCTOBER_15_2025.md) |
| **Storefront** | 🟡 Planejado | 0% | [Plano](docs/STOREFRONT_MICROSERVICE_PLAN.md) |

### ✅ Warehouse - O Que Foi Implementado

- ✅ **Domain Layer**: Agregados (DeliveryBox, BasicBasket), Value Objects, Events
- ✅ **Application Layer**: 5 Use Cases completos (Receive, Sell, Dispose, Check Stock, Cash Register)
- ✅ **Infrastructure Layer**: PostgreSQL, Flyway migrations, RabbitMQ publishers/consumers
- ✅ **Adapter Layer**: 5 REST Controllers, 14 endpoints, DTOs, Mappers
- ✅ **Security**: JWT Authentication, Role-based authorization (ADMIN, MANAGER, SALES)
- ✅ **DevOps**: Docker, Docker Compose, Health checks, Actuator
- ✅ **Documentation**: Swagger/OpenAPI, 4.273 linhas de documentação técnica
- ✅ **Tests**: 53 unit tests, integration tests com Testcontainers

### 📋 Storefront - O Que Será Implementado

- 📋 **Domain Layer**: Product, ShoppingCart, Order, Customer
- 📋 **Application Layer**: Cart, Checkout, Orders, Product Catalog
- 📋 **HTTP Client**: RestTemplate/OpenFeign para comunicação com Warehouse
- 📋 **RabbitMQ Consumers**: Eventos de estoque do Warehouse
- 📋 **API REST**: 15+ endpoints (produtos, carrinho, pedidos)
- 📋 **Database**: PostgreSQL separado (storefront_db)

**Estimativa**: 38-55 horas (~1-2 semanas de desenvolvimento)

---

## 🐳 Otimizações Docker (16/10/2025)

> **✨ NOVIDADE**: Ambiente Docker completamente otimizado para **desenvolvimento** e **produção** com consumo de recursos reduzido!

### 📊 Consumo de Recursos

| Serviço | CPU Máx | RAM Máx | Modo | Profile |
|---------|---------|---------|------|---------|
| **PostgreSQL** | 1.0 | 512M | Sempre | - |
| **RabbitMQ** | 1.0 | 512M | Sempre | - |
| **pgAdmin** | 0.5 | 256M | Opcional | `tools` |
| **Frontend Dev** | 1.0 | 512M | Dev | `dev` |
| **Frontend Prod** | 0.5 | 128M | Prod | `prod` |
| **Backend** | 2.0 | 1024M | Opcional | - |
| **📊 Total (Dev)** | **3.5** | **1792M** | - | - |
| **📊 Total (Prod)** | **2.5** | **1152M** | - | - |

### 🎯 Profiles de Execução

O Docker Compose agora suporta **profiles** para executar apenas os serviços necessários:

```bash
# Somente infraestrutura (PostgreSQL + RabbitMQ)
docker compose up -d

# Infraestrutura + Frontend em modo desenvolvimento (hot reload)
docker compose --profile dev up frontend-dev

# Infraestrutura + Frontend otimizado para produção (Nginx)
docker compose --profile prod up --build frontend-prod

# Infraestrutura + pgAdmin para gerenciar banco de dados
docker compose --profile tools up pgadmin

# Todos os perfis juntos
docker compose --profile dev --profile tools up
```

### 🚀 Frontend Otimizado

#### **Modo Desenvolvimento** (`frontend-dev`)
- Hot reload automático com Vite
- Mapeamento de volume para desenvolvimento local
- Porta: **5173**
- Comando: `docker compose --profile dev up frontend-dev`

#### **Modo Produção** (`frontend-prod`)
- **Multi-stage build** (node:20-alpine + nginx:1.27-alpine)
- Build otimizado com `npm ci --only=production`
- Servidor Nginx de alta performance
- Gzip compression habilitado
- Security headers configurados
- Cache de assets estáticos (1 ano)
- Healthcheck integrado
- Imagem final: **~20MB** (vs ~500MB sem otimização)
- Porta: **80**

**Dockerfile.prod features:**
```dockerfile
# Stage 1: Build da aplicação React
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production --ignore-scripts
COPY . .
ARG VITE_API_URL=http://localhost:8080/api
ENV VITE_API_URL=${VITE_API_URL}
RUN npm run build

# Stage 2: Servir com Nginx otimizado
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
HEALTHCHECK CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 📁 Arquivos de Configuração

#### **nginx.conf** (Frontend)
```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Cache static assets (1 year)
    location ~* \.(js|css|png|jpg|jpeg|gif|svg|ico|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # React Router - SPA support
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache";
    }

    # Health check endpoint
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

#### **.dockerignore** (Frontend)
```
node_modules/
dist/
.env*
coverage/
.vscode/
README.md
*.log
```

### 💡 Benefícios das Otimizações

| Aspecto | Antes | Depois | Melhoria |
|---------|-------|--------|----------|
| **Imagem Frontend** | ~500MB | ~20MB | **96% menor** |
| **Tempo de Build** | 3-5 min | 1-2 min | **50-60% mais rápido** |
| **RAM Frontend Prod** | Sem limite | 128MB | **Uso controlado** |
| **Startup Time** | 15-20s | 5-8s | **60% mais rápido** |
| **Segurança** | Básica | Headers + Gzip | **Reforçada** |

### 📚 Documentação Completa

**📖 Índice Geral**: [docs/00_INDEX.md](docs/00_INDEX.md) - Navegação completa de toda a documentação

**Documentação Docker**:
- [docs/02_DOCKER_SETUP.md](docs/02_DOCKER_SETUP.md) - Guia completo de configuração Docker
- [docs/04_DOCKER_OPTIMIZATION.md](docs/04_DOCKER_OPTIMIZATION.md) - Detalhes técnicos das otimizações
- [docs/06_DOCKER_CLEANUP.md](docs/06_DOCKER_CLEANUP.md) - Guia de limpeza de imagens Docker/Kubernetes

**Documentação Frontend**:
- [frontend/FRONTEND_README.md](frontend/FRONTEND_README.md) - Documentação completa do frontend (React + Docker)

---

## � Ambiente de Desenvolvimento

### Sistema Operacional

Este projeto foi desenvolvido e testado em **Ubuntu 24.04 LTS** rodando sobre **WSL2 (Windows Subsystem for Linux 2)**.

#### **Distribuições Suportadas**

| Distribuição | Versão | Status | WSL2 |
|--------------|--------|--------|------|
| **Ubuntu** | 24.04 LTS (Noble Numbat) | ✅ Recomendado | Sim |
| **Ubuntu** | 22.04 LTS (Jammy Jellyfish) | ✅ Suportado | Sim |
| **Ubuntu** | 20.04 LTS (Focal Fossa) | ✅ Suportado | Sim |
| **Debian** | 12 (Bookworm) | ✅ Suportado | Sim |
| **Debian** | 11 (Bullseye) | ✅ Suportado | Sim |
| **Fedora** | 39+ | ⚠️ Compatível* | Sim |
| **openSUSE** | Leap 15.5 | ⚠️ Compatível* | Sim |

*_Compatível com ajustes mínimos nos comandos de instalação de pacotes._

#### **Requisitos do Sistema**

**Windows (para WSL2)**:
- Windows 10 versão 2004+ (Build 19041+) ou Windows 11
- WSL2 habilitado
- 8 GB RAM (recomendado 16 GB)
- 50 GB de espaço livre em disco

**Linux Nativo**:
- Kernel 5.4+
- 8 GB RAM (recomendado 16 GB)
- 50 GB de espaço livre em disco

#### **Verificar Versão do Sistema**

```bash
# Ubuntu/Debian
lsb_release -a

# Versão do kernel
uname -r

# Verificar se está no WSL2
uname -a | grep -i microsoft
```

#### **Configuração WSL2**

Se estiver usando Windows, siga estas etapas:

```powershell
# 1. Habilitar WSL2 (PowerShell como Administrador)
wsl --install

# 2. Definir WSL2 como padrão
wsl --set-default-version 2

# 3. Instalar Ubuntu 24.04
wsl --install -d Ubuntu-24.04

# 4. Verificar versão do WSL
wsl --list --verbose
```

#### **Pacotes Necessários (Ubuntu/Debian)**

```bash
# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar dependências essenciais
sudo apt install -y \
  build-essential \
  curl \
  wget \
  git \
  ca-certificates \
  gnupg \
  lsb-release

# Docker (integrado via Docker Desktop no Windows + WSL2)
# Ou instalar Docker Engine nativo no Linux:
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
```

#### **Integração Docker Desktop + WSL2**

Para usar Docker Desktop com WSL2:

1. Instale [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
2. Vá em **Settings** → **Resources** → **WSL Integration**
3. Habilite integração para Ubuntu-24.04
4. Reinicie o WSL: `wsl --shutdown` (no PowerShell)

#### **Autor**

**Franklin David Canduri Presilla**  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Conectar-0077B5.svg?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/franklin-david-canduri-presilla-b75956266/)  
📧 Email: [candurifranklin90@gmail.com](mailto:candurifranklin90@gmail.com)  
🌐 GitHub: [@CanduriFranklin](https://github.com/CanduriFranklin)

---

## �💡 Demonstração Prática de Tecnologias

Este projeto demonstra a implementação prática de:
- **Arquitetura de Microsserviços** com Spring Cloud
- **Arquitetura Orientada a Eventos** com RabbitMQ
- **Domain-Driven Design (DDD)** e Arquitetura Hexagonal
- **Segurança JWT** com controle de acesso baseado em roles
- **Containerização** completa com Docker e Docker Compose
- **Observabilidade** com métricas, logs estruturados e health checks
- **Comunicação Síncrona** (HTTP/REST) entre microsserviços
- **Comunicação Assíncrona** (Message Broker) para eventos
- **Database per Service** pattern
- **Circuit Breaker** e resilência (Resilience4j)

---

## 🎯 Objetivos do Projeto

### Objetivos Técnicos
1. **Desenvolver um microsserviço escalável** capaz de gerenciar operações de armazém em tempo real
2. **Implementar arquitetura orientada a eventos** para desacoplar operações e permitir processamento assíncrono
3. **Aplicar padrões arquiteturais modernos** (Clean Architecture, Hexagonal Architecture, DDD, CQRS)
4. **Garantir segurança robusta** com autenticação JWT e autorização baseada em roles
5. **Facilitar observabilidade** através de logs estruturados, métricas e health checks
6. **Automatizar testes e qualidade** com testes unitários, integração e cobertura de código

### Objetivos de Negócio
1. **Controlar estoque de cestas básicas** com precisão e em tempo real
2. **Gerenciar recebimento de entregas** calculando automaticamente custos e preços de venda
3. **Processar vendas** atualizando inventário e registrando transações
4. **Rastrear perdas e descartes** de produtos vencidos ou danificados
5. **Fornecer visibilidade total** do inventário com valores monetários calculados
6. **Integrar-se com outros microsserviços** através de eventos assíncronos (arquitetura distribuída)

---

## 🚦 Roadmap de Implementação

### ✅ Fase 1: Warehouse Microservice (COMPLETA)

**Status**: 🟢 **100% Implementado e Validado** (15 de Outubro de 2025)

- ✅ Setup do projeto (Spring Boot 3.5.6, Java 25, Gradle 9.1.0)
- ✅ Domain Layer (DDD, Agregados, Value Objects, Events)
- ✅ Application Layer (5 Use Cases, Ports & Adapters)
- ✅ Infrastructure Layer (PostgreSQL, Flyway, RabbitMQ)
- ✅ Adapter Layer (5 Controllers REST, 14 endpoints)
- ✅ Security (JWT HS512, RBAC, 3 roles)
- ✅ Testing (53 unit tests, integration tests)
- ✅ Docker & DevOps (Compose, Health checks, Actuator)
- ✅ Documentation (Swagger, 4.273 linhas de docs)
- ✅ Validation (7 cenários testados, 100% sucesso)

**Tempo investido**: ~40 horas  
**Documentação**: [Ver Relatório Completo](docs/PROJECT_VALIDATION_OCTOBER_15_2025.md)

---

### 📋 Fase 2: Storefront Microservice (PLANEJADA)

**Status**: 🟡 **Planejado - Aguardando Implementação**

**Objetivo**: Criar o segundo microsserviço para completar a arquitetura distribuída.

#### **O Que Será Implementado:**

**Domain Layer**:
- 📋 Product (Produto) - Catálogo de cestas básicas
- 📋 ShoppingCart (Carrinho) - Gestão de itens selecionados
- 📋 Order (Pedido) - Lifecycle completo de pedidos
- 📋 Customer (Cliente) - Cadastro e perfil de clientes

**Application Layer**:
- 📋 AddToCartUseCase, CheckoutUseCase
- 📋 CreateOrderUseCase, GetProductsUseCase
- 📋 HTTP Client para comunicação com Warehouse
- 📋 RabbitMQ Consumers para eventos de estoque

**Infrastructure**:
- 📋 PostgreSQL separado (storefront_db na porta 5433)
- 📋 RestTemplate com Circuit Breaker (Resilience4j)
- 📋 RabbitMQ Configuration (producers e consumers)
- 📋 Flyway migrations (4 arquivos SQL)

**API REST** (15+ endpoints):
```
GET    /api/v1/products                 - Catálogo
POST   /api/v1/cart/items               - Adicionar ao carrinho
POST   /api/v1/cart/checkout            - Finalizar compra
GET    /api/v1/orders                   - Listar pedidos
POST   /api/v1/orders                   - Criar pedido
```

**Comunicação**:
- 🔄 **Síncrona** (HTTP): Storefront → Warehouse (consultar estoque)
- 📨 **Assíncrona** (RabbitMQ): 
  - Warehouse → Storefront (eventos de estoque)
  - Storefront → Warehouse (eventos de pedidos)

**Estimativa**: 38-55 horas (~1-2 semanas)  
**Plano Detalhado**: [docs/STOREFRONT_MICROSERVICE_PLAN.md](docs/STOREFRONT_MICROSERVICE_PLAN.md)

---

### 🔮 Fase 3: Integração e Refinamento (FUTURA)

**Status**: ⏳ **Aguardando Fase 2**

- 🔲 Testes end-to-end entre microsserviços
- 🔲 Contract Testing (Pact ou Spring Cloud Contract)
- 🔲 API Gateway (Spring Cloud Gateway)
- 🔲 Service Discovery (Eureka ou Consul)
- 🔲 Distributed Tracing (Zipkin ou Jaeger)
- 🔲 Centralized Logging (ELK Stack)
- 🔲 CI/CD Pipeline (GitHub Actions)
- 🔲 Kubernetes manifests (deployment, service, ingress)

**Estimativa**: 20-30 horas

---

### 📈 Próximos Passos Imediatos

**Para completar o desafio original:**

1. **📖 Revisar Plano do Storefront**
   - Ler [docs/STOREFRONT_MICROSERVICE_PLAN.md](docs/STOREFRONT_MICROSERVICE_PLAN.md)
   - Validar arquitetura proposta
   - Confirmar escopo de funcionalidades

2. **🚀 Iniciar Implementação**
   - Criar diretório `storefront/` na raiz
   - Setup inicial (build.gradle.kts, application.yml)
   - Implementar Domain Layer (Product, Cart, Order)

3. **🔗 Configurar Comunicação**
   - HTTP Client para Warehouse
   - RabbitMQ Consumers para eventos
   - Circuit Breaker (Resilience4j)

4. **✅ Testar Integração**
   - Fluxo completo: Criar pedido → Reservar estoque → Processar venda
   - Validar comunicação síncrona e assíncrona
   - Testar falhas e rollbacks

5. **📝 Documentar**
   - Swagger/OpenAPI para API do Storefront
   - Diagramas de sequência para fluxos integrados
   - Guia de execução completo do ecossistema

---

## 🔍 Problema a Resolver

### Contexto do Problema

No cenário atual de **comércio eletrônico de cestas básicas**, as empresas enfrentam desafios complexos no gerenciamento de armazéns e inventário. Os sistemas tradicionais monolíticos apresentam limitações críticas:

**1. Falta de Escalabilidade**
- Sistemas monolíticos não escalam horizontalmente de forma eficiente
- Picos de demanda (Black Friday, fim de mês) sobrecarregam toda a aplicação
- Impossibilidade de escalar apenas componentes específicos (ex: processamento de vendas)

**2. Acoplamento Forte e Manutenção Difícil**
- Mudanças em uma funcionalidade podem quebrar outras partes do sistema
- Deploys arriscados exigem parada completa da aplicação
- Equipes de desenvolvimento bloqueadas por dependências de código

**3. Processamento Síncrono Lento**
- Operações de recebimento de grandes entregas travam o sistema
- Usuários aguardam conclusão de processamentos pesados
- Timeouts frequentes em operações longas

**4. Ausência de Rastreabilidade**
- Dificuldade em auditar operações realizadas
- Perda de contexto em falhas de processamento
- Impossibilidade de reprocessar operações falhadas

**5. Dificuldade de Integração**
- Sistemas legados com APIs mal documentadas
- Integração síncrona cria dependências frágeis
- Falhas em cascata quando um sistema fica indisponível

### Solução Proposta

Este microsserviço resolve esses problemas através de:

**✅ Arquitetura de Microsserviços**
- Componente independente, deployável separadamente
- Escala horizontal conforme demanda
- Falhas isoladas não afetam outros serviços

**✅ Arquitetura Orientada a Eventos (EDA)**
- Processamento assíncrono via RabbitMQ
- Desacoplamento total entre produtores e consumidores
- Garantia de entrega com retry automático e dead letter queue

**✅ Domain-Driven Design (DDD)**
- Modelagem rica do domínio de negócio
- Linguagem ubíqua com stakeholders
- Agregados que garantem consistência transacional

**✅ Segurança Enterprise**
- Autenticação JWT com tokens seguros (512 bits)
- Controle de acesso baseado em roles (RBAC)
- Proteção contra ataques comuns (CORS, CSRF)

**✅ Observabilidade Completa**
- Logs estruturados em JSON (ELK Stack ready)
- Métricas exportadas para Prometheus
- Health checks para Kubernetes/Docker

**✅ DevOps e CI/CD Ready**
- Containerização completa com Docker
- Configuração por variáveis de ambiente
- Migrações de banco automáticas (Flyway)

### Impacto Esperado

| Métrica | Antes | Depois |
|---------|-------|--------|
| Tempo de deploy | 30-60 minutos | 5-10 minutos |
| Escalabilidade | Vertical (limitada) | Horizontal (ilimitada) |
| Downtime em falhas | Sistema completo | Serviço isolado |
| Tempo de processamento | Síncrono (lento) | Assíncrono (rápido) |
| Rastreabilidade | Logs básicos | Eventos completos |
| Integração | API REST síncrona | Eventos assíncronos |
| Manutenibilidade | Baixa | Alta |

---

## 🚀 Guia de Início Rápido

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

| Ferramenta | Versão Mínima | Verificar Instalação |
|------------|---------------|---------------------|
| **Docker** | 20.10+ | `docker --version` |
| **Docker Compose** | 2.0+ | `docker-compose --version` |
| **Java** | 17+ (recomendado 25 LTS) | `java -version` |
| **Git** | 2.30+ | `git --version` |

> **💡 Dica**: O projeto usa Java 25 LTS, mas é compatível com Java 17+. Recomendamos usar [Eclipse Temurin](https://adoptium.net/) ou [IBM Semeru Runtime](https://developer.ibm.com/languages/java/semeru-runtimes/).

---

### 📥 Instalação e Configuração

#### **Passo 1: Clone o Repositório**

```bash
# Clone via HTTPS
git clone https://github.com/CanduriFranklin/warehouse-franklindex.doo.git

# Ou via SSH (se tiver chave configurada)
git clone git@github.com:CanduriFranklin/warehouse-franklindex.doo.git

# Entre no diretório
cd warehouse-franklindex.doo
```

#### **Passo 2: Configure as Variáveis de Ambiente**

```bash
# Copie o arquivo de exemplo
cp .env.example .env

# Edite o arquivo .env com suas configurações
nano .env  # ou use seu editor preferido
```

**Configurações importantes no `.env`:**

```properties
# Banco de Dados PostgreSQL
POSTGRES_DB=warehouse_db
POSTGRES_USER=warehouse_user
POSTGRES_PASSWORD=SuaSenhaSegura123!

# JWT Security (IMPORTANTE: use uma chave segura de 512 bits)
JWT_SECRET=SuaChaveSecretaBase64De512Bits==
JWT_EXPIRATION_MS=86400000

# RabbitMQ
RABBITMQ_USER=warehouse_user
RABBITMQ_PASSWORD=SuaSenhaRabbitMQ123!

# Application
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

> **⚠️ IMPORTANTE**: Nunca commite o arquivo `.env` com senhas reais! Ele está no `.gitignore` por segurança.

#### **Passo 3: Inicie os Containers Docker**

```bash
# Inicia PostgreSQL e RabbitMQ em background
docker-compose up -d

# Verifique se os containers estão rodando
docker-compose ps

# Você deve ver:
# - warehouse-postgres (healthy)
# - warehouse-rabbitmq (healthy)
```

**Aguarde ~30 segundos** para os containers iniciarem completamente.

#### **Passo 4: Compile a Aplicação**

```bash
# Dê permissão de execução ao Gradle Wrapper
chmod +x gradlew

# Compile o projeto (sem executar testes para ser mais rápido)
./gradlew clean build -x test

# Ou compile com testes (recomendado para validar tudo)
./gradlew clean build
```

⏱️ **Tempo esperado**: 2-3 minutos na primeira compilação (Gradle baixa dependências).

#### **Passo 5: Execute a Aplicação**

```bash
# Opção 1: Usando o script de execução (recomendado)
chmod +x run.sh
./run.sh

# Opção 2: Usando Gradle diretamente
./gradlew bootRun

# Opção 3: Executando o JAR gerado
java -jar build/libs/warehouse-microservice-0.0.1-SNAPSHOT.jar
```

⏱️ **Tempo de inicialização**: ~20-30 segundos

**Logs esperados no final:**
```
Started WarehouseApplication in 4.523 seconds (process running for 5.123)
Tomcat started on port 8080 (http) with context path '/'
```

---

### 🌐 Pontos de Acesso

Após a inicialização, os seguintes serviços estarão disponíveis:

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| **🚀 API REST** | http://localhost:8080 | JWT Token (veja abaixo) |
| **📚 Swagger UI** | http://localhost:8080/swagger-ui.html | N/A |
| **📊 OpenAPI Spec** | http://localhost:8080/v3/api-docs | N/A |
| **❤️ Health Check** | http://localhost:8080/actuator/health | N/A |
| **🐰 RabbitMQ Management** | http://localhost:15672 | `guest` / `guest` |
| **🐘 pgAdmin** | http://localhost:5050 | `admin@warehouse.com` / `admin` |

---

### 🔐 Autenticação e Primeiros Passos

#### **1. Obtenha um Token JWT**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "expiresAt": "2025-10-16T10:30:00Z",
  "username": "admin",
  "roles": "ROLE_ADMIN,ROLE_WAREHOUSE_MANAGER"
}
```

**Usuários disponíveis (ambiente dev):**

| Username | Password | Roles |
|----------|----------|-------|
| `admin` | `admin123` | ADMIN, WAREHOUSE_MANAGER |
| `manager` | `manager123` | WAREHOUSE_MANAGER |
| `sales` | `sales123` | SALES |

#### **2. Teste os Endpoints Principais**

**a) Receber uma entrega de 50 cestas básicas:**

```bash
curl -X POST http://localhost:8080/api/v1/deliveries \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 50,
    "totalCost": 250.00,
    "profitMarginPercentage": 25.0,
    "validationDate": "2025-12-31"
  }'
```

**b) Verificar o estoque atual:**

```bash
curl -X GET http://localhost:8080/api/v1/stock \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

**Resposta:**
```json
{
  "totalBaskets": 50,
  "availableBaskets": 50,
  "soldBaskets": 0,
  "disposedBaskets": 0,
  "expiredBaskets": 0,
  "totalInventoryValue": 312.50
}
```

**c) Vender 10 cestas básicas:**

```bash
curl -X POST http://localhost:8080/api/v1/baskets/sell \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 10
  }'
```

**d) Verificar estoque após venda:**

```bash
curl -X GET http://localhost:8080/api/v1/stock \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

---

### 🧪 Testando via Swagger UI

Para uma experiência mais visual:

1. Acesse http://localhost:8080/swagger-ui.html
2. Clique em **"POST /api/v1/auth/login"**
3. Clique em **"Try it out"**
4. Digite credenciais (`admin` / `admin123`)
5. Clique em **"Execute"**
6. Copie o token retornado
7. Clique no botão **"Authorize"** no topo da página
8. Cole o token (sem "Bearer ")
9. Agora pode testar todos os endpoints! 🎉

---

### 📊 Monitorando Eventos no RabbitMQ

1. Acesse http://localhost:15672
2. Login: `guest` / `guest`
3. Vá em **"Queues"**
4. Observe as filas:
   - `warehouse.delivery` - Entregas recebidas
   - `warehouse.baskets.sold` - Vendas processadas
   - `warehouse.baskets.disposed` - Descartes registrados
5. Clique em uma fila para ver mensagens processadas

---

### 🛠️ Comandos Úteis

```bash
# Ver logs da aplicação em tempo real
./gradlew bootRun

# Ver logs dos containers Docker
docker-compose logs -f

# Parar containers
docker-compose down

# Parar e remover volumes (⚠️ apaga dados do banco!)
docker-compose down -v

# Recompilar após mudanças no código
./gradlew clean build -x test && ./run.sh

# Executar apenas testes
./gradlew test

# Ver relatório de cobertura de testes
./gradlew test jacocoTestReport
# Abrir: build/reports/jacoco/test/html/index.html

# Verificar se a aplicação está saudável
curl http://localhost:8080/actuator/health
```

---

### ❌ Solução de Problemas Comuns

| Problema | Solução |
|----------|---------|
| **"Port 8080 already in use"** | Outra aplicação está usando a porta. Pare-a ou mude `SERVER_PORT` no `.env` |
| **"Connection refused" ao conectar no banco** | Aguarde 30s após `docker-compose up` para o PostgreSQL iniciar |
| **"401 Unauthorized"** | Token JWT expirado ou inválido. Faça login novamente |
| **"403 Forbidden"** | Usuário não tem permissão. Use `admin` para acesso completo |
| **Erro no Flyway migration** | `docker-compose down -v` e suba novamente (⚠️ apaga dados) |
| **Gradle build falha** | Execute `./gradlew clean build --refresh-dependencies` |

📖 **Documentação completa de troubleshooting**: [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)

---

## 📚 Documentation

### Getting Started
- **[Deployment Guide](docs/DEPLOYMENT_GUIDE.md)** - Complete deployment instructions
- **[Docker Setup](DOCKER_SETUP.md)** - Docker configuration and usage
- **[Setup Guide](SETUP.md)** - Development environment setup
- **[Security Guide](SECURITY.md)** - Security configuration and best practices

### Architecture & Design
- **[Development Guide](docs/03_DEVELOPMENT.md)** - Development practices and guidelines
- **[RabbitMQ Implementation](docs/42_SPRINT_3_RABBITMQ.md)** - Event-driven architecture
- **[RabbitMQ Summary](docs/40_RABBITMQ_IMPLEMENTATION.md)** - Complete RabbitMQ integration
- **[Security Guide](docs/12_SECURITY.md)** - Security best practices
- **[Security Audit](docs/60_SECURITY_AUDIT.md)** - Security improvements log

### API Documentation
- **[Swagger UI](http://localhost:8080/swagger-ui.html)** - Interactive API documentation
- **[OpenAPI Spec](http://localhost:8080/v3/api-docs)** - OpenAPI 3.0 specification

## 🏗️ Technology Stack

### Backend Technologies
- **Java 25 LTS** - Latest long-term support with modern features
- **Spring Boot 3.5.6** - Full Java 25 compatibility
- **Spring Cloud 2025.0.0** - Latest microservices patterns
- **Gradle 9.1.0** - Modern build automation

### Frontend Technologies
- **React 18** - Modern UI library with hooks
- **TypeScript 5.x** - Type-safe JavaScript
- **Vite 7.x** - Lightning-fast build tool
- **TailwindCSS 4.x** - Utility-first CSS framework
- **React Router** - Client-side routing
- **Axios** - HTTP client for API calls

### Infrastructure
- **PostgreSQL 16 Alpine** - Latest relational database
- **RabbitMQ 3.12 Management** - Message broker with UI
- **Docker & Docker Compose** - Containerization with profiles
- **Nginx 1.27 Alpine** - High-performance web server
- **Flyway 11.7.2** - Database migrations

### Architecture & Patterns
- **Clean Architecture** - Domain-driven design
- **Event-Driven Architecture** - Async processing with RabbitMQ
- **Hexagonal Architecture** - Ports and Adapters
- **CQRS** - Command Query Responsibility Segregation
- **DDD** - Domain-Driven Design principles

### Security & Quality
- **Spring Security 6.5** - JWT authentication
- **OpenAPI 3.0** - API documentation (Swagger UI)
- **Resilience4j** - Circuit breakers and retry
- **Micrometer + Prometheus** - Metrics and monitoring
- **Logback + Logstash** - Structured logging

## 📊 Features

### ✨ Core Features
- ✅ **Warehouse Management** - Complete inventory control
- ✅ **Basket Management** - Basic basket CRUD operations
- ✅ **Event-Driven Architecture** - Async processing with RabbitMQ
- ✅ **JWT Authentication** - Secure API access
- ✅ **Role-Based Access Control** - Admin, Manager, Sales roles
- ✅ **Database Migrations** - Automatic schema management with Flyway
- ✅ **Health Checks** - Actuator endpoints for monitoring
- ✅ **API Documentation** - Interactive Swagger UI
- ✅ **Docker Support** - Fully containerized application

### 🎯 RabbitMQ Events
- **Delivery Received** - Process incoming deliveries
- **Baskets Sold** - Track sales and update inventory
- **Baskets Disposed** - Handle disposal and losses
- **Dead Letter Queue** - Failed message handling with retry (3x)

---

## 📝 Licença e Open Source

### 🆓 Este projeto é 100% GRATUITO e de código aberto!

Este projeto está licenciado sob a **Apache License 2.0** - uma das licenças open-source mais populares e permissivas do mundo.

### ❓ Licenças Open Source Têm Custo?

**NÃO! Licenças open-source são COMPLETAMENTE GRATUITAS.** 🎉

Aqui está o que você precisa saber:

#### **O que significa "Open Source"?**

Open source (código aberto) significa que:
- ✅ **O código é público** - qualquer pessoa pode ver, estudar e aprender
- ✅ **Uso gratuito** - você pode usar em projetos pessoais ou comerciais sem pagar nada
- ✅ **Modificação permitida** - pode adaptar o código às suas necessidades
- ✅ **Redistribuição livre** - pode compartilhar ou até vender sua versão modificada
- ✅ **Sem royalties** - nunca precisa pagar por usar ou distribuir

#### **Por que escolhemos Apache License 2.0?**

A Apache License 2.0 é uma das licenças mais **permissivas** e **business-friendly**:

| Permissão | Apache 2.0 | Explicação |
|-----------|------------|------------|
| ✅ **Uso Comercial** | Sim | Pode usar em produtos comerciais |
| ✅ **Modificação** | Sim | Pode alterar o código livremente |
| ✅ **Distribuição** | Sim | Pode compartilhar com outros |
| ✅ **Uso Privado** | Sim | Pode usar internamente na empresa |
| ✅ **Concessão de Patentes** | Sim | Proteção contra processos de patentes |
| ❌ **Responsabilidade** | Não | Sem garantias (use por sua conta) |
| ❌ **Trademark** | Não | Não pode usar o nome/logo sem permissão |

#### **Comparação com Outras Licenças Populares**

| Licença | Permissividade | Uso Comercial | Exige Código Aberto Derivado? |
|---------|----------------|---------------|--------------------------------|
| **Apache 2.0** | 🟢 Alta | ✅ Sim | ❌ Não (pode fechar o código) |
| **MIT** | 🟢 Muito Alta | ✅ Sim | ❌ Não |
| **GPL 3.0** | 🟡 Média | ✅ Sim | ✅ **Sim** (copyleft forte) |
| **AGPL 3.0** | 🔴 Baixa | ⚠️ Complicado | ✅ Sim (até em SaaS) |
| **BSD 3-Clause** | 🟢 Alta | ✅ Sim | ❌ Não |

**Escolhemos Apache 2.0 porque:**
1. **Empresas adoram** - Google, Microsoft, Netflix usam em seus projetos
2. **Proteção de patentes** - cláusula explícita de concessão de patentes
3. **Flexível para derivações** - pode criar versões proprietárias
4. **Compatível com outras licenças** - funciona bem com MIT, BSD, etc.

#### **O que você PODE fazer com este código:**

✅ **Usar em projetos pessoais** - aprenda, estude, experimente  
✅ **Usar em produtos comerciais** - venda seu software baseado neste código  
✅ **Modificar livremente** - adapte às suas necessidades  
✅ **Fechar o código derivado** - sua versão modificada pode ser proprietária  
✅ **Sublicenciar** - pode distribuir sob outra licença compatível  
✅ **Não dar crédito** (mas é educado dar 😊) - não é obrigatório mencionar  

#### **O que você DEVE fazer:**

📋 **Incluir aviso de copyright** - mantenha o arquivo LICENSE no projeto  
📋 **Incluir cópia da licença** - arquivo LICENSE.txt ou LICENSE.md  
📋 **Indicar mudanças** - se modificar, documente o que mudou (boas práticas)  

#### **O que você NÃO PODE fazer:**

❌ **Responsabilizar o autor** - software fornecido "como está", sem garantias  
❌ **Usar marcas registradas** - nome "Warehouse Microservice" pode ter restrições  
❌ **Remover avisos de copyright** - os créditos originais devem permanecer  

#### **Exemplo Prático**

Imagine que você:
1. **Copia este projeto** ✅ Permitido e gratuito
2. **Modifica para sua empresa** ✅ Totalmente ok
3. **Adiciona features proprietárias** ✅ Pode fazer
4. **Vende como produto comercial** ✅ Pode vender
5. **Fecha o código-fonte** ✅ Não precisa abrir seu código

Tudo isso **SEM PAGAR UM CENTAVO** ao autor original! 🎉

#### **Projetos Famosos com Apache 2.0**

Você está em boa companhia! Projetos gigantes usam Apache 2.0:

- **Kubernetes** - Orquestração de containers (Google)
- **Apache Kafka** - Streaming de eventos
- **Apache Spark** - Big data processing
- **TensorFlow** - Machine learning (Google)
- **Android** - Sistema operacional (Google)
- **Swift** - Linguagem de programação (Apple)

### 📜 Texto Completo da Licença

Leia o texto completo em: **[LICENSE](LICENSE)** ou em [opensource.org/licenses/Apache-2.0](https://opensource.org/licenses/Apache-2.0)

### 🤝 Contribuindo

Contribuições são **bem-vindas e encorajadas**! 

Se você quiser melhorar este projeto:
1. Faça um **fork** do repositório
2. Crie uma **branch** para sua feature (`git checkout -b feature/MinhaFeature`)
3. **Commit** suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. **Push** para a branch (`git push origin feature/MinhaFeature`)
5. Abra um **Pull Request**

📖 Veja o guia completo em: [CONTRIBUTING.md](CONTRIBUTING.md) *(a criar)*

---

## ✨ Autor

**Franklin David Canduri Presilla**  
[![GitHub](https://img.shields.io/badge/GitHub-CanduriFranklin-181717?logo=github)](https://github.com/CanduriFranklin)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Franklin_Canduri-0077B5?logo=linkedin)](https://www.linkedin.com/in/franklin-david-canduri-presilla-b75956266/)
[![Gmail](https://img.shields.io/badge/Gmail-candurifranklin90-EA4335?logo=gmail&logoColor=white)](mailto:candurifranklin90@gmail.com)

💬 Dúvidas ou sugestões? Abra uma [issue](https://github.com/CanduriFranklin/warehouse-franklindex.doo/issues) ou entre em contato pelo email!

---

## 🌟 Agradecimentos

- **Digital Innovation One (DIO)** - Plataforma de educação que inspira desenvolvedores
- **Spring Community** - Framework incrível e documentação excepcional
- **RabbitMQ Team** - Melhor message broker open-source
- **PostgreSQL Global Development Group** - Banco de dados robusto e confiável
- **Todos os contribuidores** de bibliotecas open-source usadas neste projeto

---

## 📚 Recursos Adicionais

### Documentação Técnica
- [� Índice Completo de Documentação](docs/00_INDEX.md) - Navegação de toda documentação
- [🚀 Quick Start](docs/01_QUICK_START.md) - Começando rapidamente
- [�📖 Guia de Deployment](docs/10_DEPLOYMENT_GUIDE.md) - Deploy em produção
- [🐳 Setup Docker](docs/02_DOCKER_SETUP.md) - Configuração Docker
- [⚡ Otimizações Docker](docs/04_DOCKER_OPTIMIZATION.md) - Performance e recursos
- [🔐 Guia de Segurança](docs/12_SECURITY.md) - Práticas de segurança
- [🐰 Implementação RabbitMQ](docs/42_SPRINT_3_RABBITMQ.md) - Mensageria assíncrona
- [🛠️ Troubleshooting](docs/11_TROUBLESHOOTING.md) - Resolução de problemas
- [✅ Validação do Projeto (Oct 16, 2025)](docs/21_VALIDATION_16_10_2025.md) - Validação mais recente
- [📊 Status do Projeto](docs/20_STATUS.md) - Status atual

### Decisões Arquiteturais (ADRs)
- [🏛️ Architecture Decision Records](docs/adr/)

### Comunidade e Suporte
- [🐛 Reportar Bug](https://github.com/CanduriFranklin/warehouse-franklindex.doo/issues/new?template=bug_report.md)
- [✨ Solicitar Feature](https://github.com/CanduriFranklin/warehouse-franklindex.doo/issues/new?template=feature_request.md)
- [💬 Discussões](https://github.com/CanduriFranklin/warehouse-franklindex.doo/discussions)

---

<div align="center">

**⭐ Se este projeto te ajudou, considere dar uma estrela no GitHub! ⭐**

**Feito com ❤️ e ☕ por Franklin Canduri**

</div>
