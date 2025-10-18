# 🚀 Próximos Passos - Roadmap de Implementação

**Data:** 16 de Outubro de 2025  
**Status Atual:** ✅ Backend 100% funcional, Frontend estrutura pronta  
**Objetivo:** Guia completo para continuar o desenvolvimento

---

## 📊 Status Atual do Projeto

### ✅ **O Que Já Está Pronto**

#### Backend (Warehouse API)
- ✅ Spring Boot 3.5.6 + Java 25 LTS
- ✅ PostgreSQL 16 com Flyway migrations
- ✅ RabbitMQ 3.13 para mensageria
- ✅ JWT Authentication funcionando
- ✅ 14 endpoints REST operacionais
- ✅ Swagger UI para testes
- ✅ Docker Compose configurado
- ✅ Dados seed (produtos, clientes, carrinhos)

#### Frontend (React)
- ✅ React 18 + TypeScript 5.6
- ✅ Vite 7.0 build tool
- ✅ TailwindCSS 4.0
- ✅ Estrutura de pastas organizada
- ✅ Serviços de API criados
- ✅ Axios configurado
- ✅ Docker nginx configurado

#### Infraestrutura
- ✅ PostgreSQL rodando (porta 5432)
- ✅ RabbitMQ rodando (portas 5672, 15672)
- ✅ pgAdmin funcionando (porta 5050)
- ✅ Backend rodando (porta 8080)
- ✅ Frontend rodando (porta 80)

---

## 🎯 Fase 1: Implementar UI do Frontend (Prioridade ALTA)

### 1.1 Conectar Páginas aos Serviços de API

**Arquivos a Modificar:**
```
frontend/src/pages/HomePage.tsx
frontend/src/pages/ProdutosPage.tsx
frontend/src/pages/CarrinhoPage.tsx
```

#### **HomePage.tsx - Dashboard Inicial**
```typescript
import { useEffect, useState } from 'react';
import { produtoService } from '../services/produtoService';

export default function HomePage() {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function carregarProdutos() {
      try {
        const response = await produtoService.listarProdutos();
        setProdutos(response.data.slice(0, 6)); // Top 6 produtos
      } catch (error) {
        console.error('Erro ao carregar produtos:', error);
      } finally {
        setLoading(false);
      }
    }
    carregarProdutos();
  }, []);

  if (loading) return <div>Carregando...</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-4xl font-bold mb-8">Bem-vindo à Loja</h1>
      
      <section className="mb-12">
        <h2 className="text-2xl font-semibold mb-4">Produtos em Destaque</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {produtos.map((produto) => (
            <div key={produto.id} className="border rounded-lg p-4 shadow-md">
              <h3 className="text-xl font-semibold">{produto.nome}</h3>
              <p className="text-gray-600 mt-2">{produto.descricao}</p>
              <p className="text-2xl font-bold text-green-600 mt-4">
                R$ {produto.preco.toFixed(2)}
              </p>
              <button className="mt-4 w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700">
                Adicionar ao Carrinho
              </button>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
```

**Tempo Estimado:** 2-3 horas

---

### 1.2 Criar Componentes de Listagem de Produtos

**Criar Arquivo:** `frontend/src/components/ProdutoCard.tsx`

