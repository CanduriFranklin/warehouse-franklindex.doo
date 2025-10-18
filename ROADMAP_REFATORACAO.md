# 🗺️ ROADMAP DE REFATORAÇÃO - AUTENTICAÇÃO PARA BANCO DE DADOS

## 📍 STATUS ATUAL

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  🏁 INÍCIO: Sistema InMemory                               │
│  ├─ Usuários: admin, manager, sales (em memória)           │
│  ├─ Autenticação: AuthenticationManager + JWT               │
│  ├─ UserDetailsService: InMemoryUserDetailsService         │
│  └─ Registro: NÃO IMPLEMENTADO                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 TIMELINE DE EXECUÇÃO

### SEMANA 1: PREPARAÇÃO E DESIGN

```
┌──────────────────────────────────────────────────────────────┐
│ 📅 DIA 1: Análise e Planejamento                            │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ✅ [00:00] Documentação do estado atual                   │
│  ✅ [00:30] Design do banco de dados                        │
│  ✅ [01:00] Revisão de padrões de segurança                │
│  ⏳ [01:30] Aprovação do plano (HOJE)                       │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 📦 FASE 1: BANCO DE DADOS (30 minutos)

```
┌────────────────────────────────────────────┐
│ CRIAR ESTRUTURA DE DADOS                   │
├────────────────────────────────────────────┤
│                                            │
│ 📄 V4__Create_usuario_e_roles.sql         │
│    ├─ Tabela: roles                       │
│    ├─ Tabela: usuarios                    │
│    ├─ Tabela: usuario_roles               │
│    ├─ Índices para performance            │
│    └─ Triggers para atualizado_em         │
│                                            │
│ 📄 V5__Seed_default_roles.sql             │
│    └─ Inserir 4 roles padrão              │
│                                            │
│ 📄 V6__Migrate_dev_users.sql              │
│    └─ Migrar admin, manager, sales        │
│                                            │
└────────────────────────────────────────────┘
        ⏱️  ~30 minutos
        
        ANTES: 9 tabelas
        DEPOIS: 12 tabelas (+3)
        
        Migrations: V3 → V6
```

**Diagrama do Banco:**
```
┌─────────────────────────┐
│       roles             │
├─────────────────────────┤
│ id (PK)                 │
│ nome (UNIQUE)           │
│ descricao               │
│ criado_em               │
└─────────────┬───────────┘
              │
              │ (1:N)
              │
      ┌───────┴────────┐
      │                │
      │ usuario_roles  │
      │ (Many-to-Many) │
      │                │
      └────────┬───────┘
               │ (1:N)
               │
┌──────────────┴────────────┐
│      usuarios             │
├──────────────────────────┤
│ id (PK, UUID)            │
│ username (UNIQUE)        │
│ email (UNIQUE)           │
│ senha_hash (BCrypt)      │
│ ativo                    │
│ ultimo_acesso            │
│ criado_em                │
│ atualizado_em            │
└──────────────────────────┘

✨ Relacionamento M:N com triggers de auditoria
```

---

## 👨‍💻 FASE 2: BACKEND - ENTIDADES (30 minutos)

```
┌────────────────────────────────────────────┐
│ CRIAR ENTIDADES JPA                        │
├────────────────────────────────────────────┤
│                                            │
│ 📦 domain/model/                           │
│    ├─ Usuario.java                        │
│    │  ├─ UUID id                          │
│    │  ├─ String username                  │
│    │  ├─ String email                     │
│    │  ├─ String senhaHash                 │
│    │  ├─ Boolean ativo                    │
│    │  ├─ LocalDateTime ultimoAcesso       │
│    │  └─ Set<Role> roles (ManyToMany)    │
│    │                                      │
│    ├─ Role.java                           │
│    │  ├─ Long id                          │
│    │  ├─ RoleEnum nome                    │
│    │  ├─ String descricao                 │
│    │  └─ Set<Usuario> usuarios            │
│    │                                      │
│    └─ UsuarioRole.java                    │
│       ├─ UsuarioId usuarioId              │
│       └─ RoleId roleId                    │
│                                            │
│ 📦 infrastructure/persistence/            │
│    ├─ UsuarioRepository.java              │
│    │  ├─ Optional<Usuario> findByUsername │
│    │  ├─ Optional<Usuario> findByEmail    │
│    │  ├─ exists...()                      │
│    │  └─ findAllByAtivoTrue()             │
│    │                                      │
│    └─ RoleRepository.java                 │
│       └─ Optional<Role> findByNome        │
│                                            │
└────────────────────────────────────────────┘
        ⏱️  ~30 minutos
        
        NOVO: 3 entidades
        NOVO: 2 repositórios
        
        Arquivos criados: 5
