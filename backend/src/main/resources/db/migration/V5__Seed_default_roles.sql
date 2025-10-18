-- V5__Seed_default_roles.sql
-- Migração para inserir as roles padrão do sistema

INSERT INTO roles (nome, descricao, criado_em, atualizado_em) VALUES
    (
        'ADMIN',
        'Acesso total ao sistema. Pode gerenciar usuários, roles, configurações e todas as operações.',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'WAREHOUSE_MANAGER',
        'Gerenciador de estoque e armazém. Pode gerenciar produtos, estoque e entregas.',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'SALES',
        'Gerenciador de vendas. Pode processar pedidos, visualizar vendas e clientes.',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        'CUSTOMER',
        'Cliente/Consumidor. Pode realizar compras, visualizar pedidos e gerenciar sua conta.',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (nome) DO NOTHING;

-- Documentação
COMMENT ON TABLE roles IS 'Tabela de papéis/funções. As 4 roles padrão foram inseridas nesta migração.';
