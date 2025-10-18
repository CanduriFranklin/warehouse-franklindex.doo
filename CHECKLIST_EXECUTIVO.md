# ✅ CHECKLIST EXECUTIVO - REFATORAÇÃO DE AUTENTICAÇÃO

## 🎯 OBJETIVO FINAL
Migrar sistema de autenticação de **InMemory** para **Banco de Dados Relacional**, criando um sistema escalável, seguro, sustentável e pronto para produção.

---

## 📋 PRÉ-REQUISITOS (ANTES DE COMEÇAR)

- [ ] Código está no git (branch main)
- [ ] Branch `refactor/auth-to-database` criado
- [ ] Backup do banco de dados realizado
- [ ] Todos os testes atuais passam
- [ ] Nenhuma alteração pending no git
- [ ] Acesso aos documentos:
  - [ ] PLANO_REFATORACAO_AUTENTICACAO.md
  - [ ] ROADMAP_REFATORACAO.md
  - [ ] ANALISE_AUTENTICACAO_E_REGISTRO.md

---

## 🏗️ FASE 1: BANCO DE DADOS

### 1.1 Criar Migração V4 - Estrutura de Dados
```
Arquivo: backend/src/main/resources/db/migration/V4__Create_usuario_e_roles.sql

Conteúdo:
├─ CREATE TABLE roles (id, nome, descricao, criado_em, atualizado_em)
├─ CREATE TABLE usuarios (id, username, email, senha_hash, ativo, ultimo_acesso, criado_em, atualizado_em)
├─ CREATE TABLE usuario_roles (usuario_id, role_id)
├─ CREATE INDEX idx_usuarios_username
├─ CREATE INDEX idx_usuarios_email
├─ CREATE INDEX idx_usuarios_ativo
├─ CREATE FUNCTION update_atualizado_em()
└─ CREATE TRIGGER trg_usuarios_atualizado_em
```

- [ ] Arquivo criado
- [ ] SQL validado
- [ ] Sem erros de sintaxe

### 1.2 Criar Migração V5 - Roles Padrão
```
Arquivo: backend/src/main/resources/db/migration/V5__Seed_default_roles.sql

Conteúdo:
├─ INSERT INTO roles: ADMIN, WAREHOUSE_MANAGER, SALES, CUSTOMER
└─ ON CONFLICT DO NOTHING
```

- [ ] Arquivo criado
- [ ] 4 roles inseridas
- [ ] SQL validado

### 1.3 Criar Migração V6 - Migrar Usuários Dev
```
Arquivo: backend/src/main/resources/db/migration/V6__Migrate_dev_users.sql

Conteúdo:
├─ INSERT INTO usuarios: admin, manager, sales (com senhas hasheadas)
└─ INSERT INTO usuario_roles: relacionamentos
```

- [ ] Arquivo criado
- [ ] Usuários inseridos (3)
- [ ] Relacionamentos criados (3)
- [ ] SQL validado

### 1.4 Testar Migrações Localmente
```
Comando:
  ./gradlew bootRun

Verificações:
├─ Spring Boot inicia sem erros
├─ Migrações Flyway executam
├─ Tabelas criadas no BD
├─ Roles inseridas (SELECT * FROM roles; → 4 linhas)
├─ Usuários inseridos (SELECT * FROM usuarios; → 3 linhas)
└─ Relacionamentos criados (SELECT * FROM usuario_roles; → 3 linhas)
```

- [ ] Migrações executadas
- [ ] Tabelas criadas
- [ ] Dados inseridos corretamente
- [ ] Sem erros de Flyway

---

## 👨‍💻 FASE 2: BACKEND - ENTIDADES

### 2.1 Criar Entidade Usuario.java
```
Arquivo: src/main/java/br/com/dio/warehouse/domain/model/Usuario.java

Conteúdo:
├─ @Entity @Table(name = "usuarios")
├─ Atributos: id, username, email, senhaHash, ativo, ultimoAcesso, criadoEm, atualizadoEm
├─ @ManyToMany com Role
├─ Builders, constructors, getters, setters
└─ Validações
```

