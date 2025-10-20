
# 🏪 Plano de Implementação: Storefront Microservice

**Data de Criação**: 15 de Outubro de 2025  
**Status**: ✅ Concluído (100%)  
**Objetivo**: Criar o segundo microsserviço da arquitetura conforme desafio original

---

## 🎯 Objetivo do Desafio

> **Construir dois microsserviços principais:**
> - ✅ **Warehouse (Armazém)** - Gerencia estoque e operações de armazém
> - ❌ **Storefront (Vitrine/Loja)** - Interface de venda para clientes
>
> **Comunicação:**
> - 🔄 **Síncrona** - HTTP/REST entre serviços
> - 📨 **Assíncrona** - RabbitMQ para eventos

---

## 📋 Situação Atual

### ✅ Warehouse Microservice (COMPLETO)
```
warehouse-franklindex.doo/
├── src/main/java/br/com/dio/warehouse/
│   ├── domain/              ✅ Completo
│   ├── application/         ✅ Completo
│   ├── infrastructure/      ✅ Completo
│   └── adapter/             ✅ Completo
├── docker-compose.yml       ✅ PostgreSQL + RabbitMQ
└── API REST                 ✅ 14 endpoints funcionando
```

**Portas:**
- Aplicação: `8080`
- PostgreSQL: `5432`
- RabbitMQ: `5672` (AMQP) e `15672` (Management)

### ✅ Storefront Microservice (COMPLETO)

**Status:** 100% concluído. Todas as funcionalidades planejadas para o Storefront/frontend foram implementadas e testadas com sucesso.

---

## 🏗️ Arquitetura Proposta

### Visão Geral do Ecossistema

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE (Frontend)                        │
│                    Angular/React/Vue                         │
└────────────────┬────────────────────────────────────────────┘
                 │ HTTP/REST
                 ▼
┌────────────────────────────────────────────────────────────┐
│              🏪 STOREFRONT MICROSERVICE                     │
│                    (Porta 8081)                             │
├────────────────────────────────────────────────────────────┤
│  Domain:                                                    │
│  - Product (Produto)                                        │
│  - ShoppingCart (Carrinho)                                  │
│  - Order (Pedido)                                           │
│  - Customer (Cliente)                                       │
├────────────────────────────────────────────────────────────┤
│  Funcionalidades:                                           │
│  ✓ Catálogo de produtos                                    │
│  ✓ Carrinho de compras                                     │
│  ✓ Checkout e pedidos                                      │
│  ✓ Consulta disponibilidade (chama Warehouse via HTTP)     │
│  ✓ Consome eventos de estoque (RabbitMQ)                   │
└────────────┬────────────────────┬──────────────────────────┘
             │                    │
             │ HTTP/REST          │ RabbitMQ Events
             │ (Síncrono)         │ (Assíncrono)
             ▼                    ▼
┌────────────────────────────────────────────────────────────┐
│              🏭 WAREHOUSE MICROSERVICE                      │
│                    (Porta 8080)                             │
├────────────────────────────────────────────────────────────┤
│  Domain:                                                    │
│  - DeliveryBox (Entrega)                                    │
│  - BasicBasket (Cesta Básica)                              │
├────────────────────────────────────────────────────────────┤
│  Funcionalidades:                                           │
│  ✓ Gerenciamento de estoque                                │
│  ✓ Recebimento de entregas                                 │
│  ✓ Venda de cestas                                         │
│  ✓ Descarte de vencidos                                    │
│  ✓ Publica eventos (DeliveryReceived, BasketsSold)         │
└────────────────────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────────┐
│                   💾 INFRAESTRUTURA                         │
├────────────────────────────────────────────────────────────┤
│  - PostgreSQL (2 databases)                                 │
│    • warehouse_db (porta 5432)                             │
│    • storefront_db (porta 5433)                            │
│                                                             │
│  - RabbitMQ (compartilhado)                                │
│    • AMQP: 5672                                            │
│    • Management: 15672                                     │
└────────────────────────────────────────────────────────────┘
```

---

## 📦 Storefront - Modelo de Domínio

### Agregados e Entidades

#### 1. **Product (Produto)**
```java
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private UUID id;
    
    private String name;
    private String description;
    private Money price;  // Preço consultado do Warehouse
    private Integer availableQuantity;  // Cache do estoque
    private String imageUrl;
    private ProductCategory category;
    private boolean active;
    
    @Embedded
    private Timestamps timestamps;
    
    // Métodos de domínio
    public boolean isAvailable() {
        return active && availableQuantity > 0;
    }
    
    public void updateStock(int quantity) {
        this.availableQuantity = quantity;
    }
}

