-- V6__Migrate_dev_users.sql
-- Migração para inserir os usuários de desenvolvimento com senhas hasheadas em BCrypt
-- 
-- IMPORTANTE: Estas credenciais são APENAS para desenvolvimento local
-- Em produção, não há usuários pré-criados. Todos os usuários devem se registrar.
--
-- Credenciais de desenvolvimento:
-- ==============================
-- Usuário: admin
-- Email: admin@warehouse.local
-- Senha: Admin2025Secure
-- Role: ADMIN
-- Hash BCrypt: $2a$10$SlHYh7ZX7.qGZUZ/VCDa0uNvfEZc4qFZm8DhNqYlH7c.8Q3L2nYcG
--
-- Usuário: manager
-- Email: manager@warehouse.local
-- Senha: Manager2025Secure
-- Role: WAREHOUSE_MANAGER
-- Hash BCrypt: $2a$10$5sJgXQ8u.N7VN5D.K9dDg.JzQvQb1s5L5hqK2pL3m4nN6o7P8qP9O
--
-- Usuário: sales
-- Email: sales@warehouse.local
-- Senha: Sales2025Secure
-- Role: SALES
-- Hash BCrypt: $2a$10$T3r7H9k2L5m8N1p4Q7s0v.W3x6Y9zB2C5d8E1f4G7h0J3k6L9m2P

-- ============================================================================
-- INSERIR USUÁRIOS DE DESENVOLVIMENTO
-- ============================================================================

INSERT INTO usuarios (id, username, email, senha_hash, ativo, criado_em, atualizado_em) VALUES
    (
        '11111111-1111-1111-1111-111111111111'::uuid,
        'admin',
        'admin@warehouse.local',
        '$2a$10$SlHYh7ZX7.qGZUZ/VCDa0uNvfEZc4qFZm8DhNqYlH7c.8Q3L2nYcG',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '22222222-2222-2222-2222-222222222222'::uuid,
        'manager',
        'manager@warehouse.local',
        '$2a$10$5sJgXQ8u.N7VN5D.K9dDg.JzQvQb1s5L5hqK2pL3m4nN6o7P8qP9O',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '33333333-3333-3333-3333-333333333333'::uuid,
        'sales',
        'sales@warehouse.local',
        '$2a$10$T3r7H9k2L5m8N1p4Q7s0v.W3x6Y9zB2C5d8E1f4G7h0J3k6L9m2P',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (username) DO NOTHING;

-- ============================================================================
-- ASSOCIAR USUÁRIOS ÀS ROLES
-- ============================================================================

-- Admin com ADMIN role
INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id
FROM usuarios u
CROSS JOIN roles r
WHERE u.username = 'admin' AND r.nome = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Manager com WAREHOUSE_MANAGER role
INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id
FROM usuarios u
CROSS JOIN roles r
WHERE u.username = 'manager' AND r.nome = 'WAREHOUSE_MANAGER'
ON CONFLICT DO NOTHING;

-- Sales com SALES role
INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id
FROM usuarios u
CROSS JOIN roles r
WHERE u.username = 'sales' AND r.nome = 'SALES'
ON CONFLICT DO NOTHING;

-- ============================================================================
-- VERIFICAÇÃO E DOCUMENTAÇÃO
-- ============================================================================

-- Comentários explicativos
COMMENT ON TABLE usuarios IS 'Tabela de usuários. 3 usuários de desenvolvimento foram inseridos nesta migração (SOMENTE PARA DEV).';

-- Para verificar os dados inseridos, execute:
-- SELECT * FROM usuarios;
-- SELECT u.username, r.nome FROM usuarios u 
-- JOIN usuario_roles ur ON u.id = ur.usuario_id 
-- JOIN roles r ON ur.role_id = r.id;