- [ ] Arquivo criado
- [ ] Compilação sucede
- [ ] Sem warnings
- [ ] Anotações JPA corretas

### 2.2 Criar Entidade Role.java
```
Arquivo: src/main/java/br/com/dio/warehouse/domain/model/Role.java

Conteúdo:
├─ @Entity @Table(name = "roles")
├─ Atributos: id, nome (RoleEnum), descricao, criadoEm, atualizadoEm
├─ @Enumerated(EnumType.STRING)
└─ Builders, constructors, getters, setters
```

- [ ] Arquivo criado
- [ ] Compilação sucede
- [ ] RoleEnum criado (ADMIN, WAREHOUSE_MANAGER, SALES, CUSTOMER)
- [ ] Sem warnings

### 2.3 Criar Repositório UsuarioRepository.java
```
Arquivo: src/main/java/br/com/dio/warehouse/infrastructure/persistence/UsuarioRepository.java

Conteúdo:
├─ extends JpaRepository<Usuario, UUID>
├─ findByUsername(String username): Optional<Usuario>
├─ findByEmail(String email): Optional<Usuario>
├─ existsByUsername(String username): boolean
├─ existsByEmail(String email): boolean
└─ findAllByAtivoTrue(): List<Usuario>
```

- [ ] Arquivo criado
- [ ] Compilação sucede
- [ ] Métodos corretos
- [ ] Sem warnings

### 2.4 Criar Repositório RoleRepository.java
```
Arquivo: src/main/java/br/com/dio/warehouse/infrastructure/persistence/RoleRepository.java

Conteúdo:
├─ extends JpaRepository<Role, Long>
├─ findByNome(RoleEnum nome): Optional<Role>
└─ findAll(): List<Role>
```

- [ ] Arquivo criado
- [ ] Compilação sucede
- [ ] Sem warnings

### 2.5 Compilar Backend
```
Comando: ./gradlew clean build -x test

Resultado:
├─ BUILD SUCCESSFUL
├─ Sem erros
├─ Sem warnings
└─ Todas as entidades reconhecidas
```

- [ ] Build bem-sucedido
- [ ] Sem erros de compilação
- [ ] Sem erros de JPA

---

## 🔐 FASE 3: BACKEND - SERVICES E ENDPOINTS

### 3.1 Criar DatabaseUserDetailsService.java
```
Arquivo: src/main/java/br/com/dio/warehouse/infrastructure/security/DatabaseUserDetailsService.java

Conteúdo:
├─ implements UserDetailsService
├─ loadUserByUsername(String username): UserDetails
│  ├─ Busca usuario no BD
│  ├─ Verifica se está ativo
│  ├─ Carrega roles
│  └─ Retorna User do Spring Security
├─ @Service
└─ Sem @Deprecated InMemoryUserDetailsService
```

- [ ] Arquivo criado
- [ ] Compilação sucede
- [ ] @Service anotado
- [ ] UserDetailsService implementado

### 3.2 Criar UsuarioRegistrationService.java
```
Arquivo: src/main/java/br/com/dio/warehouse/application/service/UsuarioRegistrationService.java

Conteúdo:
├─ registrarNovoUsuario(RegisterRequest): Usuario
│  ├─ Valida campos
│  ├─ Valida igualdade de senhas
│  ├─ Verifica duplicatas (username, email)
│  ├─ Hasheia senha com BCrypt
│  ├─ Salva no BD
│  ├─ Atribui role padrão (SALES)
│  └─ Retorna Usuario criado
├─ @Service
└─ @Transactional
```

- [ ] Arquivo criado
- [ ] Compilação sucede
- [ ] Validações implementadas
- [ ] BCrypt configurado

### 3.3 Criar RegisterRequest DTO
```
Arquivo: src/main/java/br/com/dio/warehouse/adapter/in/web/dto/auth/RegisterRequest.java

Conteúdo:
├─ record ou class
├─ username (@NotBlank, @Size(3-50))
├─ email (@NotBlank, @Email)
├─ password (@NotBlank, @Size(8-100))
├─ passwordConfirm (@NotBlank)
├─ nome (opcional)
├─ cpf (opcional)
└─ telefone (opcional)
```