```typescript
import { useState } from 'react';
import { carrinhoService } from '../services/carrinhoService';

interface Produto {
  id: string;
  nome: string;
  descricao: string;
  preco: number;
  categoria: string;
  quantidadeEstoque: number;
  ativo: boolean;
}

export default function ProdutoCard({ produto }: { produto: Produto }) {
  const [quantidade, setQuantidade] = useState(1);
  const [loading, setLoading] = useState(false);

  const adicionarAoCarrinho = async () => {
    setLoading(true);
    try {
      await carrinhoService.adicionarItem({
        produtoId: produto.id,
        quantidade: quantidade
      });
      alert('Produto adicionado ao carrinho!');
    } catch (error) {
      console.error('Erro ao adicionar ao carrinho:', error);
      alert('Erro ao adicionar produto');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow">
      {/* Imagem do produto (placeholder) */}
      <div className="h-48 bg-gray-200 flex items-center justify-center">
        <span className="text-gray-400 text-6xl">📦</span>
      </div>
      
      {/* Conteúdo */}
      <div className="p-4">
        <span className="text-xs text-gray-500 uppercase">{produto.categoria}</span>
        <h3 className="text-lg font-semibold mt-1 line-clamp-2">{produto.nome}</h3>
        <p className="text-sm text-gray-600 mt-2 line-clamp-3">{produto.descricao}</p>
        
        {/* Estoque */}
        <div className="mt-3">
          {produto.quantidadeEstoque > 0 ? (
            <span className="text-sm text-green-600">
              ✓ {produto.quantidadeEstoque} em estoque
            </span>
          ) : (
            <span className="text-sm text-red-600">✗ Fora de estoque</span>
          )}
        </div>
        
        {/* Preço */}
        <div className="mt-4 flex items-center justify-between">
          <span className="text-2xl font-bold text-gray-900">
            R$ {produto.preco.toFixed(2)}
          </span>
        </div>
        
        {/* Quantidade e Botão */}
        <div className="mt-4 flex gap-2">
          <input
            type="number"
            min="1"
            max={produto.quantidadeEstoque}
            value={quantidade}
            onChange={(e) => setQuantidade(Number(e.target.value))}
            className="w-16 border rounded px-2 py-1 text-center"
            disabled={produto.quantidadeEstoque === 0}
          />
          <button
            onClick={adicionarAoCarrinho}
            disabled={loading || produto.quantidadeEstoque === 0}
            className="flex-1 bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
          >
            {loading ? 'Adicionando...' : 'Adicionar ao Carrinho'}
          </button>
        </div>
      </div>
    </div>
  );
}
```

**Criar Arquivo:** `frontend/src/components/ProdutoLista.tsx`