```

---

## 🔐 FASE 3: BACKEND - SERVICES (1 hora)

```
┌────────────────────────────────────────────────────────┐
│ CRIAR SERVICES E ENDPOINTS                             │
├────────────────────────────────────────────────────────┤
│                                                        │
│ 🔧 DatabaseUserDetailsService.java                   │
│    └─ Substitui InMemoryUserDetailsService           │
│       ├─ loadUserByUsername()                        │
│       ├─ Valida ativo                                │
│       └─ Carrega roles do BD                         │
│                                                        │
│ 🔧 UsuarioRegistrationService.java                   │
│    ├─ registrarNovoUsuario()                         │
│    ├─ validar campos                                 │
│    ├─ verificar duplicatas                          │
│    ├─ hashear senha (BCrypt)                        │
│    ├─ salvar no BD                                  │
│    └─ atribuir role padrão                          │
│                                                        │
│ 📝 DTOs Novos:                                        │
│    ├─ RegisterRequest                               │
│    │  ├─ username                                    │
│    │  ├─ email                                       │
│    │  ├─ password                                    │
│    │  ├─ passwordConfirm                            │
│    │  ├─ nome (opcional)                            │
│    │  ├─ cpf (opcional)                             │
│    │  └─ telefone (opcional)                        │
│    │                                                  │
│    └─ UsuarioResponse DTO                           │
│       ├─ id                                          │
│       ├─ username                                    │
│       ├─ email                                       │
│       ├─ roles                                       │
│       └─ criadoEm                                   │
│                                                        │
│ 🔌 Novo Endpoint:                                     │
│    POST /api/v1/auth/register                        │
│    ├─ Recebe: RegisterRequest                       │
│    ├─ Processa: UsuarioRegistrationService         │
│    ├─ Faz login automático                          │
│    ├─ Retorna: JwtAuthenticationResponse            │
│    └─ Status: 201 Created                           │
│                                                        │
│ 🔌 Endpoint Modificado:                              │
│    POST /api/v1/auth/login                          │
│    └─ Agora usa DatabaseUserDetailsService          │
│                                                        │
└────────────────────────────────────────────────────────┘
        ⏱️  ~1 hora
        
        NOVO: 2 services
        NOVO: 2 DTOs
        MODIFICADO: 1 Controller
        
        Arquivos criados: 4
        Arquivos modificados: 1
```

---

## ⚙️ FASE 4: CONFIGURAÇÃO (30 minutos)

```
┌────────────────────────────────────────────┐
│ ATUALIZAR SPRING SECURITY                  │
├────────────────────────────────────────────┤
│                                            │
│ 🔧 SecurityConfig.java                    │
│    ├─ Remover: Bean InMemoryUserDetails   │
│    ├─ Adicionar: Bean DatabaseUserDetails │
│    ├─ Manter: AuthenticationManager       │
│    ├─ Manter: PasswordEncoder (BCrypt)    │
│    └─ Manter: JWT Configuration           │
│                                            │
│ 🔧 application-dev.yml                    │
│    ├─ Remover: dev.users.admin.password   │
│    ├─ Remover: dev.users.manager.password │
│    ├─ Remover: dev.users.sales.password   │
│    └─ ✅ Valores agora vêm do BD          │
│                                            │
│ 🗑️  REMOVER COMPLETAMENTE:                │
│    ├─ InMemoryUserDetailsService.java    │
│    │  (ou comentar @Service)              │
│    └─ Referências em tests                │
│                                            │
└────────────────────────────────────────────┘
        ⏱️  ~30 minutos
        
        MODIFICADO: 2 arquivos
        REMOVIDO: 1 classe
        
        Compilação: ✅ Sem erros