- [ ] Arquivo criado
- [ ] Anotações de validação presentes
- [ ] Compilação sucede

### 3.4 Adicionar Endpoint /auth/register
```
Arquivo: src/main/java/br/com/dio/warehouse/adapter/in/web/controller/AuthenticationController.java

Novo método:
├─ @PostMapping("/register")
├─ Recebe: RegisterRequest
├─ Processa: UsuarioRegistrationService
├─ Faz login automático
├─ Retorna: JwtAuthenticationResponse
└─ Status: 201 Created ou erro apropriado
```

- [ ] Método adicionado
- [ ] @PostMapping configurado
- [ ] Validação (@Valid) presente
- [ ] Tratamento de exceções
- [ ] Compilação sucede

### 3.5 Teste Unitário Backend
```
Comando: ./gradlew test

Resultado:
├─ Todos os testes passam
├─ DatabaseUserDetailsService: OK
├─ UsuarioRegistrationService: OK
├─ AuthenticationController: OK
└─ Coverage: >90%
```

- [ ] Todos os testes passam
- [ ] Sem falhas
- [ ] Coverage adequado

---

## ⚙️ FASE 4: CONFIGURAÇÃO SPRING

### 4.1 Atualizar SecurityConfig.java
```
Arquivo: src/main/java/br/com/dio/warehouse/infrastructure/config/SecurityConfig.java

Alterações:
├─ @Bean UserDetailsService
│  ├─ Remover: InMemoryUserDetailsService()
│  └─ Adicionar: return userDetailsService; (injected)
├─ @Bean PasswordEncoder
│  └─ Manter: BCryptPasswordEncoder
├─ @Bean AuthenticationManager
│  └─ Manter inalterado
└─ CORS, CSRF, HTTP config
   └─ Manter inalterado
```

- [ ] Arquivo modificado
- [ ] InMemory removido/comentado
- [ ] Database bean configurado
- [ ] Compilação sucede
- [ ] Sem warnings

### 4.2 Remover InMemoryUserDetailsService
```
Arquivo: src/main/java/br/com/dio/warehouse/infrastructure/security/InMemoryUserDetailsService.java

Ação:
├─ Remover @Service anotation (comentar ou deletar)
└─ Ou deletar arquivo completamente
```

- [ ] InMemory desabilitado/removido
- [ ] Compilação sucede
- [ ] DatabaseUserDetailsService é único UserDetailsService

### 4.3 Atualizar application-dev.yml
```
Arquivo: src/main/resources/application-dev.yml

Remover:
├─ dev.users.admin.password
├─ dev.users.manager.password
└─ dev.users.sales.password

Manter:
├─ datasource config
├─ jpa config
├─ flyway config
└─ logging config
```

- [ ] Arquivo modificado
- [ ] Linhas dev.users.* removidas
- [ ] Arquivo mantém validade

### 4.4 Compilar Backend
```
Comando: ./gradlew clean build -x test

Resultado:
├─ BUILD SUCCESSFUL
├─ Sem erros
├─ Sem warnings
└─ SecurityConfig compilado
```

- [ ] Build bem-sucedido
- [ ] Sem erros de configuração
- [ ] Sem warnings de segurança

---

## 🎨 FASE 5: FRONTEND

### 5.1 Atualizar AuthContext.tsx
```
Arquivo: src/context/AuthContext.tsx

Adicionar função:
├─ register(username, email, password): Promise<void>
│  ├─ POST http://localhost:8080/api/v1/auth/register
│  ├─ Enviar: { username, email, password, passwordConfirm, ... }
│  ├─ Receber: { token, username, roles, expiresAt }
│  ├─ Salvar: localStorage (token, user)
│  └─ Definir: state (user, token)
└─ Exportar: register function
```

- [ ] Arquivo modificado
- [ ] Função register adicionada
- [ ] Chamada à API correta
- [ ] localStorage atualizado
- [ ] Sem erros de compilação

