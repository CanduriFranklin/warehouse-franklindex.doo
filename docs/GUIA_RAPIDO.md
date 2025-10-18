# 🎯 Guia Rápido de Acesso - Warehouse Project

**Status:** ✅ Todos os serviços operacionais  
**Data:** 16 de Outubro de 2025

---

## 🌐 URLs de Acesso

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **Frontend** | http://localhost | React app (estrutura pronta) |
| **Backend API** | http://localhost:8080 | Spring Boot REST API |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | Documentação interativa da API |
| **Actuator Health** | http://localhost:8080/actuator/health | Status de saúde da aplicação |
| **pgAdmin** | http://localhost:5050 | Interface web PostgreSQL |
| **RabbitMQ Management** | http://localhost:15672 | Console de gerenciamento RabbitMQ |

---

## 🔑 Credenciais de Acesso

### API Backend (JWT Authentication)

**Endpoint:** `POST http://localhost:8080/api/v1/auth/login`

```bash
# Login como ADMIN
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@2025!Secure"}'

# Login como MANAGER
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"Manager@2025!Secure"}'

# Login como SALES
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"sales","password":"Sales@2025!Secure"}'
```

**Usuários de Teste:**
- `admin` / `Admin@2025!Secure` (Acesso total)
- `manager` / `Manager@2025!Secure` (Gerenciamento)
- `sales` / `Sales@2025!Secure` (Vendas)

### pgAdmin
- **Email:** `admin@warehouse.com`
- **Senha:** `admin123`

**Conexão com PostgreSQL no pgAdmin:**
- Host: `postgres` (ou `localhost` se conectar de fora do Docker)
- Port: `5432`
- Database: `warehouse_db`
- Username: `admin`
- Password: `admin123`

### RabbitMQ Management
- **Username:** `admin`
- **Password:** `admin123`

### PostgreSQL (Conexão Direta)
- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `warehouse_db`
- **Username:** `admin`
- **Password:** `admin123`

```bash
# Conectar via psql
psql -h localhost -p 5432 -U admin -d warehouse_db

# Conectar via DBeaver, DataGrip, etc.
jdbc:postgresql://localhost:5432/warehouse_db
```

---

## 📊 Dados de Teste (Seed Data)

### Produtos (10 produtos cadastrados)

| ID | Nome | Categoria | Preço | Estoque |
|----|------|-----------|-------|---------|
| 01111111... | Notebook Dell | Eletrônicos | R$ 3.500,00 | 15 |
| 02222222... | Mouse Logitech | Eletrônicos | R$ 150,00 | 50 |
| 03333333... | Teclado Mecânico | Eletrônicos | R$ 450,00 | 30 |
| 04444444... | Monitor LG 24" | Eletrônicos | R$ 800,00 | 20 |
| 05555555... | Headset Gamer | Eletrônicos | R$ 350,00 | 25 |
| 06666666... | Webcam Full HD | Eletrônicos | R$ 250,00 | 40 |
| 07777777... | SSD 500GB | Eletrônicos | R$ 300,00 | 60 |
| 08888888... | Pendrive 64GB | Eletrônicos | R$ 50,00 | 100 |
| 09999999... | Hub USB 7 Portas | Eletrônicos | R$ 80,00 | 45 |
| 0aaaaaaa... | Mousepad Gamer | Eletrônicos | R$ 70,00 | 80 |

### Clientes (3 clientes cadastrados)

| ID | Nome | Email |
|----|------|-------|
| 01111111... | Franklin Canduri | franklin@example.com |
| 02222222... | Maria Silva | maria.silva@example.com |
| 03333333... | João Santos | joao.santos@example.com |

---

## 🔧 Comandos Úteis

### Docker Compose

```bash
# Iniciar todos os serviços
docker compose up -d

# Parar todos os serviços
docker compose down

# Ver logs em tempo real
docker compose logs -f

# Ver logs de um serviço específico
docker compose logs -f warehouse
docker compose logs -f postgres
docker compose logs -f frontend

# Verificar status dos containers
docker compose ps

# Reiniciar um serviço específico
docker compose restart warehouse

# Rebuild e restart (útil após mudanças no código)
docker compose build --no-cache warehouse
docker compose up -d warehouse
```

### Backend (Gradle)

```bash
cd backend

# Compilar
./gradlew build

# Rodar testes
./gradlew test

# Rodar localmente (fora do Docker)
./gradlew bootRun

# Limpar build
./gradlew clean
```

### Frontend (npm)

```bash
cd frontend

# Instalar dependências
npm install

# Rodar em modo desenvolvimento
npm run dev

# Build de produção
npm run build

# Preview do build
npm run preview
```

---

## 🧪 Testando a API

### 1. Health Check

```bash
curl http://localhost:8080/actuator/health
```

**Resposta esperada:**
```json
{"status":"UP"}
```

### 2. Login e Obter Token JWT

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@2025!Secure"}'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "admin",
  "roles": "ROLE_ADMIN"
}
```

### 3. Listar Produtos (Autenticado)

```bash
# Substitua YOUR_TOKEN_HERE pelo token recebido no login
curl -X GET http://localhost:8080/api/v1/produtos \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 4. Criar Produto (Apenas ADMIN)

