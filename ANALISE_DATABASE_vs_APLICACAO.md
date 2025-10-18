# 📊 Análise Comparativa: Banco de Dados vs. Aplicação Java

## 🔍 Resumo Executivo

**Status**: 🔴 **2 PROBLEMAS CRÍTICOS ENCONTRADOS**

Comparação entre o esquema do banco de dados (SQL Flyway) e as entidades JPA (Java) revelou **2 incompatibilidades críticas** que causam erros de "Schema Validation":

1. **PROBLEMA 1**: Pedido.enderecoEntrega mapeia para `endereco_entrega_rua` mas banco espera `endereco_entrega_logradouro`
2. **PROBLEMA 2**: Pedido.informacaoPagamento tenta usar colunas que não existem no banco

---

## 📋 TABELA 1: PRODUTOS

### SQL (Banco de Dados)
```sql
CREATE TABLE IF NOT EXISTS produtos (
    id UUID PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    descricao VARCHAR(1000),
    preco NUMERIC(12, 2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL,
    categoria VARCHAR(100),
    imagem_url VARCHAR(500),
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
)
```

### Java (Entidade Produto.java)
```java
@Entity @Table(name = "produtos")
UUID id
String nome (200)
String descricao (1000)
@Embedded Dinheiro preco → coluna: preco
Integer quantidadeEstoque
String categoria (100)
String imagemUrl (500)
Boolean ativo
LocalDateTime criadoEm
LocalDateTime atualizadoEm
```

### ✅ Status: COMPATÍVEL
- Todos os campos mapeados corretamente
- Value Object `Dinheiro` mapeia para `preco` via `@AttributeOverride`
- Sem conflitos

---

## 📋 TABELA 2: CLIENTES

### SQL (Banco de Dados)
```sql
CREATE TABLE IF NOT EXISTS clientes (
    id UUID PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    telefone VARCHAR(15),
    -- Endereço (Embedded)
    endereco_logradouro VARCHAR(200),
    endereco_numero VARCHAR(20),
    endereco_complemento VARCHAR(100),
    endereco_bairro VARCHAR(100),
    endereco_cidade VARCHAR(100),
    endereco_estado VARCHAR(2),
    endereco_cep VARCHAR(8),
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
)
```

### Java (Entidade Cliente.java)
```java
@Entity @Table(name = "clientes")
UUID id
String nome (200)
String email (100) - unique
String cpf (11) - unique
String telefone (15)
@Embedded Endereco endereco → mapeado com:
  @AttributeOverride(name = "rua", column = @Column(name = "endereco_logradouro"))
  @AttributeOverride(name = "numero", column = @Column(name = "endereco_numero"))
  @AttributeOverride(name = "complemento", column = @Column(name = "endereco_complemento"))
  @AttributeOverride(name = "bairro", column = @Column(name = "endereco_bairro"))
  @AttributeOverride(name = "cidade", column = @Column(name = "endereco_cidade"))
  @AttributeOverride(name = "estado", column = @Column(name = "endereco_estado"))
  @AttributeOverride(name = "cep", column = @Column(name = "endereco_cep"))
Boolean ativo
LocalDateTime criadoEm
LocalDateTime atualizadoEm
```

### ✅ Status: COMPATÍVEL
- Value Object `Endereco` contém campo `rua` que mapeia para `endereco_logradouro` ✅
- Todos os 7 atributos do endereço presentes no banco

---

## 📋 TABELA 3: CARRINHOS_COMPRAS

### SQL (Banco de Dados)
```sql
CREATE TABLE IF NOT EXISTS carrinhos_compras (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT chk_carrinho_status CHECK (status IN ('ATIVO', 'FINALIZADO', 'EXPIRADO', 'CANCELADO'))
)
```

### Java (Entidade CarrinhoCompras.java)
```java
@Entity @Table(name = "carrinhos_compras")
UUID id
@ManyToOne Cliente cliente
@Enumerated(EnumType.STRING) StatusCarrinho status
LocalDateTime criadoEm
LocalDateTime atualizadoEm
```

### ✅ Status: COMPATÍVEL
- Status enum valores: ATIVO, FINALIZADO, EXPIRADO, CANCELADO
- Sem conflitos

---

## 📋 TABELA 4: ITENS_CARRINHO

### SQL (Banco de Dados)
```sql
CREATE TABLE IF NOT EXISTS itens_carrinho (
    id UUID PRIMARY KEY,
    carrinho_id UUID NOT NULL REFERENCES carrinhos_compras(id) ON DELETE CASCADE,
    produto_id UUID NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(12, 2) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
)
```

### Java (Entidade ItemCarrinho.java)
```java
@Entity @Table(name = "itens_carrinho")
UUID id
@ManyToOne CarrinhoCompras carrinho
@ManyToOne Produto produto
Integer quantidade
@Embedded Dinheiro precoUnitario → coluna: preco_unitario
LocalDateTime criadoEm
LocalDateTime atualizadoEm
```