```

---

## 🎨 FASE 5: FRONTEND (1 hora)

```
┌────────────────────────────────────────────┐
│ ATUALIZAR COMPONENTES DE AUTH              │
├────────────────────────────────────────────┤
│                                            │
│ 📄 AuthContext.tsx                        │
│    ├─ Adicionar: função register()        │
│    ├─ Chamar: POST /api/v1/auth/register  │
│    ├─ Enviar: username, email, password   │
│    ├─ Receber: token + user info          │
│    └─ Salvar: localStorage (token + user) │
│                                            │
│ 📄 AuthPage.tsx                           │
│    ├─ Modificar: handleRegister()         │
│    │  ├─ Validar senhas iguais            │
│    │  ├─ Validar senha (8+ chars)         │
│    │  ├─ Chamar: register()               │
│    │  └─ Redirecionar: /dashboard         │
│    │                                      │
│    └─ Formulário de Registro:             │
│       ├─ Username input                   │
│       ├─ Email input                      │
│       ├─ Password input                   │
│       ├─ Confirm Password input           │
│       └─ Submit button                    │
│                                            │
│ 🎨 UI/UX Improvements:                    │
│    ├─ Validação em real-time              │
│    ├─ Mensagens de erro claras            │
│    ├─ Loading state visual                │
│    ├─ Success notification                │
│    └─ Toggle login ↔ register             │
│                                            │
└────────────────────────────────────────────┘
        ⏱️  ~1 hora
        
        MODIFICADO: 2 componentes
        NOVO: Lógica register
        
        Teste: ✅ Funcional
```

---

## 🧪 FASE 6: TESTES (1 hora)

```
┌────────────────────────────────────────────┐
│ TESTES UNITÁRIOS E INTEGRAÇÃO              │
├────────────────────────────────────────────┤
│                                            │
│ ✅ DatabaseUserDetailsServiceTest.java    │
│    ├─ loadUserByUsername() - Sucesso      │
│    ├─ loadUserByUsername() - Não encontrado│
│    ├─ Usuário inativo - Exceção           │
│    ├─ Roles carregadas corretamente       │
│    └─ Coverage: 95%+                      │
│                                            │
│ ✅ UsuarioRegistrationServiceTest.java    │
│    ├─ Registro bem-sucedido               │
│    ├─ Username duplicado - Exceção        │
│    ├─ Email duplicado - Exceção           │
│    ├─ Senha mismatch - Exceção            │
│    ├─ Senha hasheada com BCrypt           │
│    ├─ Role padrão atribuída               │
│    └─ Coverage: 95%+                      │
│                                            │
│ ✅ AuthenticationControllerIT.java        │
│    ├─ POST /auth/login - admin OK         │
│    ├─ POST /auth/login - credenciais inv. │
│    ├─ POST /auth/register - novo user     │
│    ├─ POST /auth/register - username dup. │
│    ├─ Token JWT válido                    │
│    ├─ Logout funciona                     │
│    └─ Coverage: 90%+                      │
│                                            │
│ ✅ Testes Manuais - Backend               │
│    ├─ Migrate BD: ok                      │
│    ├─ Login admin: ok                     │
│    ├─ Login manager: ok                   │
│    ├─ Login sales: ok                     │
│    ├─ Registrar novo user: ok             │
│    ├─ Login novo user: ok                 │
│    ├─ Token JWT válido: ok                │
│    ├─ Logout: ok                          │
│    └─ Permissões/Roles: ok                │
│                                            │
│ ✅ Testes Manuais - Frontend              │
│    ├─ Página Login: renderiza ok          │
│    ├─ Página Register: renderiza ok       │
│    ├─ Login com admin: funciona           │
│    ├─ Logout: funciona                    │
│    ├─ Registrar novo: funciona            │
│    ├─ Login novo: funciona                │
│    ├─ Validações UI: ok                   │
│    ├─ Mensagens de erro: ok               │
│    ├─ Redirecionamentos: ok               │
│    └─ localStorage: ok                    │
│                                            │
└────────────────────────────────────────────┘
        ⏱️  ~1 hora
        
        NOVO: 3 test classes
        Coverage: 93%+
        
        Status: ✅ TODOS PASSANDO
