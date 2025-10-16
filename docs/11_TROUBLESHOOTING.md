# 🔧 Troubleshooting Guide

Guia completo para resolução de problemas comuns no Warehouse Management System.

**Última Atualização**: 15 de Outubro de 2025 - Novos problemas críticos resolvidos

## 📋 Table of Contents

1. [🆕 Critical Issues Resolved (Oct 15, 2025)](#-critical-issues-resolved-oct-15-2025)
2. [Java & Build Issues](#java--build-issues)
3. [Spring Boot & Dependencies](#spring-boot--dependencies)
4. [Docker Issues](#docker-issues)
5. [Database Issues](#database-issues)
6. [RabbitMQ Issues](#rabbitmq-issues)
7. [Application Startup Issues](#application-startup-issues)
8. [API & Authentication Issues](#api--authentication-issues)
9. [Performance Issues](#performance-issues)

---

## 🆕 Critical Issues Resolved (Oct 15, 2025)

Esta seção documenta os problemas críticos encontrados e resolvidos durante a validação completa do sistema em 15/10/2025.

### ❌ Problem 1: JWT Signing Key Too Short for HS512

**Symptom:**
```
HTTP 500 Internal Server Error on POST /api/v1/auth/login

io.jsonwebtoken.security.SignatureException: Unable to compute HS512 signature.
Cause: The signing key's size is 256 bits which is not secure enough for the HS512 algorithm.
The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HS512 
MUST have a size >= 512 bits (the key size must be greater than or equal to the hash output size).
```

**Root Cause:**
- JWT secret key in `.env` file was only 256 bits (32 bytes)
- HS512 algorithm requires minimum 512 bits (64 bytes) per RFC 7518
- Previous key: `4jGtX3li8bs15GWtBRnWyIAh7TACszCo0rSx0Qt1lNc=` (44 characters = 256 bits)

**Solution:**

1. Generate new 512-bit secure key:
```bash
openssl rand -base64 64 | tr -d '\n'
```

2. Update `.env` file:
```bash
# BEFORE (256 bits - INSECURE)
JWT_SECRET=4jGtX3li8bs15GWtBRnWyIAh7TACszCo0rSx0Qt1lNc=

# AFTER (512 bits - SECURE)
JWT_SECRET=/rBp+bVRCDamLJIRwc3cQ3HML/CRlM5dLGTbLsIzoLsMSECOE3wJVMOWrKhrYY/Z0bhz6GhlgVrmiKG1ul5cbw==
```

3. Reiniciar aplicação:
```bash
# Parar aplicação (Ctrl+C)
./run.sh
```

**Verificação:**
```bash
# Testar login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@2025!Secure"}'

# Deve retornar 200 OK com token válido
```

**Referência**: RFC 7518 Section 3.2 - HMAC with SHA-2 Functions

---

### ❌ Error: HTTP 403 Forbidden on /api/v1/auth/login

**Problema:**
```
Access to localhost was denied
You don't have the user rights to view this page.
HTTP ERROR 403
```

**Causa:**
Conflito entre `servlet.context-path: /api` no `application.yml` e os `@RequestMapping("/api/v1/...")` nos controllers, causando paths duplicados como `/api/api/v1/auth/login`.

**Solução:**

1. Comentar o context-path em `application.yml`:
```yaml
server:
  port: ${SERVER_PORT:8080}
  servlet:
    # context-path: /api  # Removed to avoid conflicts with @RequestMapping
```

2. Manter os controllers com `/api/v1/...`:
```java
@RestController
@RequestMapping("/api/v1/auth")  // ✅ Correto
public class AuthenticationController {
    // ...
}
```

3. Atualizar SecurityConfig para usar paths completos:
```java
.requestMatchers(
    "/api/v1/auth/**",        // ✅ Path completo
    "/actuator/health",
    "/swagger-ui/**"
).permitAll()
```

4. Recompilar e reiniciar:
```bash
./gradlew compileJava
./run.sh
```

**Verificação:**
```bash
# Testar endpoint
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@2025!Secure"}'

# Deve retornar 200 OK (não 403)
```

---

### ❌ Error: HTTP 500 on /api/v1/auth/me

**Problema:**
```json
{
  "status": 500,
  "type": "https://api.warehouse.com/errors/internal-server-error",
  "title": "Internal Server Error",
  "detail": "An unexpected error occurred..."
}
```

**Causa:**
O endpoint `GET /api/v1/auth/me` não existia no `AuthenticationController`.

**Solução:**

Adicionar método no `AuthenticationController.java`:
```java
@GetMapping("/me")
@Operation(summary = "Get Current User", description = "Get information about the currently authenticated user")
public ResponseEntity<JwtAuthenticationResponse> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(401).build();
    }
    
    String roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    
    JwtAuthenticationResponse response = JwtAuthenticationResponse.of(
            null,
            Instant.now().plusMillis(tokenProvider.getExpirationMs()),
            authentication.getName(),
            roles
    );
    
    return ResponseEntity.ok(response);
}
```

**Verificação:**
```bash
# Obter token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@2025!Secure"}' | jq -r '.token')

# Testar endpoint /me
curl -X GET http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"

# Deve retornar dados do usuário
```

---

### ❌ Error: NULL Constraint Violation on delivery_boxes

**Problema:**
```
SQL Error: 0, SQLState: 23502
ERROR: null value in column "unit_cost" of relation "delivery_boxes" violates not-null constraint
ERROR: null value in column "selling_price" of relation "delivery_boxes" violates not-null constraint
```

**Causa:**
Os valores `unit_cost` e `selling_price` não estavam sendo calculados antes de persistir a entidade `DeliveryBox`.

**Solução:**

Modificar `ReceiveDeliveryService.java`:
```java
@Override
@Transactional
public DeliveryBox execute(ReceiveDeliveryCommand command) {
    // Create delivery box
    Money totalCost = Money.of(command.totalCost());
    DeliveryBox deliveryBox = DeliveryBox.builder()
            .totalQuantity(command.totalQuantity())
            .validationDate(command.validationDate())
            .totalCost(totalCost)
            .profitMargin(command.profitMarginPercentage().doubleValue())
            .build();
    
    // ✅ CALCULAR unit_cost e selling_price ANTES de persistir
    Money unitCost = deliveryBox.calculateUnitCost();
    double marginAsDecimal = command.profitMarginPercentage().doubleValue() / 100.0;
    Money sellingPrice = deliveryBox.calculateSellingPrice(marginAsDecimal);
    
    // ✅ SETAR valores calculados
    deliveryBox.setUnitCost(unitCost);
    deliveryBox.setSellingPrice(sellingPrice);
    
    // Generate baskets
    deliveryBox.generateBaskets(sellingPrice);
    
    // Save (agora com todos os campos preenchidos)
    DeliveryBox savedDeliveryBox = deliveryBoxRepository.save(deliveryBox);
    
    // Publish event
    eventPublisher.publish(DeliveryReceivedEvent.of(
        savedDeliveryBox.getId(),
        command.totalQuantity()
    ));
    
    return savedDeliveryBox;
}
```

**Verificação:**
```bash
curl -X POST http://localhost:8080/api/v1/deliveries \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "totalQuantity": 50,
    "validationDate": "2025-10-15",
    "totalCost": 250.00,
    "profitMarginPercentage": 25.0
  }'

# Deve retornar 200 OK com unitCost e sellingPrice preenchidos
```

---

### ❌ Error: Jackson Cannot Deserialize RabbitMQ Events

**Problema:**
```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException: 
Cannot construct instance of `br.com.dio.warehouse.domain.event.DeliveryReceivedEvent` 
(no Creators, like default constructor, exist): cannot deserialize from Object value 
(no delegate- or property-based Creator)
```

**Causa:**
Classes de evento usando Lombok `@Value` sem anotações Jackson para desserialização.

**Solução:**

Adicionar `@JsonCreator` e `@JsonProperty` em TODOS os eventos:

**DeliveryReceivedEvent.java:**
```java
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
@Builder
public class DeliveryReceivedEvent {
    UUID eventId;
    Instant occurredOn;
    UUID deliveryBoxId;
    Long totalQuantity;

    @JsonCreator
    public DeliveryReceivedEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("occurredOn") Instant occurredOn,
            @JsonProperty("deliveryBoxId") UUID deliveryBoxId,
            @JsonProperty("totalQuantity") Long totalQuantity
    ) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.deliveryBoxId = deliveryBoxId;
        this.totalQuantity = totalQuantity;
    }

    public static DeliveryReceivedEvent of(UUID deliveryBoxId, Long totalQuantity) {
        return DeliveryReceivedEvent.builder()
            .eventId(UUID.randomUUID())
            .occurredOn(Instant.now())
            .deliveryBoxId(deliveryBoxId)
            .totalQuantity(totalQuantity)
            .build();
    }
}
```

Repetir para `BasketsSoldEvent.java` e `BasketsDisposedEvent.java`.

**Verificação:**
```bash
# Após publicar evento, verificar RabbitMQ Management
# http://localhost:15672
# Login: guest/guest
# Verificar que mensagens foram consumidas sem erro
```

---

### ❌ Error: Incorrect Profit Margin Calculation

**Problema:**
Preço de venda calculado incorretamente:
```json
{
  "unitCost": 5.00,
  "sellingPrice": 130.00,  // ❌ Esperado: 6.25
  "profitMarginPercentage": 25.0
}
```

**Cálculo Incorreto:**
```
5.00 × (1 + 25.0) = 5.00 × 26 = 130.00 ❌
```

**Causa:**
Margem sendo passada como `25.0` em vez de `0.25` (decimal).

**Solução:**

Converter percentual para decimal em `ReceiveDeliveryService.java`:
```java
// ❌ ANTES (margem como 25.0)
Money sellingPrice = deliveryBox.calculateSellingPrice(
    command.profitMarginPercentage().doubleValue()
);

// ✅ DEPOIS (margem como 0.25)
double marginAsDecimal = command.profitMarginPercentage().doubleValue() / 100.0;
Money sellingPrice = deliveryBox.calculateSellingPrice(marginAsDecimal);
```

**Cálculo Correto:**
```
25% → 0.25 (dividir por 100)
5.00 × (1 + 0.25) = 5.00 × 1.25 = 6.25 ✅
```

**Verificação:**
```bash
curl -X POST http://localhost:8080/api/v1/deliveries \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "totalQuantity": 50,
    "validationDate": "2025-10-15",
    "totalCost": 250.00,
    "profitMarginPercentage": 25.0
  }'

# Resposta esperada:
# "unitCost": 5.00
# "sellingPrice": 6.25  ✅
```

---

### ❌ Error: No Static Resource api/v1/baskets

**Problema:**
```
org.springframework.web.servlet.resource.NoResourceFoundException: 
No static resource api/v1/baskets.
```

**Causa:**
Spring tratando path `/api/v1/baskets` como recurso estático porque o endpoint `GET /api/v1/baskets` não existia.

**Solução:**

Adicionar endpoint no `BasketController.java`:
```java
@GetMapping
@Operation(summary = "Listar cestas", description = "Lista todas as cestas disponíveis no estoque")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
})
public ResponseEntity<String> listBaskets() {
    log.info("Listing all baskets");
    // TODO: Implementar listagem completa de cestas
    return ResponseEntity.ok("{\"message\":\"Basket listing endpoint - implementation pending\"}");
}
```

**Verificação:**
```bash
curl -X GET http://localhost:8080/api/v1/baskets \
  -H "Authorization: Bearer $TOKEN"

# Deve retornar 200 OK com mensagem placeholder
```

---

---

## Java & Build Issues

### ❌ Error: "Unsupported class file major version 69"

**Problema:**
```
Unsupported class file major version 69
```

**Causa:** 
Gradle não suporta Java 25 (class file version 69).

**Solução:**
```bash
# Atualizar para Gradle 9.1.0+
sdk install gradle 9.1.0
sdk use gradle 9.1.0

# Ou atualizar o wrapper
./gradlew wrapper --gradle-version=9.1.0 --distribution-type=bin
```

**Verificação:**
```bash
gradle --version
# Deve mostrar: Gradle 9.1.0+
```

---

### ❌ Error: "Could not determine java version from '25'"

**Problema:**
```
Could not determine java version from '25'
```

**Causa:** 
Versão antiga do Gradle não reconhece Java 25.

**Solução:**
Mesmo que o problema anterior - atualizar para Gradle 9.1.0+.

---

### ❌ Build falha com "OutOfMemoryError: Metaspace"

**Problema:**
```
java.lang.OutOfMemoryError: Metaspace
```

**Causa:** 
JVM sem memória suficiente para build.

**Solução:**

Adicionar em `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m
```

Ou via linha de comando:
```bash
./gradlew clean build -Dorg.gradle.jvmargs="-Xmx2g -XX:MaxMetaspaceSize=512m"
```

---

## Spring Boot & Dependencies

### ❌ Error: "Spring Boot [3.5.6] is not compatible with this Spring Cloud release train"

**Problema:**
```
Spring Boot [3.5.6] is not compatible with this Spring Cloud release train
Verify that you configured the correct Spring Cloud BOM version
```

**Causa:** 
Versão incompatível entre Spring Boot e Spring Cloud.

**Compatibilidade:**
| Spring Boot | Spring Cloud |
|------------|--------------|
| 3.4.x      | 2024.0.x     |
| 3.5.x      | 2025.0.x     |
| 3.6.x      | 2025.0.x     |

**Solução:**

Atualizar `build.gradle.kts`:
```kotlin
plugins {
    id("org.springframework.boot") version "3.5.6"
}

extra["springCloudVersion"] = "2025.0.0"
```

---

### ❌ Error: "'var buildDir: File' is deprecated"

**Problema:**
```
'var buildDir: File' is deprecated. Deprecated in Java
```

**Causa:** 
Gradle 9.x deprecou acesso direto ao `buildDir`.

**Solução:**

Substituir em `build.gradle.kts`:
```kotlin
// ❌ Antigo
"sonar.java.binaries" to "${project.buildDir}/classes/java/main"

// ✅ Novo
"sonar.java.binaries" to "${layout.buildDirectory.get()}/classes/java/main"
```

---

### ❌ Error: Module access warnings no Java 25

**Problema:**
```
WARNING: Illegal reflective access by ... to ...
```

**Causa:** 
Java 25 module system mais restritivo.

**Solução:**

Adicionar em `build.gradle.kts`:
```kotlin
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "--enable-preview",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    ))
}

tasks.withType<Test> {
    jvmArgs = listOf(
        "--enable-preview",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}
```

---

## Docker Issues

### ❌ Error: "docker: command not found"

**Problema:**
Docker não instalado ou não está no PATH.

**Solução:**

**Linux:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install docker.io docker-compose-plugin

# Adicionar usuário ao grupo docker
sudo usermod -aG docker $USER
newgrp docker
```

**Windows:**
1. Instalar Docker Desktop: https://www.docker.com/products/docker-desktop/
2. Habilitar WSL2 integration
3. Reiniciar o computador

**macOS:**
```bash
brew install --cask docker
# Abrir Docker Desktop e aguardar inicialização
```

---

### ❌ Error: "Cannot connect to the Docker daemon"

**Problema:**
```
Cannot connect to the Docker daemon at unix:///var/run/docker.sock
```

**Causa:** 
Docker daemon não está rodando.

**Solução:**

**Linux:**
```bash
sudo systemctl start docker
sudo systemctl enable docker
```

**Windows/macOS:**
Abrir Docker Desktop e aguardar inicialização.

---

### ❌ Error: "port is already allocated"

**Problema:**
```
Error starting userland proxy: listen tcp4 0.0.0.0:5432: bind: address already in use
```

**Causa:** 
Porta já está em uso por outro processo.

**Solução:**

**Identificar o processo:**
```bash
# Linux/macOS
sudo lsof -i :5432
sudo netstat -tulpn | grep :5432

# Windows (PowerShell)
netstat -ano | findstr :5432
```

**Parar o processo:**
```bash
# Linux/macOS
sudo kill -9 <PID>

# Windows (PowerShell como Admin)
taskkill /PID <PID> /F
```

**Ou alterar a porta no docker-compose.yml:**
```yaml
services:
  postgres:
    ports:
      - "5433:5432"  # Porta local alterada
```

---

### ❌ Error: "no space left on device"

**Problema:**
```
no space left on device
```

**Causa:** 
Disco cheio ou Docker consumindo muito espaço.

**Solução:**

**Verificar espaço em disco:**
```bash
df -h
docker system df
```

**Limpar recursos Docker não utilizados:**
```bash
# Remover containers parados
docker container prune -f

# Remover imagens não utilizadas
docker image prune -a -f

# Remover volumes não utilizados
docker volume prune -f

# Limpar tudo (CUIDADO!)
docker system prune -a --volumes -f
```

---

### ❌ Containers não iniciam após reboot

**Problema:**
Containers não iniciam automaticamente após reiniciar o sistema.

**Solução:**

**Adicionar restart policy no docker-compose.yml:**
```yaml
services:
  postgres:
    restart: unless-stopped
  
  rabbitmq:
    restart: unless-stopped
  
  pgadmin:
    restart: unless-stopped
```

**Ou iniciar manualmente:**
```bash
cd /path/to/project
docker-compose up -d
```

---

## Database Issues

### ❌ Error: "password authentication failed for user 'warehouse_user'"

**Problema:**
```
org.postgresql.util.PSQLException: FATAL: password authentication failed for user "warehouse_user"
```

**Causa:** 
Senha incorreta ou container PostgreSQL não reinicializado após mudança de senha.

**Solução:**

1. **Verificar senha no .env:**
```bash
cat .env | grep DB_PASSWORD
```

2. **Parar e remover volume PostgreSQL:**
```bash
docker-compose down
docker volume rm warehouse-franklindexdoo_postgres_data
```

3. **Recriar container:**
```bash
docker-compose up -d postgres
```

4. **Verificar conexão:**
```bash
docker exec -it warehouse-postgres psql -U warehouse_user -d warehouse_db -c "SELECT 1;"
```

---

### ❌ Error: "Flyway migration failed"

**Problema:**
```
org.flywaydb.core.api.FlywayException: Validate failed
```

**Causa:** 
Script de migração foi modificado após execução ou corrompido.

**Solução:**

**Verificar histórico de migrações:**
```sql
docker exec -it warehouse-postgres psql -U warehouse_user -d warehouse_db
SELECT * FROM flyway_schema_history;
```

**Opção 1 - Repair (recomendado):**
```bash
./gradlew flywayRepair
```

**Opção 2 - Reiniciar banco (desenvolvimento):**
```bash
docker-compose down
docker volume rm warehouse-franklindexdoo_postgres_data
docker-compose up -d postgres
./gradlew flywayMigrate
```

---

### ❌ Database connection timeout

**Problema:**
```
Connection to localhost:5432 refused
```

**Causa:** 
PostgreSQL ainda não está pronto para aceitar conexões.

**Solução:**

**Verificar health do container:**
```bash
docker-compose ps
# Status deve ser "Up (healthy)"
```

**Aguardar health check:**
```bash
until docker exec warehouse-postgres pg_isready -U warehouse_user; do
  echo "Aguardando PostgreSQL..."
  sleep 2
done
echo "PostgreSQL está pronto!"
```

---

## RabbitMQ Issues

### ❌ Error: "Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces"

**Problema:**
```
Error when reading /var/lib/rabbitmq/.erlang.cookie: eacces
```

**Causa:** 
Permissões incorretas no volume RabbitMQ.

**Solução:**

1. **Parar containers:**
```bash
docker-compose down
```

2. **Remover volumes RabbitMQ:**
```bash
docker volume rm warehouse-franklindexdoo_rabbitmq_data
docker volume rm warehouse-franklindexdoo_rabbitmq_logs
```

3. **Adicionar user no docker-compose.yml:**
```yaml
services:
  rabbitmq:
    user: rabbitmq:rabbitmq
    environment:
      RABBITMQ_ERLANG_COOKIE: "warehouse-secret-cookie"
```

4. **Recriar containers:**
```bash
docker-compose up -d
```

---

### ❌ Error: "Connection refused to localhost:5672"

**Problema:**
Aplicação não consegue conectar ao RabbitMQ.

**Causa:** 
RabbitMQ ainda não está pronto ou credenciais incorretas.

**Solução:**

**Verificar health:**
```bash
docker exec warehouse-rabbitmq rabbitmq-diagnostics ping
```

**Verificar credenciais no .env:**
```bash
cat .env | grep RABBITMQ
```

**Acessar management UI:**
```
http://localhost:15672
Usuário: guest
Senha: guest
```

**Verificar logs:**
```bash
docker logs warehouse-rabbitmq --tail 100
```

---

### ❌ Queues não são criadas automaticamente

**Problema:**
Aplicação inicia mas as filas RabbitMQ não são criadas.

**Causa:** 
Configuração RabbitMQ desabilitada ou erro na inicialização.

**Solução:**

**Verificar configuração no application.yml:**
```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USER:guest}
    password: ${RABBITMQ_PASSWORD:guest}
```

**Verificar beans de configuração:**
```bash
# Procurar por RabbitMQConfig no log de startup
grep -i "rabbitmqconfig" build/logs/spring.log
```

**Criar manualmente via management UI:**
1. Acessar http://localhost:15672
2. Aba "Queues and Streams" → Add a new queue
3. Nome: `warehouse.delivery`, Type: `Classic`, Durability: `Durable`

---

## Application Startup Issues

### ❌ Error: "Could not resolve placeholder 'DEV_ADMIN_PASSWORD'"

**Problema:**
```
java.lang.IllegalArgumentException: Could not resolve placeholder 'DEV_ADMIN_PASSWORD' in value "${DEV_ADMIN_PASSWORD}"
```

**Causa:** 
Variáveis de ambiente do .env não foram carregadas.

**Solução:**

**Opção 1 - Usar run.sh (recomendado):**
```bash
chmod +x run.sh
./run.sh
```

**Opção 2 - Exportar manualmente:**
```bash
set -a
source .env
set +a
./gradlew bootRun
```

**Opção 3 - Passar via linha de comando:**
```bash
./gradlew bootRun -DDEV_ADMIN_PASSWORD="Admin@2025!Secure"
```

---

### ❌ Startup muito lento (mais de 2 minutos)

**Problema:**
Aplicação demora muito para iniciar.

**Causa:** 
- Spring Boot detectando Docker Compose
- Hibernate gerando schema
- Flyway aplicando migrações
- Inicialização de beans pesados

**Solução:**

**Desabilitar Docker Compose detection (se não precisar):**
```yaml
# application-dev.yml
spring:
  docker:
    compose:
      enabled: false
```

**Otimizar JVM:**
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx2g -XX:TieredStopAtLevel=1
```

**Verificar logs para identificar gargalo:**
```bash
./gradlew bootRun --debug 2>&1 | grep -E "Starting|Started|Completed"
```

---

### ❌ Error: "Port 8080 is already in use"

**Problema:**
```
Web server failed to start. Port 8080 was already in use.
```

**Causa:** 
Outra aplicação já está usando a porta 8080.

**Solução:**

**Opção 1 - Parar processo existente:**
```bash
# Linux/macOS
sudo lsof -ti:8080 | xargs kill -9

# Windows (PowerShell)
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
```

**Opção 2 - Alterar porta da aplicação:**
```yaml
# application.yml
server:
  port: 8081
```

Ou via variável de ambiente:
```bash
SERVER_PORT=8081 ./run.sh
```

---

## API & Authentication Issues

### ❌ Error: "401 Unauthorized" em todos os endpoints

**Problema:**
```bash
curl http://localhost:8080/api/v1/baskets
# Retorna 401 Unauthorized
```

**Causa:** 
API protegida por Spring Security, requer autenticação.

**Solução:**

**1. Fazer login para obter token:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@2025!Secure"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600
}
```

**2. Usar token nas requisições:**
```bash
curl http://localhost:8080/api/v1/baskets \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### ❌ Error: "403 Forbidden" mesmo com token válido

**Problema:**
Token válido mas acesso negado a um endpoint específico.

**Causa:** 
Usuário não possui role necessária.

**Solução:**

**Verificar roles necessárias:**
```java
// Exemplo: Endpoint requer ROLE_ADMIN
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/reports")
```

**Usuários padrão e suas roles:**
| Username | Password | Roles |
|----------|----------|-------|
| admin | Admin@2025!Secure | ADMIN, MANAGER, SALES |
| manager | Manager@2025!Secure | MANAGER, SALES |
| sales | Sales@2025!Secure | SALES |

**Fazer login com usuário correto:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "Admin@2025!Secure"}'
```

---

### ❌ Swagger UI não abre (404)

**Problema:**
```
http://localhost:8080/swagger-ui.html → 404 Not Found
```

**Causa:** 
SpringDoc não configurado corretamente ou caminho alterado.

**Solução:**

**Verificar dependência no build.gradle.kts:**
```kotlin
dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
}
```

**Tentar caminhos alternativos:**
```bash
# SpringDoc 2.x usa este caminho por padrão
http://localhost:8080/swagger-ui/index.html

# Ou configurado em application.yml
http://localhost:8080/api-docs
```

**Verificar configuração:**
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---

## Performance Issues

### ❌ Alto consumo de memória

**Problema:**
Aplicação consumindo muita RAM (>2GB).

**Causa:** 
JVM não configurada para container ou memória não limitada.

**Solução:**

**Limitar memória no Dockerfile:**
```dockerfile
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=256m"
```

**Ou via docker-compose.yml:**
```yaml
services:
  app:
    image: warehouse-api:latest
    deploy:
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M
```

**Verificar consumo:**
```bash
docker stats warehouse-app
```

---

### ❌ Queries lentas no PostgreSQL

**Problema:**
Endpoints de listagem muito lentos.

**Causa:** 
Falta de índices ou queries N+1.

**Solução:**

**Analisar queries lentas:**
```sql
-- Habilitar log de queries lentas
docker exec -it warehouse-postgres psql -U warehouse_user -d warehouse_db
ALTER DATABASE warehouse_db SET log_min_duration_statement = 1000;

-- Verificar queries lentas
SELECT query, calls, total_exec_time, mean_exec_time
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

**Adicionar índices:**
```sql
-- Exemplo: índice em basket_id em deliveries
CREATE INDEX idx_deliveries_basket_id ON deliveries(basket_id);
```

**Otimizar queries N+1 com @EntityGraph:**
```java
@EntityGraph(attributePaths = {"products", "deliveries"})
List<Basket> findAll();
```

---

### ❌ RabbitMQ com muitas mensagens na DLQ

**Problema:**
Dead Letter Queue acumulando muitas mensagens.

**Causa:** 
- Consumidores falhando consistentemente
- Erros de serialização
- Timeout de processamento

**Solução:**

**Inspecionar mensagens na DLQ:**
```bash
# Via Management UI
http://localhost:15672/#/queues/%2F/warehouse.dlq

# Ou via CLI
docker exec warehouse-rabbitmq rabbitmqctl list_queues name messages
```

**Verificar logs de erro:**
```bash
./gradlew bootRun 2>&1 | grep -i "rabbitmq\|listener\|error"
```

**Reprocessar mensagens manualmente:**
1. Acessar http://localhost:15672
2. Queues → warehouse.dlq → Get Message(s)
3. Corrigir problema
4. Move messages back to original queue

---

## 📞 Suporte Adicional

Se o problema persistir:

1. **Verificar logs completos:**
```bash
./gradlew bootRun --info 2>&1 | tee application.log
docker-compose logs -f
```

2. **Verificar versões:**
```bash
java --version
gradle --version
docker --version
docker-compose --version
```

3. **Criar issue no GitHub:**
- Incluir logs completos
- Descrever passos para reproduzir
- Informar versões de Java, Gradle, Docker

4. **Consultar documentação:**
- [SETUP.md](../SETUP.md) - Guia de instalação
- [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) - Guia de deploy
- [DOCKER_ARCHITECTURE.md](DOCKER_ARCHITECTURE.md) - Arquitetura Docker

---

## 🔍 Comandos Úteis para Diagnóstico

```bash
# Verificar status de todos os serviços
docker-compose ps

# Logs de todos os containers
docker-compose logs -f --tail=100

# Logs de um container específico
docker logs warehouse-postgres --tail=100 -f

# Inspecionar container
docker inspect warehouse-postgres

# Verificar rede Docker
docker network inspect warehouse-franklindexdoo_warehouse-network

# Listar volumes
docker volume ls

# Executar comando em container rodando
docker exec -it warehouse-postgres bash

# Testar conectividade entre containers
docker exec warehouse-app ping postgres

# Verificar uso de recursos
docker stats

# Clean build completo
./gradlew clean build --refresh-dependencies --info

# Rebuild de imagem Docker
docker-compose build --no-cache app

# Restart completo
docker-compose down -v
docker-compose up -d --build
```

---

**Última atualização:** Janeiro 2025  
**Versões cobertas:** Java 25, Spring Boot 3.5.6, Gradle 9.1.0, Docker 28.x