### 5.2 Atualizar AuthPage.tsx
```
Arquivo: src/pages/AuthPage.tsx

Modificar handleRegister():
├─ Validar senhas iguais
├─ Validar senha (8+ chars)
├─ Chamar: register(username, email, password)
├─ Capturar: erros e exibir ao usuário
├─ Redirecionar: navigate('/dashboard')
└─ Loading state visual
```

- [ ] Arquivo modificado
- [ ] handleRegister atualizado
- [ ] Validações presentes
- [ ] Tratamento de erros
- [ ] Sem erros de compilação

### 5.3 Testar Frontend Localmente
```
Comando: npm run dev (na pasta frontend)

Verificações:
├─ Página de login renderiza
├─ Página de registro renderiza
├─ Botão de toggle login/register funciona
├─ Formulário de registro com campos corretos
├─ Validações funcionam (visual feedback)
├─ Console sem erros
└─ Conexão com API funciona
```

- [ ] Frontend dev server rodando
- [ ] Páginas renderizando corretamente
- [ ] Sem erros no console
- [ ] Sem erros de compilação TypeScript

---

## 🧪 FASE 6: TESTES COMPLETOS

### 6.1 Testes Backend - Login Existente
```
Cenários:
├─ Login com admin / Admin2025Secure ✅
├─ Login com manager / Manager2025Secure ✅
├─ Login com sales / Sales2025Secure ✅
├─ Login com username errado ❌ (401)
├─ Login com password errada ❌ (401)
└─ Verificar token JWT gerado
```

**Testes:**
- [ ] Admin login OK
- [ ] Manager login OK
- [ ] Sales login OK
- [ ] Invalid credentials retorna 401
- [ ] Token JWT válido

### 6.2 Testes Backend - Registro Novo
```
Cenários:
├─ Registrar novo usuário ✅ (201)
├─ Registrar com username duplicado ❌ (409)
├─ Registrar com email duplicado ❌ (409)
├─ Registrar com senhas diferentes ❌ (400)
├─ Registrar com senha < 8 chars ❌ (400)
├─ Fazer login automático após registro ✅
└─ Token JWT recebido
```

**Testes:**
- [ ] Novo usuário registrado OK
- [ ] Username duplicado recusado
- [ ] Email duplicado recusado
- [ ] Validação de senha
- [ ] Login automático funciona
- [ ] Token gerado

### 6.3 Testes Frontend - Login
```
Cenários:
├─ Preencher formulário login
├─ Clicar "Entrar"
├─ Sucesso: redirect para /dashboard ✅
├─ Erro: mensagem de erro exibida ❌
├─ Token salvo em localStorage
└─ User info salvo em localStorage
```

**Testes:**
- [ ] Formulário de login funciona
- [ ] Sucesso redireciona
- [ ] Erro mostra mensagem
- [ ] localStorage atualizado

### 6.4 Testes Frontend - Registro
```
Cenários:
├─ Clicar em "Criar Conta"
├─ Preencher formulário
├─ Validações em real-time (senha, confirmação)
├─ Clicar "Registrar"
├─ Sucesso: redirect para /dashboard ✅
├─ Erro: mensagem de erro exibida ❌
└─ Login automático funciona
```

**Testes:**
- [ ] Tab de registro funciona
- [ ] Formulário renderiza campos corretos
- [ ] Validações visual feedback
- [ ] Sucesso redireciona
- [ ] Erro mostra mensagem
- [ ] Login automático OK

### 6.5 Teste de Regressions
```
Verificar que nada quebrou:
├─ Listar produtos: OK
├─ Adicionar ao carrinho: OK
├─ Ver pedidos: OK (se autenticado)
├─ Logout funciona: OK
├─ Refresh página mantém login: OK
├─ Sem console errors: OK
└─ Performance OK
```

**Testes:**
- [ ] Produtos listam
- [ ] Carrinho funciona
- [ ] Pedidos funcionam
- [ ] Logout OK
- [ ] Persistência session OK
- [ ] Sem erros console
- [ ] Performance OK

---

## 📝 FASE 7: DOCUMENTAÇÃO E FINALIZAÇÃO