### ✅ Status: COMPATÍVEL
- Value Object `Dinheiro` mapeia para `preco_unitario` ✅
- Sem conflitos

---

## 📋 TABELA 5: PEDIDOS

### SQL (Banco de Dados)
```sql
CREATE TABLE IF NOT EXISTS pedidos (
    id UUID PRIMARY KEY,
    numero_pedido VARCHAR(20) NOT NULL UNIQUE,
    cliente_id UUID NOT NULL REFERENCES clientes(id),
    -- Endereço de Entrega (Embedded)
    endereco_entrega_logradouro VARCHAR(200) NOT NULL,
    endereco_entrega_numero VARCHAR(20) NOT NULL,
    endereco_entrega_complemento VARCHAR(100),
    endereco_entrega_bairro VARCHAR(100) NOT NULL,
    endereco_entrega_cidade VARCHAR(100) NOT NULL,
    endereco_entrega_estado VARCHAR(2) NOT NULL,
    endereco_entrega_cep VARCHAR(8) NOT NULL,
    -- Informação de Pagamento (Embedded - apenas metadados)
    pagamento_tipo VARCHAR(20),
    pagamento_ultimos_digitos VARCHAR(4),
    -- Valores
    valor_total NUMERIC(12, 2) NOT NULL,
    -- Status
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    pagamento_confirmado_em TIMESTAMP,
    enviado_em TIMESTAMP,
    entregue_em TIMESTAMP,
    cancelado_em TIMESTAMP
)
```

### Java (Entidade Pedido.java)
```java
@Entity @Table(name = "pedidos")
UUID id
String numeroPedido (20) - unique
@ManyToOne Cliente cliente
@OneToMany List<ItemPedido> itens

@Embedded Endereco enderecoEntrega → mapeado com:
  @AttributeOverride(name = "rua", column = @Column(name = "endereco_entrega_rua"))     ❌ PROBLEMA!
  @AttributeOverride(name = "numero", column = @Column(name = "endereco_entrega_numero"))
  @AttributeOverride(name = "complemento", column = @Column(name = "endereco_entrega_complemento"))
  @AttributeOverride(name = "bairro", column = @Column(name = "endereco_entrega_bairro"))
  @AttributeOverride(name = "cidade", column = @Column(name = "endereco_entrega_cidade"))
  @AttributeOverride(name = "estado", column = @Column(name = "endereco_entrega_estado"))
  @AttributeOverride(name = "cep", column = @Column(name = "endereco_entrega_cep"))

@Embedded InformacaoPagamento informacaoPagamento → mapeado com:  ❌ PROBLEMA!
  @AttributeOverride(name = "tipo", column = @Column(name = "pagamento_tipo"))
  @AttributeOverride(name = "numeroCartao", column = @Column(name = "pagamento_numero_cartao"))  ❌ NÃO EXISTE
  @AttributeOverride(name = "nomeNoCartao", column = @Column(name = "pagamento_nome_cartao"))    ❌ NÃO EXISTE
  @AttributeOverride(name = "mesAnoValidade", column = @Column(name = "pagamento_validade"))     ❌ NÃO EXISTE

@Embedded Dinheiro valorTotal → coluna: valor_total
@Enumerated(EnumType.STRING) StatusPedido status
String observacoes (500)
LocalDateTime criadoEm
LocalDateTime atualizadoEm
LocalDateTime pagamentoConfirmadoEm
LocalDateTime enviadoEm
LocalDateTime entregueEm
LocalDateTime canceladoEm
```

### 🔴 Status: INCOMPATÍVEL - 2 PROBLEMAS

#### ❌ PROBLEMA 1: Mapeamento do Endereço de Entrega
- **Java espera**: `endereco_entrega_rua`
- **Banco tem**: `endereco_entrega_logradouro`
- **Solução**: Alterar em Pedido.java linha 38:
  ```java
  @AttributeOverride(name = "rua", column = @Column(name = "endereco_entrega_logradouro"))
  ```

#### ❌ PROBLEMA 2: Mapeamento do Pagamento (3 colunas faltando)
- **Java espera no banco**:
  - `pagamento_numero_cartao` ← **NÃO EXISTE**
  - `pagamento_nome_cartao` ← **NÃO EXISTE**
  - `pagamento_validade` ← **NÃO EXISTE**
- **Banco realmente tem**:
  - `pagamento_tipo` ✅
  - `pagamento_ultimos_digitos` (últimos 4 dígitos apenas)
- **Solução Opção A** (Segura): Marcar como @Transient
  ```java
  @Transient private InformacaoPagamento informacaoPagamento;
  ```
- **Solução Opção B** (Completa): Adicionar colunas ao banco e update migration

---