enum ProductCategory {
    BASIC_BASKET,
    PREMIUM_BASKET,
    ORGANIC_BASKET
}
```

#### 2. **ShoppingCart (Carrinho)**
```java
@Entity
@Getter
@Builder
public class ShoppingCart {
    @Id
    private UUID id;
    
    @ManyToOne
    private Customer customer;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    private CartStatus status;
    
    @Embedded
    private Timestamps timestamps;
    
    // Métodos de domínio
    public void addItem(Product product, int quantity) {
        // Valida disponibilidade
        // Adiciona ou atualiza item
    }
    
    public void removeItem(UUID productId) {}
    
    public Money calculateTotal() {
        return items.stream()
            .map(item -> item.getSubtotal())
            .reduce(Money.ZERO, Money::add);
    }
    
    public int getTotalItems() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
}

@Embeddable
@Getter
class CartItem {
    @ManyToOne
    private Product product;
    
    private Integer quantity;
    
    private Money unitPrice;
    
    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }
}

enum CartStatus {
    ACTIVE,
    CONVERTED_TO_ORDER,
    ABANDONED
}
```

#### 3. **Order (Pedido)**
```java
@Entity
@Table(name = "orders")
@Getter
@Builder
public class Order {
    @Id
    private UUID id;
    
    private String orderNumber;  // AUTO-GENERATED (ex: ORD-20251015-0001)
    
    @ManyToOne
    private Customer customer;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    private Money subtotal;
    private Money tax;
    private Money total;
    
    private OrderStatus status;
    
    @Embedded
    private ShippingAddress shippingAddress;
    
    @Embedded
    private PaymentInfo paymentInfo;
    
    @Embedded
    private Timestamps timestamps;
    
    @Version
    private Long version;
    
    // Métodos de domínio
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Order already confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Cannot cancel delivered order");
        }
        this.status = OrderStatus.CANCELLED;
    }
}

@Embeddable
@Getter
class OrderItem {
    @ManyToOne
    private Product product;
    
    private Integer quantity;
    private Money unitPrice;
    private Money subtotal;
}

enum OrderStatus {
    PENDING,           // Criado mas não confirmado
    CONFIRMED,         // Confirmado, aguardando separação
    PROCESSING,        // Warehouse está separando
    SHIPPED,           // Enviado
    DELIVERED,         // Entregue
    CANCELLED          // Cancelado
}
```

#### 4. **Customer (Cliente)**
```java
@Entity
@Getter
@Builder
public class Customer {
    @Id
    private UUID id;
    
    private String email;
    private String fullName;
    private String cpf;
    private String phone;
    
    @Embedded
    private Address defaultAddress;
    
    private CustomerType type;
    
    @Embedded
    private Timestamps timestamps;
}

enum CustomerType {
    INDIVIDUAL,   // Pessoa física
    BUSINESS      // Pessoa jurídica
}
```

---

## 🔄 Comunicação entre Microsserviços

### 1. **Comunicação Síncrona (HTTP/REST)**

**Storefront → Warehouse**

```java
// Storefront consulta disponibilidade em tempo real
@Service
@RequiredArgsConstructor
public class WarehouseClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${warehouse.api.url}")
    private String warehouseUrl;  // http://localhost:8080
    
    public StockInfoResponse checkStock() {
        String url = warehouseUrl + "/api/v1/stock";
        return restTemplate.getForObject(url, StockInfoResponse.class);
    }
    
    public boolean reserveBaskets(int quantity) {
        String url = warehouseUrl + "/api/v1/baskets/reserve";
        ReserveRequest request = new ReserveRequest(quantity);
        
        try {
            restTemplate.postForObject(url, request, ReserveResponse.class);
            return true;
        } catch (HttpClientErrorException e) {
            return false;
        }
    }
}
```

### 2. **Comunicação Assíncrona (RabbitMQ)**

**Warehouse → Storefront** (Eventos)

```java
// Storefront consome eventos do Warehouse
@Component
@RabbitListener(queues = "storefront.stock.updates")
@RequiredArgsConstructor
public class StockUpdateEventConsumer {
    