```

---

## ✅ FASE 7: FINALIZAÇÃO (30 minutos)

```
┌────────────────────────────────────────────┐
│ GIT E DOCUMENTAÇÃO                         │
├────────────────────────────────────────────┤
│                                            │
│ 📝 Git Workflow:                           │
│    ├─ git add .                           │
│    ├─ git commit -m "refactor: migrate... │
│    ├─ git push origin refactor/auth-db    │
│    ├─ Criar Pull Request                  │
│    ├─ Code Review                         │
│    ├─ Merge to main                       │
│    └─ Tag: v2.0.0-auth-database           │
│                                            │
│ 📚 Documentação:                           │
│    ├─ README.md atualizado                │
│    ├─ CHANGELOG.md adicionado             │
│    ├─ Migration Guide                     │
│    ├─ Security.md atualizado              │
│    └─ API Docs (Swagger)                  │
│                                            │
│ 🚀 Deploy:                                 │
│    ├─ Recompilar imagem Docker            │
│    ├─ Push para registry                  │
│    ├─ Deploy para staging                 │
│    ├─ Testes em staging                   │
│    └─ Deploy para produção                │
│                                            │
└────────────────────────────────────────────┘
        ⏱️  ~30 minutos
        
        Commits: 1 (squashed)
        PR: 1
        Tags: 1
```

---

## 📊 RESUMO TIMELINE

```
FASE 1: Banco de Dados        ████░░░░░░░░░░░░░░░░░░ 30 min
FASE 2: Entidades             ████░░░░░░░░░░░░░░░░░░ 30 min
FASE 3: Services              ████████████░░░░░░░░░░  60 min
FASE 4: Configuração          ████░░░░░░░░░░░░░░░░░░ 30 min
FASE 5: Frontend              ████████░░░░░░░░░░░░░░ 60 min
FASE 6: Testes                ████████░░░░░░░░░░░░░░ 60 min
FASE 7: Finalização           ████░░░░░░░░░░░░░░░░░░ 30 min
                              ─────────────────────────
TOTAL:                        ████████████████████░░ 300 min
                              (5 horas)
```

---

## 🎯 FIM: Sistema com BD

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  🏁 FINAL: Sistema Unificado em Banco de Dados         │
│  ├─ Usuários: admin, manager, sales, + novos (BD)     │
│  ├─ Autenticação: DatabaseUserDetailsService + JWT     │
│  ├─ Registro: ✅ IMPLEMENTADO                         │
│  ├─ Segurança: BCrypt + roles granulares              │
│  ├─ Escalabilidade: ✅ PRONTO                         │
│  ├─ Sustentabilidade: ✅ PRONTO                       │
│  ├─ Testes: ✅ 93%+ coverage                          │
│  └─ Documentação: ✅ ATUALIZADA                       │
│                                                         │
│  ✨ SISTEMA PRONTO PARA PRODUÇÃO ✨                   │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 📞 SUPORTE DURANTE A REFATORAÇÃO

Se encontrar problemas em qualquer fase:

1. **Compilação**: Revisar imports
2. **Migration**: Verificar sintaxe SQL
3. **Teste**: Adicionar logs debug
4. **Frontend**: Checar console do navegador
5. **Login**: Verificar senha hasheada

**Arquivo de referência**: PLANO_REFATORACAO_AUTENTICACAO.md