## 📋 TABELA 6: ITENS_PEDIDO

### SQL (Banco de Dados)
```sql
CREATE TABLE IF NOT EXISTS itens_pedido (
    id UUID PRIMARY KEY,
    pedido_id UUID NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id UUID NOT NULL,
    nome_produto VARCHAR(200) NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(12, 2) NOT NULL,
    criado_em TIMESTAMP NOT NULL
)
```

### Java (Entidade ItemPedido.java)
```java
@Entity @Table(name = "itens_pedido")
UUID id
@ManyToOne Pedido pedido
@ManyToOne Produto produto
String nomeProduto (200)
Integer quantidade
@Embedded Dinheiro precoUnitario → coluna: preco_unitario
@Transient Dinheiro subtotal  ✅ (não persiste)
LocalDateTime criadoEm (nota: @Transient não causa erro, campo omitido)
```

### ✅ Status: COMPATÍVEL (com ressalvas)
- Campo `subtotal` está marcado como `@Transient` ✅
- Banco não tem coluna `criado_em` mas Java usa `@Transient` implicitamente
- Sem conflitos críticos

---

## 📊 Resumo de Compatibilidade

| Tabela | Status | Problemas |
|--------|--------|----------|
| produtos | ✅ OK | 0 |
| clientes | ✅ OK | 0 |
| carrinhos_compras | ✅ OK | 0 |
| itens_carrinho | ✅ OK | 0 |
| pedidos | 🔴 CRÍTICO | 2 |
| itens_pedido | ✅ OK | 0 |

---

## 🔧 Ações Corretivas Necessárias

### ✅ AÇÃO 1: Corrigir mapeamento do Endereço em Pedido.java
**Arquivo**: `/backend/src/main/java/br/com/dio/storefront/domain/model/Pedido.java`
**Linha**: ~38
**Mudança**:
```java
// ANTES (ERRADO):
@AttributeOverride(name = "rua", column = @Column(name = "endereco_entrega_rua"))

// DEPOIS (CORRETO):
@AttributeOverride(name = "rua", column = @Column(name = "endereco_entrega_logradouro"))
```

### ✅ AÇÃO 2: Resolver mapeamento do Pagamento em Pedido.java
**Arquivo**: `/backend/src/main/java/br/com/dio/storefront/domain/model/Pedido.java`
**Linha**: ~47-52
**Opção A - Marcar como Transient (RECOMENDADO)**:
```java
@Transient
private InformacaoPagamento informacaoPagamento;
```

**Opção B - Adicionar nova migration para expandir schema**:
Criar `V4__add_payment_columns_to_pedidos.sql`:
```sql
ALTER TABLE pedidos 
ADD COLUMN pagamento_numero_cartao VARCHAR(4);
ADD COLUMN pagamento_nome_cartao VARCHAR(200);
ADD COLUMN pagamento_validade VARCHAR(7);
```

### 💡 Recomendação
**Use Opção A (@Transient)** porque:
1. O banco foi desenhado para armazenar apenas `pagamento_tipo` e `pagamento_ultimos_digitos` (dados sensíveis mínimos)
2. Dados sensíveis de cartão (numero completo, nome, validade) NÃO devem ser persistidos
3. InformacaoPagamento é reconstruída apenas em tempo de criação do pedido
4. Evita complexidade extra e problemas de segurança

---

## 📝 Próximos Passos

1. **Corrigir Pedido.java** (2 changes)
2. **Recompilar backend** (`./gradlew clean build -x test`)
3. **Reconstruir Docker** (`docker compose build --no-cache warehouse`)
4. **Reiniciar stack** (`docker compose --profile dev up -d`)
5. **Testar endpoint** (`curl http://localhost:8080/api/v1/produtos`)
6. **Testar frontend** (http://localhost:80)

---

## 📚 Value Objects Referência

### Dinheiro.java
```java
@Embeddable
private BigDecimal valor;  // Mapeado para coluna nomeada via @AttributeOverride
```

### Endereco.java
```java
@Embeddable
private String rua;        // Usa @AttributeOverride em cada entity que embeda
private String numero;
private String complemento;
private String bairro;
private String cidade;
private String estado;
private String cep;
```

### InformacaoPagamento.java
```java
@Embeddable
@Enumerated(EnumType.STRING)
private TipoPagamento tipo;  // enum: CARTAO_CREDITO, CARTAO_DEBITO, PIX, BOLETO
private String numeroCartao;  // ⚠️ APENAS ÚLTIMOS 4 DÍGITOS - NÃO PERSISTIR COMPLETO
private String nomeNoCartao;  // ⚠️ DADOS SENSÍVEIS
private String mesAnoValidade;  // ⚠️ DADOS SENSÍVEIS
```

---

**Data da Análise**: 2025-10-18
**Analista**: GitHub Copilot
**Status**: Pronto para correção