    private final ProductService productService;
    
    @RabbitHandler
    public void handleBasketsSoldEvent(BasketsSoldEvent event) {
        log.info("Received BasketsSoldEvent: {} baskets sold", event.quantity());
        
        // Atualiza cache de estoque local
        productService.decreaseStock(event.quantity());
    }
    
    @RabbitHandler
    public void handleDeliveryReceivedEvent(DeliveryReceivedEvent event) {
        log.info("Received DeliveryReceivedEvent: {} baskets", event.totalQuantity());
        
        // Atualiza disponibilidade de produtos
        productService.increaseStock(event.totalQuantity().intValue());
    }
}

// Storefront publica eventos de pedidos
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = OrderCreatedEvent.builder()
            .eventId(UUID.randomUUID())
            .occurredOn(Instant.now())
            .orderId(order.getId())
            .customerId(order.getCustomer().getId())
            .totalItems(order.getItems().size())
            .totalAmount(order.getTotal().getAmount())
            .build();
        
        rabbitTemplate.convertAndSend(
            "storefront.events",
            "order.created",
            event
        );
    }
}
```

**Configuração RabbitMQ**

```yaml
# storefront/application.yml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: ${RABBITMQ_USER}
    password: ${RABBITMQ_PASSWORD}
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 3s
          max-attempts: 3
          multiplier: 2.0

# Exchanges e Queues
rabbitmq:
  exchanges:
    warehouse-events: warehouse.events
    storefront-events: storefront.events
  queues:
    stock-updates: storefront.stock.updates
    order-notifications: warehouse.order.notifications
  routing-keys:
    baskets-sold: baskets.sold
    delivery-received: delivery.received
    order-created: order.created
```

---

## 📡 API REST do Storefront

### Endpoints Principais

#### **Products (Catálogo)**
```
GET    /api/v1/products                 - Listar produtos (paginado)
GET    /api/v1/products/{id}            - Detalhe do produto
GET    /api/v1/products/search          - Buscar por nome/categoria
GET    /api/v1/products/available       - Produtos disponíveis em estoque
```

#### **Shopping Cart**
```
GET    /api/v1/cart                     - Obter carrinho atual
POST   /api/v1/cart/items               - Adicionar item ao carrinho
PUT    /api/v1/cart/items/{productId}   - Atualizar quantidade
DELETE /api/v1/cart/items/{productId}   - Remover item
DELETE /api/v1/cart                     - Limpar carrinho
POST   /api/v1/cart/checkout            - Finalizar compra (cria pedido)
```

#### **Orders**
```
GET    /api/v1/orders                   - Listar pedidos do cliente
GET    /api/v1/orders/{id}              - Detalhe do pedido
POST   /api/v1/orders                   - Criar pedido (via checkout)
PUT    /api/v1/orders/{id}/cancel       - Cancelar pedido
GET    /api/v1/orders/{id}/status       - Status do pedido
```

#### **Customers**
```
POST   /api/v1/customers                - Cadastrar cliente
GET    /api/v1/customers/me             - Dados do cliente logado
PUT    /api/v1/customers/me             - Atualizar dados
PUT    /api/v1/customers/me/address     - Atualizar endereço
```

#### **Authentication**
```
POST   /api/v1/auth/register            - Registrar novo cliente
POST   /api/v1/auth/login               - Login
POST   /api/v1/auth/logout              - Logout
GET    /api/v1/auth/me                  - Usuário atual
```

---

## 🛠️ Tecnologias (Storefront)

### Stack Idêntico ao Warehouse

```kotlin
// build.gradle.kts
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    
    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    
    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // MapStruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
}
```

---

## 🐳 Docker Compose Atualizado

```yaml
# docker-compose.yml (atualizado para 2 microsserviços)
version: '3.8'

