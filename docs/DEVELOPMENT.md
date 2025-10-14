# Guia de Desenvolvimento

## Pré-requisitos

- **Java 25 LTS**
- **Gradle 9.0+**
- **Docker & Docker Compose**
- **PostgreSQL 16+**
- **RabbitMQ 3.13+**
- **IDE**: IntelliJ IDEA, VS Code, ou Eclipse

## Configuração do Ambiente

### 1. Clone o Repositório

```bash
git clone https://github.com/CanduriFranklin/warehouse-franklindex.doo.git
cd warehouse-franklindex.doo
```

### 2. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=warehouse_db
DB_USERNAME=warehouse_user
DB_PASSWORD=your_secure_password

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# Security
JWT_SECRET=your-256-bit-secret-key-change-in-production

# Application
SPRING_PROFILES_ACTIVE=dev
```

### 3. Iniciar Dependências com Docker

```bash
docker-compose up -d postgres rabbitmq
```

### 4. Build e Run

```bash
# Build
gradle clean build

# Run
gradle bootRun

# Run com profile específico
gradle bootRun --args='--spring.profiles.active=dev'
```

## Estrutura do Código

```
src/main/java/br/com/dio/warehouse/
├── domain/                    # Camada de Domínio
│   ├── model/                # Entidades e Agregados
│   ├── valueobject/          # Value Objects
│   ├── repository/           # Interfaces de Repository
│   ├── event/                # Domain Events
│   └── exception/            # Domain Exceptions
│
├── application/               # Camada de Aplicação
│   ├── usecase/              # Casos de Uso
│   ├── port/in/              # Input Ports
│   └── port/out/             # Output Ports
│
├── infrastructure/            # Camada de Infraestrutura
│   ├── persistence/          # JPA Repositories
│   ├── messaging/            # RabbitMQ
│   └── config/               # Spring Configurations
│
└── presentation/              # Camada de Apresentação
    ├── rest/                 # REST Controllers
    ├── dto/                  # Data Transfer Objects
    ├── mapper/               # MapStruct Mappers
    └── exception/            # Exception Handlers
```

## Convenções de Código

### Nomenclatura

- **Classes**: PascalCase (ex: `BasicBasket`, `DeliveryBox`)
- **Métodos**: camelCase (ex: `calculateSellingPrice()`)
- **Constantes**: UPPER_SNAKE_CASE (ex: `MAX_RETRIES`)
- **Packages**: lowercase (ex: `br.com.dio.warehouse`)

### Anotações

Use Lombok para reduzir boilerplate:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyEntity {
    // fields
}
```

### Comentários

- Use JavaDoc para classes e métodos públicos
- Evite comentários óbvios
- Explique o "porquê", não o "o quê"

```java
/**
 * Calculates the selling price with profit margin applied.
 * 
 * @param marginPercentage the profit margin (e.g., 0.20 for 20%)
 * @return the calculated selling price
 */
public Money calculateSellingPrice(Double marginPercentage) {
    // implementation
}
```

## Testes

### Executar Testes

```bash
# Todos os testes
gradle test

# Testes específicos
gradle test --tests "br.com.dio.warehouse.domain.*"

# Com relatório de cobertura
gradle test jacocoTestReport
```

### Estrutura de Testes

```
src/test/java/br/com/dio/warehouse/
├── domain/                    # Testes de Domínio
│   ├── model/                # Testes de Entidades
│   └── valueobject/          # Testes de Value Objects
│
├── application/               # Testes de Use Cases
│   └── usecase/
│
├── infrastructure/            # Testes de Integração
│   └── persistence/
│
└── presentation/              # Testes de API
    └── rest/
```

### Exemplo de Teste Unitário