```bash
curl -X POST http://localhost:8080/api/v1/produtos \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Produto Teste",
    "descricao": "Descrição do produto teste",
    "preco": 99.90,
    "categoria": "Teste",
    "quantidadeEstoque": 10
  }'
```

### 5. Adicionar Item ao Carrinho

```bash
curl -X POST http://localhost:8080/api/v1/carrinhos/itens \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "produtoId": "01111111-1111-1111-1111-111111111111",
    "quantidade": 2
  }'
```

---

## 📚 Endpoints Disponíveis (14 endpoints)

### Autenticação (Público)
- `POST /api/v1/auth/register` - Registrar novo usuário
- `POST /api/v1/auth/login` - Login e obter JWT

### Produtos (Autenticado)
- `GET /api/v1/produtos` - Listar todos os produtos
- `GET /api/v1/produtos/{id}` - Buscar produto por ID
- `POST /api/v1/produtos` - Criar novo produto (ADMIN)
- `PUT /api/v1/produtos/{id}` - Atualizar produto (ADMIN)
- `DELETE /api/v1/produtos/{id}` - Deletar produto (ADMIN)
- `GET /api/v1/produtos/categoria/{categoria}` - Buscar por categoria

### Carrinhos (Autenticado)
- `GET /api/v1/carrinhos` - Obter carrinho do usuário
- `POST /api/v1/carrinhos/itens` - Adicionar item ao carrinho
- `PUT /api/v1/carrinhos/itens/{produtoId}` - Atualizar quantidade
- `DELETE /api/v1/carrinhos/itens/{produtoId}` - Remover item
- `DELETE /api/v1/carrinhos` - Limpar carrinho

### Clientes (Autenticado)
- `GET /api/v1/clientes/{id}` - Buscar cliente por ID

---

## 🛠️ Troubleshooting

### Serviço não inicia

```bash
# Ver logs detalhados
docker compose logs warehouse

# Verificar se as portas estão em uso
sudo netstat -tulpn | grep -E ':(80|8080|5432|5672|15672|5050)'

# Reiniciar apenas o serviço problemático
docker compose restart warehouse
```

### Erro de conexão com banco de dados

```bash
# Verificar se PostgreSQL está rodando
docker compose ps postgres

# Verificar logs do PostgreSQL
docker compose logs postgres

# Testar conexão
docker compose exec postgres psql -U admin -d warehouse_db -c "SELECT 1;"
```

### Erro de autenticação JWT

```bash
# Verificar variáveis de ambiente
docker compose exec warehouse env | grep JWT

# Verificar se o usuário existe no banco
docker compose exec postgres psql -U admin -d warehouse_db \
  -c "SELECT username, enabled FROM users;"
```

### Frontend não carrega

```bash
# Verificar se o container está rodando
docker compose ps frontend

# Ver logs do nginx
docker compose logs frontend

# Testar se o HTML está sendo servido
curl -I http://localhost
```

### RabbitMQ não conecta

```bash
# Verificar status
docker compose ps rabbitmq

# Ver logs
docker compose logs rabbitmq

# Testar conectividade
curl http://localhost:15672
```

---

## 📁 Estrutura do Projeto

```
warehouse-franklindex.doo/
├── backend/                    # Spring Boot API
│   ├── src/main/java/         # Código fonte
│   ├── src/main/resources/    # application.yml, migrations
│   └── build.gradle.kts       # Dependências Gradle
├── frontend/                   # React TypeScript
│   ├── src/                   # Código fonte
│   │   ├── pages/            # Páginas React
│   │   ├── components/       # Componentes reutilizáveis
│   │   └── services/         # Serviços de API
│   └── package.json          # Dependências npm
├── docker-compose.yml         # Orquestração de containers
└── docs/                      # Documentação
    ├── PROXIMOS_PASSOS.md    # Roadmap de implementação
    └── GUIA_RAPIDO.md        # Este arquivo
```

---

## 🎯 Estado Atual do Projeto

### ✅ Completamente Funcional
- Backend API com todos os endpoints
- Autenticação JWT
- PostgreSQL com dados seed
- RabbitMQ configurado
- Docker Compose funcionando
- pgAdmin para gerenciar banco
- Swagger UI para testar API

### 🚧 Estrutura Pronta (Precisa Implementar UI)
- Frontend React + TypeScript
- Serviços de API (api.ts, produtoService.ts, etc.)
- Páginas (HomePage, ProdutosPage, CarrinhoPage)
- TailwindCSS configurado

### 📝 Para Fazer (Ver `PROXIMOS_PASSOS.md`)
- Conectar páginas aos serviços
- Implementar componentes de UI
- Adicionar autenticação na UI
- Relatórios no backend
- CI/CD
- Deploy em cloud
- Monitoramento

---

## 📞 Referências Rápidas

**Swagger UI:** Para testar qualquer endpoint interativamente  
http://localhost:8080/swagger-ui/index.html

**pgAdmin:** Para explorar o banco de dados  
http://localhost:5050

**RabbitMQ:** Para monitorar filas de mensagens  
http://localhost:15672

**Próximos Passos:** Roadmap completo de implementação  
[docs/PROXIMOS_PASSOS.md](./PROXIMOS_PASSOS.md)

---

**Projeto funcionando perfeitamente! 🎉**  
**Pronto para continuar o desenvolvimento!**