### 7.1 Atualizar Documentação
- [ ] README.md: adicionar seção sobre autenticação com BD
- [ ] CHANGELOG.md: descrever breaking changes (se houver)
- [ ] API Docs (Swagger): verificar /auth/register documentado
- [ ] MIGRATION_GUIDE.md: criar guia de migração de dados

### 7.2 Git Workflow
```
Comandos:
├─ git add .
├─ git commit -m "refactor: migrate authentication to database"
├─ git push origin refactor/auth-to-database
├─ Criar Pull Request
├─ Code review
├─ Merge para main
└─ git tag v2.0.0-auth-database
```

- [ ] Todos os arquivos staged
- [ ] Commit message descritiva
- [ ] Push para branch
- [ ] PR criado
- [ ] Code review completo
- [ ] Merge OK
- [ ] Tag criado

### 7.3 Build e Docker
```
Comandos:
├─ ./gradlew clean build (verificar jar)
├─ docker compose build --no-cache warehouse
├─ docker compose down && docker compose --profile dev up -d
├─ Aguardar 4-5 minutos para inicialização
└─ Verificar login funciona
```

- [ ] Backend compila OK
- [ ] Docker image rebuilt
- [ ] Container inicia OK
- [ ] Aplicação saudável
- [ ] Endpoints funcionam

---

## ✅ CRITÉRIO DE SUCESSO FINAL

Sistema será considerado **100% SUCESSO** quando:

**Backend:**
- [ ] Todos os 3 usuários de dev (admin, manager, sales) fazem login ✅
- [ ] Novo usuário pode registrar via `/api/v1/auth/register` ✅
- [ ] Novo usuário recebe token JWT válido após registro ✅
- [ ] Novo usuário pode fazer login com credenciais registradas ✅
- [ ] Roles e permissões funcionam corretamente ✅
- [ ] BCrypt hashing confirmado no BD ✅
- [ ] Migrações Flyway executadas com sucesso ✅

**Frontend:**
- [ ] Página de login renderiza corretamente ✅
- [ ] Página de registro renderiza corretamente ✅
- [ ] Login com admin funciona e redireciona ✅
- [ ] Registro de novo usuário funciona ✅
- [ ] Novo usuário consegue fazer login após registro ✅
- [ ] Logout funciona ✅
- [ ] Refresh mantém sessão (localStorage) ✅

**Qualidade:**
- [ ] Todos os testes passam (>90% coverage) ✅
- [ ] Sem console errors ou warnings ✅
- [ ] Sem erros de compilação backend ✅
- [ ] Sem erros de compilação frontend ✅
- [ ] Código segue padrões do projeto ✅

**Documentação:**
- [ ] Plano executado conforme PLANO_REFATORACAO_AUTENTICACAO.md ✅
- [ ] Changelog atualizado ✅
- [ ] README atualizado ✅
- [ ] API Docs atualizados ✅

**DevOps:**
- [ ] Build Docker sucede ✅
- [ ] Container inicia sem erros ✅
- [ ] Endpoints acessíveis ✅
- [ ] Sem regressions em outras funcionalidades ✅

---

## 📞 CONTATO DE SUPORTE

Se durante a execução encontrar:

| Problema | Solução |
|----------|---------|
| **Erro ao compilar** | Revisar imports, verificar entidades |
| **Migração falha** | Verificar sintaxe SQL, testar localmente |
| **Login não funciona** | Verificar BD (SELECT * FROM usuarios) |
| **Senha não faz hash** | Confirmar PasswordEncoder configuration |
| **Frontend erro** | Verificar console (F12), verificar API URL |
| **Token inválido** | Verificar JwtTokenProvider configuration |
| **CORS error** | Verificar SecurityConfig CORS |

---

## 🎊 PARABÉNS!

Ao completar todas as checagens, seu sistema estará:

✅ **Escalável** - Suporta crescimento de usuários
✅ **Seguro** - Senhas hasheadas, roles granulares, JWT
✅ **Sustentável** - Código limpo, testado, documentado
✅ **Pronto para Produção** - Sem débitos técnicos

**Data de Início:** ________________
**Data de Conclusão:** ________________
**Executado por:** ________________

