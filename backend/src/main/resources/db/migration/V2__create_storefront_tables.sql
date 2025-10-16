-- Migration: Create Storefront Tables
-- Description: Cria as tabelas do módulo Storefront (Produtos, Clientes, Carrinhos, Pedidos)
-- Author: Franklin Canduri
-- Date: 2025-10-15

-- =====================================================
-- TABELA: produtos
-- =====================================================
CREATE TABLE IF NOT EXISTS produtos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(200) NOT NULL,
    descricao VARCHAR(1000),
    preco NUMERIC(12, 2) NOT NULL CHECK (preco >= 0),
    quantidade_estoque INTEGER NOT NULL DEFAULT 0 CHECK (quantidade_estoque >= 0),
    categoria VARCHAR(100),
    imagem_url VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_produtos_categoria ON produtos(categoria) WHERE ativo = true;
CREATE INDEX idx_produtos_nome ON produtos USING gin(to_tsvector('portuguese', nome));
CREATE INDEX idx_produtos_ativo ON produtos(ativo);

COMMENT ON TABLE produtos IS 'Catálogo de produtos disponíveis na loja';
COMMENT ON COLUMN produtos.preco IS 'Preço do produto em BRL (Real)';
COMMENT ON COLUMN produtos.quantidade_estoque IS 'Quantidade disponível em estoque';

-- =====================================================
-- TABELA: clientes
-- =====================================================
CREATE TABLE IF NOT EXISTS clientes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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
    -- Metadata
    ativo BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_clientes_email ON clientes(LOWER(email));
CREATE INDEX idx_clientes_cpf ON clientes(cpf);
CREATE INDEX idx_clientes_nome ON clientes USING gin(to_tsvector('portuguese', nome));

COMMENT ON TABLE clientes IS 'Cadastro de clientes da loja';
COMMENT ON COLUMN clientes.cpf IS 'CPF sem pontuação (11 dígitos)';
COMMENT ON COLUMN clientes.endereco_cep IS 'CEP sem pontuação (8 dígitos)';

-- =====================================================
-- TABELA: carrinhos_compras
-- =====================================================
CREATE TABLE IF NOT EXISTS carrinhos_compras (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_carrinho_status CHECK (status IN ('ATIVO', 'FINALIZADO', 'EXPIRADO', 'CANCELADO'))
);

CREATE INDEX idx_carrinhos_cliente ON carrinhos_compras(cliente_id);
CREATE INDEX idx_carrinhos_status ON carrinhos_compras(status);
CREATE UNIQUE INDEX idx_carrinhos_cliente_ativo ON carrinhos_compras(cliente_id) 
    WHERE status = 'ATIVO';

COMMENT ON TABLE carrinhos_compras IS 'Carrinhos de compras dos clientes';
COMMENT ON COLUMN carrinhos_compras.status IS 'Status: ATIVO, FINALIZADO, EXPIRADO, CANCELADO';

-- =====================================================
-- TABELA: itens_carrinho
-- =====================================================
CREATE TABLE IF NOT EXISTS itens_carrinho (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    carrinho_id UUID NOT NULL REFERENCES carrinhos_compras(id) ON DELETE CASCADE,
    produto_id UUID NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(12, 2) NOT NULL CHECK (preco_unitario >= 0),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_carrinho_produto UNIQUE (carrinho_id, produto_id)
);

CREATE INDEX idx_itens_carrinho_carrinho ON itens_carrinho(carrinho_id);
CREATE INDEX idx_itens_carrinho_produto ON itens_carrinho(produto_id);

COMMENT ON TABLE itens_carrinho IS 'Itens dentro de cada carrinho de compras';
COMMENT ON COLUMN itens_carrinho.preco_unitario IS 'Preço do produto no momento da adição ao carrinho';

-- =====================================================
-- TABELA: pedidos
-- =====================================================
CREATE TABLE IF NOT EXISTS pedidos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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
    -- Informação de Pagamento (Embedded - apenas metadados, não dados sensíveis)
    pagamento_tipo VARCHAR(20),
    pagamento_ultimos_digitos VARCHAR(4),
    -- Valores
    valor_total NUMERIC(12, 2) NOT NULL CHECK (valor_total >= 0),
    -- Status e Timestamps
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    observacoes VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pagamento_confirmado_em TIMESTAMP,
    enviado_em TIMESTAMP,
    entregue_em TIMESTAMP,
    cancelado_em TIMESTAMP,
    CONSTRAINT chk_pedido_status CHECK (status IN (
        'AGUARDANDO_PAGAMENTO', 
        'PAGAMENTO_CONFIRMADO', 
        'EM_SEPARACAO', 
        'ENVIADO', 
        'ENTREGUE', 
        'CANCELADO'
    ))
);

CREATE INDEX idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_status ON pedidos(status);
CREATE INDEX idx_pedidos_numero ON pedidos(numero_pedido);
CREATE INDEX idx_pedidos_criado_em ON pedidos(criado_em DESC);

COMMENT ON TABLE pedidos IS 'Pedidos realizados pelos clientes';
COMMENT ON COLUMN pedidos.numero_pedido IS 'Número único do pedido (formato: PED-YYYYMMDD-XXXXX)';
COMMENT ON COLUMN pedidos.status IS 'Ciclo de vida: AGUARDANDO_PAGAMENTO -> PAGAMENTO_CONFIRMADO -> EM_SEPARACAO -> ENVIADO -> ENTREGUE';

-- =====================================================
-- TABELA: itens_pedido
-- =====================================================
CREATE TABLE IF NOT EXISTS itens_pedido (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id UUID NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id UUID NOT NULL,  -- Não FK pois produto pode ser deletado depois
    nome_produto VARCHAR(200) NOT NULL,
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(12, 2) NOT NULL CHECK (preco_unitario >= 0),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_itens_pedido_pedido ON itens_pedido(pedido_id);
CREATE INDEX idx_itens_pedido_produto ON itens_pedido(produto_id);

COMMENT ON TABLE itens_pedido IS 'Itens de cada pedido (snapshot do produto no momento da compra)';
COMMENT ON COLUMN itens_pedido.nome_produto IS 'Nome do produto gravado para histórico';
COMMENT ON COLUMN itens_pedido.preco_unitario IS 'Preço do produto no momento da compra';

-- =====================================================
-- TRIGGERS: Updated At
-- =====================================================
CREATE OR REPLACE FUNCTION update_atualizado_em()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_produtos_atualizado_em
    BEFORE UPDATE ON produtos
    FOR EACH ROW
    EXECUTE FUNCTION update_atualizado_em();

CREATE TRIGGER trg_clientes_atualizado_em
    BEFORE UPDATE ON clientes
    FOR EACH ROW
    EXECUTE FUNCTION update_atualizado_em();

CREATE TRIGGER trg_carrinhos_atualizado_em
    BEFORE UPDATE ON carrinhos_compras
    FOR EACH ROW
    EXECUTE FUNCTION update_atualizado_em();

CREATE TRIGGER trg_itens_carrinho_atualizado_em
    BEFORE UPDATE ON itens_carrinho
    FOR EACH ROW
    EXECUTE FUNCTION update_atualizado_em();

CREATE TRIGGER trg_pedidos_atualizado_em
    BEFORE UPDATE ON pedidos
    FOR EACH ROW
    EXECUTE FUNCTION update_atualizado_em();
