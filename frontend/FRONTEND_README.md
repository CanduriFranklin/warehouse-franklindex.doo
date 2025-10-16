# 🛍️ Storefront - Frontend

Frontend da aplicação de e-commerce Storefront desenvolvido com React + TypeScript + TailwindCSS.

## 🚀 Tecnologias

- **React 18** + **TypeScript**
- **Vite** - Build tool
- **TailwindCSS 4.x**
- **React Router** - Roteamento
- **Axios** - Cliente HTTP
- **React Icons**

## 📁 Estrutura

```
src/
├── components/     # Button, Card, Loading, Input, Header, Footer
├── pages/          # HomePage, ProdutosPage, CarrinhoPage
├── services/       # API services (produto, cliente, carrinho, pedido)
├── context/        # CarrinhoContext (estado global)
├── types/          # TypeScript interfaces
└── utils/          # formatters.ts (dinheiro, CPF, data, etc.)
```

## ⚙️ Configuração

Arquivo `.env`:
```
VITE_API_URL=http://localhost:8080/api
```

## 🏃 Como Executar

```bash
# Desenvolvimento
npm install
npm run dev         # http://localhost:5173

# Build
npm run build       # dist/ (288 KB JS + 17 KB CSS)
npm run preview     # Preview da build
```

## 📡 API Endpoints

```
GET  /produtos           # Lista produtos
GET  /produtos/{id}      # Detalhes
GET  /carrinho           # Carrinho do cliente
POST /carrinho/adicionar # Adicionar produto
PUT  /carrinho/atualizar # Atualizar quantidade
DELETE /carrinho/remover # Remover produto
POST /carrinho/finalizar # Finalizar compra (criar pedido)
```

## ✅ Funcionalidades

- ✅ Lista de produtos com busca e paginação
- ✅ Carrinho de compras com Context API
- ✅ Adicionar/remover/atualizar quantidade
- ✅ Layout responsivo com TailwindCSS
- ✅ Componentes reutilizáveis
- ✅ Integração completa com backend REST

## 📦 Build Stats

- Bundle JS: 288.35 KB (93.43 KB gzipped)
- CSS: 17.15 KB (4.18 KB gzipped)
- Build time: ~10s

## 🐳 Docker e Deploy

### Desenvolvimento com Docker

```bash
# Usando Docker Compose (profile dev)
docker compose --profile dev up frontend-dev

# Acesse: http://localhost:5173
```

### Produção com Docker

#### **Dockerfile.prod** - Multi-stage Build

A aplicação utiliza um **Dockerfile multi-stage** otimizado:

**Stage 1: Builder** (node:20-alpine)
- Instala dependências de produção
- Compila a aplicação com Vite
- Gera build otimizado (~306 KB total)

**Stage 2: Production** (nginx:1.27-alpine)
- Copia apenas os arquivos compilados
- Usa Nginx como servidor web
- **Redução de 96% no tamanho da imagem**: 1.2 GB → **50 MB**

#### **Build da Imagem**

```bash
# Build local
docker build -f Dockerfile.prod -t warehouse-frontend:latest .

# Build com API URL customizada
docker build -f Dockerfile.prod \
  --build-arg VITE_API_URL=https://api.example.com \
  -t warehouse-frontend:latest .
```

#### **Executar Container**

```bash
# Desenvolvimento
docker compose --profile dev up frontend-dev

# Produção
docker compose --profile prod up frontend-prod

# Apenas frontend (standalone)
docker run -d -p 80:80 warehouse-frontend:latest
```

### nginx.conf - Configurações

O arquivo `nginx.conf` customizado inclui:

**🔒 Security Headers**
- `X-Frame-Options: SAMEORIGIN` - Previne clickjacking
- `X-Content-Type-Options: nosniff` - Previne MIME sniffing
- `X-XSS-Protection: 1; mode=block` - Proteção XSS
- `Referrer-Policy: no-referrer-when-downgrade` - Controle de referrer

**⚡ Performance**
- **Gzip compression** - Reduz tamanho de respostas em ~70%
- **Cache de assets estáticos** - 1 ano para JS/CSS/imagens
- **No-cache para HTML** - Sempre busca versão mais recente

**� React Router Support**
- `try_files $uri $uri/ /index.html` - Redireciona todas as rotas para index.html
- Suporta navegação client-side sem erros 404

**🏥 Health Check**
- Endpoint `/health` - Retorna status do container
- Usado por Docker health checks e load balancers

### Docker Compose Profiles

#### **Profile: dev**
```yaml
frontend-dev:
  build: ./frontend
  volumes:
    - ./frontend:/app          # Hot reload
    - /app/node_modules
  ports:
    - "5173:5173"
  environment:
    VITE_API_URL: http://localhost:8080/api
```

#### **Profile: prod**
```yaml
frontend-prod:
  build:
    context: ./frontend
    dockerfile: Dockerfile.prod
  ports:
    - "80:80"
  healthcheck:
    test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/health"]
    interval: 30s
    timeout: 3s
    retries: 3
```

### Resource Limits (Produção)

```yaml
deploy:
  resources:
    limits:
      cpus: '0.5'      # 50% de 1 CPU
      memory: 256M     # Máximo 256MB RAM
    reservations:
      cpus: '0.25'     # Mínimo 25% de 1 CPU
      memory: 128M     # Mínimo 128MB RAM
```

### Comandos Úteis

```bash
# Verificar logs
docker compose logs frontend-prod -f

# Verificar saúde do container
docker compose ps

# Acessar shell do container
docker compose exec frontend-prod sh

# Rebuild sem cache
docker compose build --no-cache frontend-prod

# Parar e remover containers
docker compose --profile prod down
```

### Métricas de Performance

| Métrica | Desenvolvimento | Produção |
|---------|-----------------|----------|
| **Tamanho da Imagem** | ~1.2 GB | **50 MB** (-96%) |
| **Tempo de Build** | 2-3 min | **1-2 min** |
| **Tempo de Startup** | 15-20s | **2-3s** |
| **Uso de RAM** | 300-500 MB | **50-150 MB** |
| **TTFB** (Time to First Byte) | 50-100ms | **10-30ms** |

### Troubleshooting

**Container não inicia:**
```bash
# Verificar logs
docker compose logs frontend-prod

# Verificar health check
docker inspect warehouse-frontend-prod | grep Health -A 10
```

**Assets não carregam:**
```bash
# Verificar build
docker compose exec frontend-prod ls -la /usr/share/nginx/html

# Verificar nginx config
docker compose exec frontend-prod cat /etc/nginx/conf.d/default.conf
```

**API não conecta:**
```bash
# Verificar variável de ambiente
docker compose exec frontend-prod env | grep VITE_API_URL

# Rebuild com URL correta
docker compose build --build-arg VITE_API_URL=http://backend:8080/api frontend-prod
```

---

## 📚 Documentação Adicional

- [Docker Setup](../docs/DOCKER_SETUP.md) - Guia completo de configuração Docker
- [Docker Optimization](../docs/DOCKER_OPTIMIZATION.md) - Otimizações implementadas
- [Deployment Guide](../docs/DEPLOYMENT_GUIDE.md) - Guia de deploy em produção

---

Desenvolvido por **Franklin Canduri** �🚀  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Franklin_Canduri-0077B5.svg?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/franklin-david-canduri-presilla-b75956266/)