```typescript
import { useEffect, useState } from 'react';
import { produtoService } from '../services/produtoService';
import ProdutoCard from './ProdutoCard';

export default function ProdutoLista() {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [categorias, setCategorias] = useState<string[]>([]);

  useEffect(() => {
    carregarProdutos();
  }, []);

  const carregarProdutos = async () => {
    try {
      const response = await produtoService.listarProdutos();
      setProdutos(response.data);
      
      // Extrair categorias únicas
      const cats = [...new Set(response.data.map(p => p.categoria))];
      setCategorias(cats);
    } catch (error) {
      console.error('Erro ao carregar produtos:', error);
    } finally {
      setLoading(false);
    }
  };

  const produtosFiltrados = filtroCategoria
    ? produtos.filter(p => p.categoria === filtroCategoria)
    : produtos;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-xl text-gray-600">Carregando produtos...</div>
      </div>
    );
  }

  return (
    <div>
      {/* Filtros */}
      <div className="mb-6 flex gap-2">
        <button
          onClick={() => setFiltroCategoria('')}
          className={`px-4 py-2 rounded ${!filtroCategoria ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}
        >
          Todos
        </button>
        {categorias.map(cat => (
          <button
            key={cat}
            onClick={() => setFiltroCategoria(cat)}
            className={`px-4 py-2 rounded ${filtroCategoria === cat ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Grid de Produtos */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {produtosFiltrados.map(produto => (
          <ProdutoCard key={produto.id} produto={produto} />
        ))}
      </div>

      {produtosFiltrados.length === 0 && (
        <div className="text-center py-12 text-gray-600">
          Nenhum produto encontrado nesta categoria.
        </div>
      )}
    </div>
  );
}
```

**Tempo Estimado:** 3-4 horas

---

### 1.3 Implementar Carrinho de Compras Funcional

**Criar Context:** `frontend/src/context/CarrinhoContext.tsx`

```typescript
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { carrinhoService } from '../services/carrinhoService';

interface ItemCarrinho {
  id: string;
  produtoId: string;
  produtoNome: string;
  produtoPreco: number;
  quantidade: number;
  subtotal: number;
}

interface CarrinhoContextType {
  itens: ItemCarrinho[];
  total: number;
  loading: boolean;
  adicionarItem: (produtoId: string, quantidade: number) => Promise<void>;
  removerItem: (produtoId: string) => Promise<void>;
  atualizarQuantidade: (produtoId: string, quantidade: number) => Promise<void>;
  limparCarrinho: () => Promise<void>;
  recarregar: () => Promise<void>;
}

const CarrinhoContext = createContext<CarrinhoContextType | undefined>(undefined);

export function CarrinhoProvider({ children }: { children: ReactNode }) {
  const [itens, setItens] = useState<ItemCarrinho[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarCarrinho();
  }, []);

  const carregarCarrinho = async () => {
    try {
      const response = await carrinhoService.obterCarrinho();
      setItens(response.data.itens || []);
    } catch (error) {
      console.error('Erro ao carregar carrinho:', error);
    } finally {
      setLoading(false);
    }
  };

  const adicionarItem = async (produtoId: string, quantidade: number) => {
    await carrinhoService.adicionarItem({ produtoId, quantidade });
    await carregarCarrinho();
  };

  const removerItem = async (produtoId: string) => {
    await carrinhoService.removerItem(produtoId);
    await carregarCarrinho();
  };

  const atualizarQuantidade = async (produtoId: string, quantidade: number) => {
    await carrinhoService.atualizarQuantidade(produtoId, quantidade);
    await carregarCarrinho();
  };

  const limparCarrinho = async () => {
    await carrinhoService.limparCarrinho();
    setItens([]);
  };

  const total = itens.reduce((sum, item) => sum + item.subtotal, 0);

  return (
    <CarrinhoContext.Provider value={{
      itens,
      total,
      loading,
      adicionarItem,
      removerItem,
      atualizarQuantidade,
      limparCarrinho,
      recarregar: carregarCarrinho
    }}>
      {children}
    </CarrinhoContext.Provider>
  );
}

export function useCarrinho() {
  const context = useContext(CarrinhoContext);
  if (!context) {
    throw new Error('useCarrinho deve ser usado dentro de CarrinhoProvider');
  }
  return context;
}
```

**Atualizar:** `frontend/src/pages/CarrinhoPage.tsx`

```typescript
import { useCarrinho } from '../context/CarrinhoContext';
import { useNavigate } from 'react-router-dom';

export default function CarrinhoPage() {
  const { itens, total, loading, removerItem, atualizarQuantidade } = useCarrinho();
  const navigate = useNavigate();

  if (loading) return <div className="text-center py-12">Carregando...</div>;

  if (itens.length === 0) {
    return (
      <div className="container mx-auto px-4 py-12 text-center">
        <h1 className="text-3xl font-bold mb-4">Seu carrinho está vazio</h1>
        <p className="text-gray-600 mb-8">Adicione produtos para começar suas compras!</p>
        <button
          onClick={() => navigate('/produtos')}
          className="bg-blue-600 text-white px-6 py-3 rounded hover:bg-blue-700"
        >
          Ver Produtos
        </button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-8">Meu Carrinho</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Lista de Itens */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-lg shadow">
            {itens.map(item => (
              <div key={item.id} className="p-6 border-b last:border-b-0">
                <div className="flex items-center gap-4">
                  <div className="w-20 h-20 bg-gray-200 rounded flex items-center justify-center">
                    <span className="text-3xl">📦</span>
                  </div>
                  
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg">{item.produtoNome}</h3>
                    <p className="text-gray-600">R$ {item.produtoPreco.toFixed(2)}</p>
                  </div>

                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => atualizarQuantidade(item.produtoId, item.quantidade - 1)}
                      disabled={item.quantidade <= 1}
                      className="w-8 h-8 border rounded hover:bg-gray-100 disabled:opacity-50"
                    >
                      -
                    </button>
                    <span className="w-12 text-center">{item.quantidade}</span>
                    <button
                      onClick={() => atualizarQuantidade(item.produtoId, item.quantidade + 1)}
                      className="w-8 h-8 border rounded hover:bg-gray-100"
                    >
                      +
                    </button>
                  </div>

                  <div className="text-right">
                    <p className="font-bold text-lg">R$ {item.subtotal.toFixed(2)}</p>
                  </div>

                  <button
                    onClick={() => removerItem(item.produtoId)}
                    className="text-red-600 hover:text-red-800"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Resumo */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow p-6 sticky top-4">
            <h2 className="text-xl font-bold mb-4">Resumo do Pedido</h2>
            
            <div className="space-y-2 mb-4">
              <div className="flex justify-between">
                <span>Subtotal:</span>
                <span>R$ {total.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Frete:</span>
                <span className="text-green-600">Grátis</span>
              </div>
            </div>

            <div className="border-t pt-4 mb-6">
              <div className="flex justify-between text-xl font-bold">
                <span>Total:</span>
                <span>R$ {total.toFixed(2)}</span>
              </div>
            </div>

            <button
              onClick={() => navigate('/checkout')}
              className="w-full bg-green-600 text-white py-3 rounded hover:bg-green-700 font-semibold"
            >
              Finalizar Compra
            </button>

            <button
              onClick={() => navigate('/produtos')}
              className="w-full mt-2 border border-gray-300 py-3 rounded hover:bg-gray-50"
            >
              Continuar Comprando
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
```

**Tempo Estimado:** 4-5 horas

---

### 1.4 Adicionar Autenticação na UI

**Criar:** `frontend/src/context/AuthContext.tsx`

```typescript
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import axios from 'axios';

interface User {
  username: string;
  roles: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verificar se já tem token salvo
    const savedToken = localStorage.getItem('auth_token');
    const savedUser = localStorage.getItem('user_data');
    
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = async (username: string, password: string) => {
    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
        username,
        password
      });

      const { token: authToken, username: userName, roles } = response.data;
      
      setToken(authToken);
      setUser({ username: userName, roles });
      
      localStorage.setItem('auth_token', authToken);
      localStorage.setItem('user_data', JSON.stringify({ username: userName, roles }));
    } catch (error) {
      console.error('Erro no login:', error);
      throw new Error('Credenciais inválidas');
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_data');
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      loading,
      login,
      logout,
      isAuthenticated: !!token
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }
  return context;
}
```

**Criar:** `frontend/src/pages/LoginPage.tsx`

```typescript
import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(username, password);
      navigate('/');
    } catch (err) {
      setError('Usuário ou senha incorretos');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
        <h1 className="text-3xl font-bold text-center mb-8">Login</h1>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-700 mb-2">Usuário</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>

          <div className="mb-6">
            <label className="block text-gray-700 mb-2">Senha</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
          >
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-gray-600">
          <p className="mb-2">Usuários de teste:</p>
          <ul className="space-y-1">
            <li><strong>admin</strong> / Admin@2025!Secure</li>
            <li><strong>manager</strong> / Manager@2025!Secure</li>
            <li><strong>sales</strong> / Sales@2025!Secure</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
```

**Tempo Estimado:** 3-4 horas

---

## 🔧 Fase 2: Melhorias no Backend (Prioridade MÉDIA)

### 2.1 Adicionar Mais Endpoints

**Criar:** `backend/src/main/java/br/com/dio/warehouse/adapter/in/web/controller/RelatorioController.java`

```java
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    @GetMapping("/vendas-diarias")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<VendasDiariasResponse> getVendasDiarias(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        // TODO: Implementar lógica
        return ResponseEntity.ok(new VendasDiariasResponse());
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<ProdutoVendasResponse>> getProdutosMaisVendidos() {
        // TODO: Implementar lógica
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/estoque-baixo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProdutoEstoqueResponse>> getProdutosEstoqueBaixo(
            @RequestParam(defaultValue = "10") int minimo) {
        // TODO: Implementar lógica
        return ResponseEntity.ok(List.of());
    }
}
```

**Endpoints Sugeridos:**
- `GET /api/v1/relatorios/vendas-diarias` - Vendas por período
- `GET /api/v1/relatorios/produtos-mais-vendidos` - Top 10 produtos
- `GET /api/v1/relatorios/estoque-baixo` - Produtos com estoque crítico
- `GET /api/v1/relatorios/receita-mensal` - Receita do mês
- `GET /api/v1/dashboard/metricas` - Métricas gerais (KPIs)

**Tempo Estimado:** 6-8 horas

---

### 2.2 Implementar Validações Extras

**Adicionar em:** `backend/src/main/java/br/com/dio/warehouse/application/service/`

```java
// Validar quantidade em estoque antes de vender
public void validateStockAvailability(UUID productId, int quantity) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
    
    if (product.getQuantityInStock() < quantity) {
        throw new InsufficientStockException(
            "Produto " + product.getName() + " possui apenas " + 
            product.getQuantityInStock() + " unidades em estoque"
        );
    }
}

// Validar data de validade
public void validateExpirationDate(LocalDate expirationDate) {
    if (expirationDate.isBefore(LocalDate.now())) {
        throw new InvalidExpirationDateException("Data de validade já passou");
    }
    
    if (expirationDate.isAfter(LocalDate.now().plusYears(2))) {
        throw new InvalidExpirationDateException("Data de validade muito distante (máximo 2 anos)");
    }
}

// Validar margem de lucro
public void validateProfitMargin(BigDecimal margin) {
    if (margin.compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidProfitMarginException("Margem de lucro deve ser positiva");
    }
    
    if (margin.compareTo(new BigDecimal("100")) > 0) {
        throw new InvalidProfitMarginException("Margem de lucro não pode exceder 100%");
    }
}
```

**Tempo Estimado:** 2-3 horas

---

## ☁️ Fase 3: DevOps (Prioridade BAIXA - Depois do MVP)

### 3.1 Configurar CI/CD com GitHub Actions

**Criar:** `.github/workflows/ci-cd.yml`

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 25
        uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'
      
      - name: Run Backend Tests
        working-directory: ./backend
        run: ./gradlew test
      
      - name: Generate Coverage Report
        working-directory: ./backend
        run: ./gradlew jacocoTestReport
      
      - name: Upload Coverage
        uses: codecov/codecov-action@v4
        with:
          files: ./backend/build/reports/jacoco/test/jacocoTestReport.xml

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
      
      - name: Install Dependencies
        working-directory: ./frontend
        run: npm ci
      
      - name: Run Frontend Tests
        working-directory: ./frontend
        run: npm test
      
      - name: Build Frontend
        working-directory: ./frontend
        run: npm run build

  docker-build:
    needs: [backend-tests, frontend-tests]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      
      - name: Build Backend Image
        run: docker build -t warehouse-api:${{ github.sha }} ./backend
      
      - name: Build Frontend Image
        run: docker build -f ./frontend/Dockerfile.prod -t warehouse-frontend:${{ github.sha }} ./frontend
      
      - name: Push to Registry
        run: |
          echo "TODO: Push to Docker Hub or ECR"

  deploy-staging:
    needs: docker-build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to Staging
        run: |
          echo "TODO: Deploy to staging environment"
```

**Tempo Estimado:** 4-6 horas

---

### 3.2 Deploy em Ambiente Cloud

**Opção 1: AWS ECS/Fargate**

```bash
# Criar cluster ECS
aws ecs create-cluster --cluster-name warehouse-cluster

# Criar task definition
aws ecs register-task-definition --cli-input-json file://task-definition.json

# Criar serviço
aws ecs create-service \
  --cluster warehouse-cluster \
  --service-name warehouse-api \
  --task-definition warehouse-api:1 \
  --desired-count 2 \
  --launch-type FARGATE
```

**Opção 2: Google Cloud Run**

```bash
# Build e push
gcloud builds submit --tag gcr.io/PROJECT_ID/warehouse-api

# Deploy
gcloud run deploy warehouse-api \
  --image gcr.io/PROJECT_ID/warehouse-api \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

**Opção 3: Azure Container Apps**

```bash
# Criar resource group
az group create --name warehouse-rg --location eastus

# Criar container app
az containerapp create \
  --name warehouse-api \
  --resource-group warehouse-rg \
  --image warehouse-api:latest \
  --target-port 8080 \
  --ingress external
```

**Tempo Estimado:** 8-12 horas (incluindo configuração de banco de dados gerenciado)

---

### 3.3 Monitoramento com Prometheus/Grafana

**Adicionar ao docker-compose.yml:**

```yaml
  prometheus:
    image: prom/prometheus:latest
    container_name: warehouse-prometheus
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    ports:
      - "9090:9090"
    networks:
      - warehouse-network

  grafana:
    image: grafana/grafana:latest
    container_name: warehouse-grafana
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/dashboards:/etc/grafana/provisioning/dashboards
    ports:
      - "3000:3000"
    networks:
      - warehouse-network
    depends_on:
      - prometheus
```

**Criar:** `monitoring/prometheus.yml`

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'warehouse-api'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['warehouse:8080']
  
  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-exporter:9187']
  
  - job_name: 'rabbitmq'
    static_configs:
      - targets: ['rabbitmq:15692']
```

**Métricas Importantes:**
- Requests por segundo (RPS)
- Latência média/P95/P99
- Taxa de erro (4xx, 5xx)
- Uso de CPU/Memória
- Conexões de banco de dados
- Tamanho das filas RabbitMQ
- Taxa de sucesso de migrações Flyway

**Tempo Estimado:** 6-8 horas

---

## 📋 Checklist de Implementação

### Frontend (Total: 12-16 horas)
- [ ] Conectar HomePage aos serviços de API (2-3h)
- [ ] Criar ProdutoCard component (1-2h)
- [ ] Criar ProdutoLista component (2h)
- [ ] Implementar CarrinhoContext (2h)
- [ ] Atualizar CarrinhoPage (2-3h)
- [ ] Criar AuthContext (2h)
- [ ] Criar LoginPage (1-2h)
- [ ] Adicionar rotas protegidas (1h)
- [ ] Implementar Header com login/logout (1h)

### Backend (Total: 8-11 horas)
- [ ] Criar RelatorioController (3-4h)
- [ ] Implementar queries de relatórios (3-4h)
- [ ] Adicionar validações extras (2-3h)
- [ ] Testes unitários para novos endpoints (2-3h)

### DevOps (Total: 18-26 horas)
- [ ] Configurar GitHub Actions CI (2-3h)
- [ ] Configurar GitHub Actions CD (2-3h)
- [ ] Escolher e configurar cloud provider (4-6h)
- [ ] Configurar banco de dados gerenciado (2-3h)
- [ ] Configurar Redis para cache (2h)
- [ ] Implementar Prometheus (3-4h)
- [ ] Criar dashboards Grafana (3-4h)
- [ ] Configurar alertas (2-3h)

---

## 🚀 Ordem Recomendada de Implementação

### Semana 1: Frontend Essencial (12-16h)
1. ProdutoCard + ProdutoLista
2. CarrinhoContext + CarrinhoPage
3. AuthContext + LoginPage

### Semana 2: Backend Melhorias (8-11h)
1. RelatorioController
2. Validações extras
3. Testes

### Semana 3: DevOps Básico (6-10h)
1. GitHub Actions CI
2. Testes automatizados

### Semana 4: Deploy Cloud (12-16h)
1. Escolher provider
2. Configurar infraestrutura
3. Deploy staging
4. Deploy production

---

## 📚 Recursos Úteis

### Documentação
- [React Documentation](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [TailwindCSS](https://tailwindcss.com/docs)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Docker Documentation](https://docs.docker.com)

### Tutoriais
- [React + TypeScript Best Practices](https://react-typescript-cheatsheet.netlify.app)
- [REST API Design](https://restfulapi.net)
- [CI/CD with GitHub Actions](https://docs.github.com/en/actions)

---

## ⚠️ Avisos Importantes

1. **Sempre testar localmente antes de fazer deploy**
2. **Fazer backup do banco de dados antes de mudanças grandes**
3. **Usar branches separadas para features (`git checkout -b feature/nome`)**
4. **Fazer commits pequenos e descritivos**
5. **Atualizar a documentação conforme implementa**

---

## 🆘 Suporte

Se tiver dúvidas durante a implementação:

1. **Swagger UI:** http://localhost:8080/swagger-ui/index.html - Testar API
2. **pgAdmin:** http://localhost:5050 - Ver estrutura do banco
3. **RabbitMQ:** http://localhost:15672 - Monitorar filas
4. **Logs:** `docker compose logs -f warehouse` - Ver logs em tempo real

---

**Boa sorte com o desenvolvimento! 🚀**

**Data de criação:** 16 de Outubro de 2025  
**Última atualização:** 16 de Outubro de 2025
