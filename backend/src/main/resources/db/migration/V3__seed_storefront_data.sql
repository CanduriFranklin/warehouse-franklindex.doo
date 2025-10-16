-- Migration: Seed Data for Storefront
-- Description: Dados iniciais para testes do módulo Storefront
-- Author: Franklin Canduri
-- Date: 2025-10-15

-- =====================================================
-- PRODUTOS DE EXEMPLO
-- =====================================================
INSERT INTO produtos (id, nome, descricao, preco, quantidade_estoque, categoria, ativo) VALUES
-- Eletrônicos
('11111111-1111-1111-1111-111111111111', 'Notebook Dell Inspiron 15', 'Intel Core i7, 16GB RAM, 512GB SSD', 3499.99, 50, 'Eletrônicos', true),
('22222222-2222-2222-2222-222222222222', 'Mouse Logitech MX Master 3', 'Mouse sem fio ergonômico profissional', 449.90, 100, 'Eletrônicos', true),
('33333333-3333-3333-3333-333333333333', 'Teclado Mecânico Keychron K8', 'Teclado mecânico wireless 87 teclas', 699.00, 75, 'Eletrônicos', true),
('44444444-4444-4444-4444-444444444444', 'Monitor LG 27" 4K UHD', 'Monitor IPS 27 polegadas 4K HDR', 1899.00, 30, 'Eletrônicos', true),
('55555555-5555-5555-5555-555555555555', 'Webcam Logitech C920', 'Full HD 1080p com microfone embutido', 389.90, 60, 'Eletrônicos', true),

-- Livros
('66666666-6666-6666-6666-666666666666', 'Clean Code - Robert C. Martin', 'Um guia de boas práticas de programação', 89.90, 200, 'Livros', true),
('77777777-7777-7777-7777-777777777777', 'Domain-Driven Design - Eric Evans', 'O livro clássico sobre DDD', 129.90, 150, 'Livros', true),
('88888888-8888-8888-8888-888888888888', 'Design Patterns - Gang of Four', 'Padrões de projeto orientados a objetos', 99.90, 180, 'Livros', true),

-- Acessórios
('99999999-9999-9999-9999-999999999999', 'Fone de Ouvido Sony WH-1000XM5', 'Cancelamento de ruído ativo premium', 1799.00, 40, 'Acessórios', true),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Mochila para Notebook Targus 15.6"', 'Compartimento acolchoado para laptop', 189.90, 120, 'Acessórios', true);

-- =====================================================
-- CLIENTES DE EXEMPLO
-- =====================================================
INSERT INTO clientes (id, nome, email, cpf, telefone, endereco_logradouro, endereco_numero, endereco_bairro, endereco_cidade, endereco_estado, endereco_cep, ativo) VALUES
('c1111111-1111-1111-1111-111111111111', 'Franklin Canduri', 'franklin@example.com', '12345678901', '11987654321', 'Rua das Flores', '123', 'Centro', 'São Paulo', 'SP', '01234567', true),
('c2222222-2222-2222-2222-222222222222', 'Maria Silva', 'maria.silva@example.com', '98765432109', '11876543210', 'Av. Paulista', '1000', 'Bela Vista', 'São Paulo', 'SP', '01310100', true),
('c3333333-3333-3333-3333-333333333333', 'João Santos', 'joao.santos@example.com', '11122233344', '11765432109', 'Rua Augusta', '500', 'Consolação', 'São Paulo', 'SP', '01305000', true);

-- =====================================================
-- CARRINHOS DE EXEMPLO (ATIVOS)
-- =====================================================
INSERT INTO carrinhos_compras (id, cliente_id, status) VALUES
('ca111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'ATIVO'),
('ca222222-2222-2222-2222-222222222222', 'c2222222-2222-2222-2222-222222222222', 'ATIVO');

-- =====================================================
-- ITENS NOS CARRINHOS
-- =====================================================
INSERT INTO itens_carrinho (carrinho_id, produto_id, quantidade, preco_unitario) VALUES
-- Carrinho do Franklin
('ca111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 1, 3499.99),
('ca111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 1, 449.90),
('ca111111-1111-1111-1111-111111111111', '66666666-6666-6666-6666-666666666666', 2, 89.90),

-- Carrinho da Maria
('ca222222-2222-2222-2222-222222222222', '99999999-9999-9999-9999-999999999999', 1, 1799.00),
('ca222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 1, 189.90);

-- =====================================================
-- PEDIDOS DE EXEMPLO
-- =====================================================
INSERT INTO pedidos (
    id, 
    numero_pedido, 
    cliente_id, 
    endereco_entrega_logradouro,
    endereco_entrega_numero,
    endereco_entrega_bairro,
    endereco_entrega_cidade,
    endereco_entrega_estado,
    endereco_entrega_cep,
    valor_total,
    status,
    pagamento_confirmado_em
) VALUES
(
    'p1111111-1111-1111-1111-111111111111',
    'PED-20251015-00001',
    'c3333333-3333-3333-3333-333333333333',
    'Rua Augusta',
    '500',
    'Consolação',
    'São Paulo',
    'SP',
    '01305000',
    4349.88,
    'PAGAMENTO_CONFIRMADO',
    CURRENT_TIMESTAMP - INTERVAL '2 hours'
),
(
    'p2222222-2222-2222-2222-222222222222',
    'PED-20251015-00002',
    'c1111111-1111-1111-1111-111111111111',
    'Rua das Flores',
    '123',
    'Centro',
    'São Paulo',
    'SP',
    '01234567',
    1988.90,
    'ENVIADO',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- =====================================================
-- ITENS DOS PEDIDOS
-- =====================================================
INSERT INTO itens_pedido (pedido_id, produto_id, nome_produto, quantidade, preco_unitario) VALUES
-- Pedido PED-20251015-00001 (João)
('p1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Notebook Dell Inspiron 15', 1, 3499.99),
('p1111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'Mouse Logitech MX Master 3', 1, 449.90),
('p1111111-1111-1111-1111-111111111111', '66666666-6666-6666-6666-666666666666', 'Clean Code - Robert C. Martin', 2, 89.90),
('p1111111-1111-1111-1111-111111111111', '77777777-7777-7777-7777-777777777777', 'Domain-Driven Design - Eric Evans', 2, 129.90),

-- Pedido PED-20251015-00002 (Franklin)
('p2222222-2222-2222-2222-222222222222', '99999999-9999-9999-9999-999999999999', 'Fone de Ouvido Sony WH-1000XM5', 1, 1799.00),
('p2222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Mochila para Notebook Targus 15.6"', 1, 189.90);

-- =====================================================
-- COMENTÁRIOS
-- =====================================================
COMMENT ON TABLE produtos IS 'Contém 10 produtos de exemplo em categorias variadas';
COMMENT ON TABLE clientes IS 'Contém 3 clientes de exemplo';
COMMENT ON TABLE carrinhos_compras IS 'Contém 2 carrinhos ativos com itens';
COMMENT ON TABLE pedidos IS 'Contém 2 pedidos de exemplo em diferentes status';
