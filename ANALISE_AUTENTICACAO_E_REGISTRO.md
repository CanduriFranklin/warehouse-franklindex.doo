# 📋 ANÁLISE: Autenticação e Registro de Usuários

## 🔍 ESTADO ATUAL DO SISTEMA

### 1. **Autenticação Atual (InMemory)**
```
Usuários pré-carregados em memória (não persistidos):
  ├─ admin / Admin@2025Secure (sem !)
  ├─ manager / Manager@2025Secure (sem !)
  └─ sales / Sales@2025Secure (sem !)

Carregados de: environment variables → application-dev.yml
  ├─ DEV_ADMIN_PASSWORD
  ├─ DEV_MANAGER_PASSWORD
  └─ DEV_SALES_PASSWORD

Implementação: InMemoryUserDetailsService.java
```

### 2. **Tabelas Existentes no Banco**
```
✅ clientes (para dados de clientes da loja)
   ├─ id (UUID)
   ├─ nome ✓
   ├─ email ✓ (UNIQUE)
   ├─ cpf ✓ (UNIQUE)
   ├─ telefone
   ├─ endereco_* (7 colunas)
   ├─ ativo
   └─ criado_em / atualizado_em

❌ usuarios / users / accounts (NÃO EXISTE)
❌ auth_credentials (NÃO EXISTE)
```

### 3. **Endpoints Disponíveis**
```
POST /api/v1/auth/login           ✅ Funciona
POST /api/v1/auth/logout          ✅ Funciona
GET  /api/v1/auth/me              ✅ Funciona
GET  /api/v1/auth/validate        ✅ Funciona
POST /api/v1/auth/register        ❌ NÃO EXISTE
```

### 4. **Frontend (AuthPage.tsx)**
```
Login Form:
  ├─ username ✓
  ├─ password ✓
  └─ Submit → /api/v1/auth/login ✓

Register Form:
  ├─ username
  ├─ email
  ├─ password
  ├─ confirmPassword
  └─ Submit → Simula apenas (não chama API) ❌
```

---

## 🎯 PROPOSTA DE SOLUÇÃO

Existem 2 abordagens possíveis:

### **OPÇÃO A: Sistema Duplo (RECOMENDADO)**
Manter ambos os sistemas:

1. **Admin/Manager/Sales** → InMemory (sem registro)
   - Usuários pré-carregados
   - Apenas login disponível
   - Para equipe interna

2. **Clientes** → Banco de Dados
   - Novo endpoint `/api/v1/auth/register-cliente`
   - Criação de registro em `clientes` tabela
   - Autenticação via email + senha (não username)
   - Para clientes da loja

**Vantagens:**
- ✅ Separa equipe interna de clientes
- ✅ Reutiliza tabela `clientes` existente
- ✅ Menos alterações no código
- ✅ Segurança: roles diferentes

---

### **OPÇÃO B: Migrar Todos para Banco**
Substituir InMemory por UserDetailsService baseado em banco:

1. Criar tabela `usuarios`
2. Adicionar campos `username` e `senha_hash` à tabela
3. Implementar UserDetailsService que lê do banco
4. Criar endpoint `/api/v1/auth/register`

**Vantagens:**
- ✅ Sistema unificado
- ✅ Todos os usuários no banco
- ❌ Mais alterações no código
- ❌ Equipe interna também pode se registrar

---

## 📊 COMPARAÇÃO: O que existe vs O que o Frontend Pede

### **Login**
```
Frontend envia:
  {
    "username": "admin",
    "password": "Admin@2025Secure"
  }

Backend aceita:
  ✅ Sim - InMemoryUserDetailsService

Resposta:
  {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "username": "admin",
    "roles": "ROLE_ADMIN,ROLE_WAREHOUSE_MANAGER",
    "expiresAt": "2025-10-18T22:00:00Z"
  }
```

### **Register (Atualmente)**
```
Frontend envia:
  {
    "username": "novo_usuario",
    "email": "user@example.com",
    "password": "senha123",
    "confirmPassword": "senha123"
  }

Backend:
  ❌ Endpoint /api/v1/auth/register NÃO EXISTE

Frontend faz:
  - Simula o registro (console.log apenas)
  - Tenta fazer login com credenciais que não existem
  - Falha com "Credenciais inválidas" ❌
```

---

## ✅ RECOMENDAÇÃO FINAL

**Implementar OPÇÃO A (Sistema Duplo)** porque:

1. **Rápido**: Reutiliza tabela `clientes` existente
2. **Seguro**: Separa equipe de clientes
3. **Realista**: Lojas reais têm este modelo
4. **Manutenível**: Mínimas alterações no código

### **O que fazer:**

1. **Backend:**
   - Adicionar coluna `username` em `clientes` (UNIQUE, NULL initially)
   - Adicionar coluna `senha_hash` em `clientes` (NULL initially)
   - Criar `ClienteUserDetailsService` que implementa `UserDetailsService`
   - Criar endpoint `POST /api/v1/auth/register-cliente`
   - Criar `RegisterClienteRequest` DTO

2. **Frontend:**
   - Adicionar modo "Cliente" vs "Admin" na página de login
   - Atualizar formulário de registro para cliente
   - Chamar `/api/v1/auth/register-cliente` ao registrar
   - Validar senha sem caracteres especiais problemáticos

3. **Banco de Dados:**
   - Migração Flyway: `V4__Add_cliente_authentication_fields.sql`
   - Adicionar `username` e `senha_hash` à tabela `clientes`

---

## 🔐 Campos que o Banco Precisa (Cliente)

```sql
ALTER TABLE clientes ADD COLUMN (
  username VARCHAR(50) UNIQUE,          -- login do cliente
  senha_hash VARCHAR(255),              -- BCrypt hash
  ultimo_acesso TIMESTAMP,              -- último login
  tentativas_login_falhas INT DEFAULT 0, -- contra bruteforce
  bloqueado BOOLEAN DEFAULT FALSE       -- bloquear após muitas tentativas
);
```

---

## 📝 Credenciais Sem "!" (Recomendado)

Para evitar problemas com caracteres especiais:

```
DEV_ADMIN_PASSWORD=Admin2025Secure
DEV_MANAGER_PASSWORD=Manager2025Secure
DEV_SALES_PASSWORD=Sales2025Secure
```

Ou mais simples para desenvolvimento:

```
DEV_ADMIN_PASSWORD=admin123
DEV_MANAGER_PASSWORD=manager123
DEV_SALES_PASSWORD=sales123
```