services:
  # PostgreSQL para Warehouse
  warehouse-postgres:
    image: postgres:16-alpine
    container_name: warehouse-postgres
    environment:
      POSTGRES_DB: warehouse_db
      POSTGRES_USER: warehouse_user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - warehouse-data:/var/lib/postgresql/data
    networks:
      - ecommerce-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U warehouse_user -d warehouse_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  # PostgreSQL para Storefront
  storefront-postgres:
    image: postgres:16-alpine
    container_name: storefront-postgres
    environment:
      POSTGRES_DB: storefront_db
      POSTGRES_USER: storefront_user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5433:5432"  # Porta externa diferente!
    volumes:
      - storefront-data:/var/lib/postgresql/data
    networks:
      - ecommerce-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U storefront_user -d storefront_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  # RabbitMQ (compartilhado)
  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: ecommerce-rabbitmq
    ports:
      - "5672:5672"    # AMQP
      - "15672:15672"  # Management UI
    environment:
      RABBITMQ_DEFAULT_USER: ${RABBITMQ_USER}
      RABBITMQ_DEFAULT_PASS: ${RABBITMQ_PASSWORD}
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq
    networks:
      - ecommerce-network
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Warehouse Microservice
  warehouse-service:
    build:
      context: ./warehouse
      dockerfile: Dockerfile
    container_name: warehouse-service
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://warehouse-postgres:5432/warehouse_db
      SPRING_DATASOURCE_USERNAME: warehouse_user
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_USERNAME: ${RABBITMQ_USER}
      SPRING_RABBITMQ_PASSWORD: ${RABBITMQ_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      warehouse-postgres:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
    networks:
      - ecommerce-network
    restart: unless-stopped

  # Storefront Microservice
  storefront-service:
    build:
      context: ./storefront
      dockerfile: Dockerfile
    container_name: storefront-service
    ports:
      - "8081:8081"
    environment:
      SERVER_PORT: 8081
      SPRING_DATASOURCE_URL: jdbc:postgresql://storefront-postgres:5432/storefront_db
      SPRING_DATASOURCE_USERNAME: storefront_user
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_USERNAME: ${RABBITMQ_USER}
      SPRING_RABBITMQ_PASSWORD: ${RABBITMQ_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      WAREHOUSE_API_URL: http://warehouse-service:8080
    depends_on:
      storefront-postgres:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
      warehouse-service:
        condition: service_started
    networks:
      - ecommerce-network
    restart: unless-stopped

volumes:
  warehouse-data:
  storefront-data:
  rabbitmq-data:

networks:
  ecommerce-network:
    driver: bridge
```

---

## 📁 Estrutura de Diretórios

```
ecommerce-platform/
├── warehouse/                      # Microsserviço Warehouse (EXISTENTE)
│   ├── src/
│   ├── build.gradle.kts
│   ├── Dockerfile
│   └── ...
│
├── storefront/                     # Microsserviço Storefront (NOVO)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/br/com/dio/storefront/
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── Product.java
│   │   │   │   │   │   ├── ShoppingCart.java
│   │   │   │   │   │   ├── Order.java
│   │   │   │   │   │   └── Customer.java
│   │   │   │   │   ├── valueobject/
│   │   │   │   │   │   ├── Money.java
│   │   │   │   │   │   ├── Address.java
│   │   │   │   │   │   └── PaymentInfo.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── event/
│   │   │   │   │   └── exception/
│   │   │   │   │
│   │   │   │   ├── application/
│   │   │   │   │   ├── port/in/
│   │   │   │   │   │   ├── AddToCartUseCase.java
│   │   │   │   │   │   ├── CheckoutUseCase.java
│   │   │   │   │   │   ├── CreateOrderUseCase.java
│   │   │   │   │   │   └── GetProductsUseCase.java
│   │   │   │   │   ├── port/out/
│   │   │   │   │   │   └── WarehouseClient.java
│   │   │   │   │   └── service/
│   │   │   │   │       ├── CartService.java
│   │   │   │   │       ├── OrderService.java
│   │   │   │   │       └── ProductService.java
│   │   │   │   │
│   │   │   │   ├── infrastructure/
│   │   │   │   │   ├── adapter/
│   │   │   │   │   │   ├── in/
│   │   │   │   │   │   │   ├── web/
│   │   │   │   │   │   │   │   ├── controller/
│   │   │   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   │   │   └── mapper/
│   │   │   │   │   │   │   └── messaging/
│   │   │   │   │   │   │       └── StockUpdateEventConsumer.java
│   │   │   │   │   │   └── out/
│   │   │   │   │   │       ├── persistence/
│   │   │   │   │   │       ├── http/
│   │   │   │   │   │       │   └── WarehouseHttpClient.java
│   │   │   │   │   │       └── messaging/
│   │   │   │   │   │           └── OrderEventPublisher.java
│   │   │   │   │   ├── config/
│   │   │   │   │   │   ├── RabbitMQConfig.java
│   │   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   │   ├── RestTemplateConfig.java
│   │   │   │   │   │   └── OpenAPIConfig.java
│   │   │   │   │   └── security/
│   │   │   │   │       ├── JwtTokenProvider.java
│   │   │   │   │       └── JwtAuthenticationFilter.java
│   │   │   │   │
│   │   │   │   └── StorefrontApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       ├── application-prod.yml
│   │   │       └── db/migration/
│   │   │           ├── V1__create_products_table.sql
│   │   │           ├── V2__create_customers_table.sql
│   │   │           ├── V3__create_shopping_carts_table.sql
│   │   │           └── V4__create_orders_table.sql
│   │   │
│   │   └── test/
│   │       └── java/br/com/dio/storefront/
│   │
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── Dockerfile
│   ├── .env.example
│   └── README.md
│
├── docker-compose.yml              # Orquestra AMBOS os serviços
├── .gitignore
└── README.md                       # Documentação do projeto completo
```

---

## 🚀 Roadmap de Implementação

### **Fase 1: Setup Inicial** (2-3 horas)
- [ ] Criar diretório `storefront/` na raiz
- [ ] Configurar `build.gradle.kts` com dependências
- [ ] Criar `application.yml` com configurações
- [ ] Configurar `.env` com variáveis de ambiente
- [ ] Criar `StorefrontApplication.java`

### **Fase 2: Domain Layer** (4-6 horas)
- [ ] Implementar `Product` entity
- [ ] Implementar `ShoppingCart` aggregate
- [ ] Implementar `Order` aggregate
- [ ] Implementar `Customer` entity
- [ ] Criar Value Objects (Money, Address, PaymentInfo)
- [ ] Criar Domain Events (OrderCreated, CartCheckoutStarted)
- [ ] Criar Domain Exceptions

### **Fase 3: Application Layer** (4-6 horas)
- [ ] Criar Use Cases (Input Ports):
  - [ ] AddToCartUseCase
  - [ ] CheckoutUseCase
  - [ ] CreateOrderUseCase
  - [ ] GetProductsUseCase
  - [ ] GetOrdersUseCase
- [ ] Criar Output Ports:
  - [ ] WarehouseClient (interface)
  - [ ] EventPublisher (interface)
- [ ] Implementar Services

### **Fase 4: Infrastructure Layer** (6-8 horas)
- [ ] **Persistence:**
  - [ ] JPA Repositories
  - [ ] Flyway Migrations (4 arquivos SQL)
- [ ] **HTTP Client:**
  - [ ] Implementar WarehouseHttpClient com RestTemplate
  - [ ] Circuit Breaker (Resilience4j)
- [ ] **Messaging:**
  - [ ] RabbitMQ Configuration
  - [ ] StockUpdateEventConsumer
  - [ ] OrderEventPublisher
- [ ] **Security:**
  - [ ] JWT Configuration (reutilizar do Warehouse)
  - [ ] SecurityConfig

### **Fase 5: Adapter Layer - REST API** (6-8 horas)
- [ ] **Controllers:**
  - [ ] ProductController
  - [ ] CartController
  - [ ] OrderController
  - [ ] CustomerController
  - [ ] AuthenticationController
- [ ] **DTOs:** 15+ classes
- [ ] **Mappers:** MapStruct mappers
- [ ] **Exception Handlers:** GlobalExceptionHandler

### **Fase 6: Testing** (6-8 horas)
- [ ] Unit Tests (Domain, Application)
- [ ] Integration Tests (API, Database)
- [ ] RabbitMQ Tests (Testcontainers)
- [ ] Contract Tests (comunicação com Warehouse)

### **Fase 7: Documentation** (2-3 horas)
- [ ] OpenAPI/Swagger annotations
- [ ] README.md do Storefront
- [ ] Diagramas de arquitetura
- [ ] Guia de API

### **Fase 8: Docker & DevOps** (2-3 horas)
- [ ] Dockerfile do Storefront
- [ ] Atualizar docker-compose.yml
- [ ] Scripts de inicialização
- [ ] Health checks

### **Fase 9: Integration End-to-End** (4-6 horas)
- [ ] Testar comunicação HTTP Storefront → Warehouse
- [ ] Testar eventos RabbitMQ em ambas direções
- [ ] Validar fluxo completo: Criar pedido → Reservar estoque → Processar venda
- [ ] Testar falhas e rollbacks

### **Fase 10: Refinamento** (2-4 horas)
- [ ] Code review
- [ ] Otimizações de performance
- [ ] Logging e monitoramento
- [ ] Documentação final

**TOTAL ESTIMADO:** 38-55 horas (~1-2 semanas)

---

## 🔄 Fluxo de Negócio Completo

### Cenário: Cliente Compra 3 Cestas Básicas

```
1. CLIENTE (Frontend)
   ↓ GET /api/v1/products
   
2. STOREFRONT
   ↓ Consulta produtos no banco local
   ↓ HTTP GET → http://warehouse:8080/api/v1/stock
   
3. WAREHOUSE
   ↓ Retorna estoque disponível: 100 cestas
   ↓ Response: { "availableBaskets": 100, "totalInventoryValue": 6250.00 }
   
4. STOREFRONT
   ↓ Atualiza cache de estoque
   ↓ Retorna produtos para o cliente
   
5. CLIENTE
   ↓ POST /api/v1/cart/items { "productId": "...", "quantity": 3 }
   
6. STOREFRONT
   ↓ Valida disponibilidade
   ↓ Adiciona ao carrinho (não reserva ainda)
   ↓ Response: Carrinho atualizado
   
7. CLIENTE
   ↓ POST /api/v1/cart/checkout
   
8. STOREFRONT
   ↓ Cria pedido (status: PENDING)
   ↓ HTTP POST → http://warehouse:8080/api/v1/baskets/reserve { "quantity": 3 }
   
9. WAREHOUSE
   ↓ Reserva 3 cestas (status: RESERVED)
   ↓ Response: { "success": true, "reservedBaskets": [...] }
   
10. STOREFRONT
    ↓ Confirma pedido (status: CONFIRMED)
    ↓ RabbitMQ PUBLISH → OrderCreatedEvent
    
11. WAREHOUSE (Consumer)
    ↓ Consome OrderCreatedEvent
    ↓ Marca cestas como SOLD
    ↓ RabbitMQ PUBLISH → BasketsSoldEvent
    
12. STOREFRONT (Consumer)
    ↓ Consome BasketsSoldEvent
    ↓ Atualiza cache de estoque (-3)
    ↓ Atualiza pedido (status: PROCESSING)
    
13. RESPONSE ao Cliente
    ↓ { "orderId": "...", "status": "CONFIRMED", "total": 18.75 }
```

---

## 🎯 Critérios de Sucesso

### Funcional
- [ ] Storefront pode consultar estoque do Warehouse (HTTP síncrono)
- [ ] Storefront consome eventos de estoque (RabbitMQ assíncrono)
- [ ] Warehouse consome eventos de pedidos (RabbitMQ assíncrono)
- [ ] Carrinho de compras funciona corretamente
- [ ] Checkout cria pedidos e reserva estoque
- [ ] Pedidos têm lifecycle completo (PENDING → CONFIRMED → PROCESSING → DELIVERED)

### Técnico
- [ ] Ambos microsserviços seguem Hexagonal Architecture
- [ ] Ambos usam Domain-Driven Design
- [ ] Comunicação síncrona com Circuit Breaker
- [ ] Comunicação assíncrona com retry policy
- [ ] Cada microsserviço tem seu próprio banco de dados
- [ ] RabbitMQ compartilhado entre serviços
- [ ] Autenticação JWT funciona em ambos
- [ ] Cobertura de testes > 80%

### DevOps
- [ ] Docker Compose inicia ambos serviços + infra
- [ ] Health checks funcionando
- [ ] Logs estruturados
- [ ] Métricas do Actuator
- [ ] Swagger UI documentando ambas APIs

---

## 📚 Referências

- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [Resilience4j Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)
- [RabbitMQ Best Practices](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)

---


---

**Status Final**: ✅ Storefront/frontend 100% concluído e integrado. Todas as etapas do plano foram realizadas com sucesso.

**Última Atualização**: 15 de Outubro de 2025