```java
@ExtendWith(MockitoExtension.class)
class SellBasketsUseCaseTest {
    
    @Mock
    private BasketRepository basketRepository;
    
    @InjectMocks
    private SellBasketsUseCase useCase;
    
    @Test
    void shouldSellBaskets() {
        // Given
        List<BasicBasket> baskets = createMockBaskets();
        when(basketRepository.findCheapestAvailableBaskets(anyInt()))
            .thenReturn(baskets);
        
        // When
        SellResult result = useCase.execute(new SellCommand(2));
        
        // Then
        assertThat(result.quantity()).isEqualTo(2);
        verify(basketRepository).saveAll(anyList());
    }
}
```

### Exemplo de Teste de Integração

```java
@SpringBootTest
@Testcontainers
class BasketRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:16-alpine");
    
    @Autowired
    private BasketRepository repository;
    
    @Test
    void shouldFindExpiredBaskets() {
        // Given
        BasicBasket expired = createExpiredBasket();
        repository.save(expired);
        
        // When
        List<BasicBasket> result = repository.findExpiredBaskets();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isExpired()).isTrue();
    }
}
```

## Migrations (Flyway)

### Criar Nova Migration

1. Criar arquivo em `src/main/resources/db/migration/`
2. Nomenclatura: `V{version}__{description}.sql`
3. Exemplo: `V2__Add_supplier_table.sql`

```sql
-- V2__Add_supplier_table.sql
CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_supplier_email ON suppliers(email);
```

## API Development

### Criar Novo Endpoint

1. **Criar DTO** (`presentation/dto/`)
2. **Criar Mapper** (`presentation/mapper/`)
3. **Criar Controller** (`presentation/rest/`)
4. **Documentar com OpenAPI**

```java
@RestController
@RequestMapping("/api/v1/baskets")
@RequiredArgsConstructor
@Tag(name = "Baskets", description = "Basket management endpoints")
public class BasketController {
    
    @PostMapping("/sell")
    @Operation(summary = "Sell baskets", description = "Sells the specified quantity of cheapest available baskets")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Baskets sold successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "422", description = "Insufficient stock")
    })
    public ResponseEntity<SellResponseDto> sellBaskets(
        @Valid @RequestBody SellRequestDto request
    ) {
        // implementation
    }
}
```

## Debug

### Logs

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MyService {
    
    public void doSomething() {
        log.debug("Debug message");
        log.info("Info message");
        log.warn("Warning message");
        log.error("Error message", exception);
    }
}
```

### Profiles

```yaml
# application-dev.yml
logging:
  level:
    root: DEBUG
    br.com.dio: DEBUG
```

## Git Workflow

### Branches

- `main`: Production
- `develop`: Development
- `feature/*`: New features
- `bugfix/*`: Bug fixes
- `hotfix/*`: Production hotfixes

### Commits

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add basket disposal endpoint
fix: correct profit margin calculation
docs: update API documentation
test: add unit tests for Money value object
refactor: improve repository queries
chore: update dependencies
```

### Pull Requests

1. Create feature branch from `develop`
2. Make changes
3. Run tests and ensure they pass
4. Create PR to `develop`
5. Code review
6. Merge after approval

## Troubleshooting

### Problema: Porta já em uso

```bash
# Verificar processo usando a porta 8080
lsof -i :8080

# Matar processo
kill -9 <PID>
```

### Problema: Database connection failed

```bash
# Verificar se PostgreSQL está rodando
docker ps | grep postgres

# Ver logs do container
docker logs <container_id>

# Reiniciar container
docker restart <container_id>
```

### Problema: Compilation errors com Lombok

```bash
# Limpar cache do Gradle
gradle clean

# Rebuild
gradle build --refresh-dependencies
```

## Recursos Úteis

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Gradle Docs](https://docs.gradle.org/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [RabbitMQ Docs](https://www.rabbitmq.com/documentation.html)
- [Project Lombok](https://projectlombok.org/)
- [MapStruct](https://mapstruct.org/)

## Contato

Para dúvidas ou sugestões, abra uma issue no GitHub ou entre em contato com o time de desenvolvimento.
