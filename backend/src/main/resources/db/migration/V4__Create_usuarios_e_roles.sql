-- V4__Create_usuarios_e_roles.sql
-- Migração para criar estrutura de autenticação baseada em banco de dados
-- Substitui o sistema InMemory por autenticação persistida em BD

-- ============================================================================
-- CRIAR TABELA: roles
-- ============================================================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índice para melhor performance em buscas
CREATE INDEX IF NOT EXISTS idx_roles_nome ON roles(nome);

-- ============================================================================
-- CRIAR TABELA: usuarios
-- ============================================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    ultimo_acesso TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON usuarios(username);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_ativo ON usuarios(ativo);

-- ============================================================================
-- CRIAR TABELA: usuario_roles (Relacionamento muitos-para-muitos)
-- ============================================================================
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id UUID NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índice para melhor performance em buscas
CREATE INDEX IF NOT EXISTS idx_usuario_roles_usuario_id ON usuario_roles(usuario_id);
CREATE INDEX IF NOT EXISTS idx_usuario_roles_role_id ON usuario_roles(role_id);

-- ============================================================================
-- CRIAR FUNÇÃO: update_atualizado_em()
-- Atualiza automaticamente o campo 'atualizado_em' quando um registro é modificado
-- ============================================================================
CREATE OR REPLACE FUNCTION update_atualizado_em()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- CRIAR TRIGGERS: atualizar timestamp em modificações
-- ============================================================================
DROP TRIGGER IF EXISTS trg_roles_atualizado_em ON roles;
CREATE TRIGGER trg_roles_atualizado_em
BEFORE UPDATE ON roles
FOR EACH ROW
EXECUTE FUNCTION update_atualizado_em();

DROP TRIGGER IF EXISTS trg_usuarios_atualizado_em ON usuarios;
CREATE TRIGGER trg_usuarios_atualizado_em
BEFORE UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION update_atualizado_em();

-- ============================================================================
-- COMENTÁRIOS PARA DOCUMENTAÇÃO
-- ============================================================================
COMMENT ON TABLE roles IS 'Tabela de papéis/funções do sistema. Define permissões e tipos de usuário.';
COMMENT ON COLUMN roles.id IS 'Identificador único da role';
COMMENT ON COLUMN roles.nome IS 'Nome da role (ex: ADMIN, WAREHOUSE_MANAGER, SALES, CUSTOMER)';
COMMENT ON COLUMN roles.descricao IS 'Descrição da role e suas permissões';

COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema. Armazena credenciais de autenticação.';
COMMENT ON COLUMN usuarios.id IS 'Identificador único do usuário (UUID)';
COMMENT ON COLUMN usuarios.username IS 'Nome de usuário único para login';
COMMENT ON COLUMN usuarios.email IS 'Email único do usuário';
COMMENT ON COLUMN usuarios.senha_hash IS 'Hash BCrypt da senha (não a senha em texto plano)';
COMMENT ON COLUMN usuarios.ativo IS 'Flag indicando se o usuário está ativo (TRUE) ou desativado (FALSE)';
COMMENT ON COLUMN usuarios.ultimo_acesso IS 'Timestamp do último login do usuário';

COMMENT ON TABLE usuario_roles IS 'Tabela de relacionamento muitos-para-muitos entre usuários e roles.';
COMMENT ON COLUMN usuario_roles.usuario_id IS 'Referência ao usuário';
COMMENT ON COLUMN usuario_roles.role_id IS 'Referência à role';
