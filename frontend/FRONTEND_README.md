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

---

Desenvolvido por **Franklin Canduri** 🚀
