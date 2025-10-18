# 🏗️ PLANO CIRÚRGICO: REFATORAÇÃO DE AUTENTICAÇÃO PARA BANCO DE DADOS

## 📋 OBJETIVO
Migrar sistema de autenticação de **InMemory** para **Banco de Dados Relacional**, unificando todos os usuários (admin, manager, sales, clientes) em um único sistema escalável, seguro e sustentável.

---

## 📊 ARQUITETURA ATUAL vs FUTURA

### ANTES (InMemory)
```
Autenticação:
  └─ InMemoryUserDetailsService
     ├─ admin (memory)
     ├─ manager (memory)
     └─ sales (memory)

Clientes:
  └─ Tabela: clientes (sem autenticação)
```

### DEPOIS (Banco de Dados)
```
Autenticação Unificada:
  └─ DatabaseUserDetailsService
     ├─ Tabela: usuarios
     │  ├─ admin (BD + roles)
     │  ├─ manager (BD + roles)
     │  ├─ sales (BD + roles)
     │  └─ clientes (BD + roles)
     └─ UserRole (tabela de relacionamento)

Clientes:
  └─ Tabela: clientes (estendida com autenticação)
```

---

## 🔧 FASES DE IMPLEMENTAÇÃO

### FASE 1: Design do Banco de Dados
**Duração: 30 minutos**
**Arquivos:**
- V4__Create_usuario_e_roles.sql (nova migração Flyway)
- V5__Migrate_dev_users.sql (migração de dados)

**Tarefas:**
```
1.1. Criar tabela `usuarios`
     └─ Substitui InMemory completamente
     
1.2. Criar tabela `usuario_roles` (Many-to-Many)
     └─ Relacionamento: usuarios ←→ roles
     
1.3. Criar tabela `roles`
     └─ ROLE_ADMIN, ROLE_WAREHOUSE_MANAGER, ROLE_SALES
     
1.4. Adicionar colunas de auditoria
     └─ criado_em, atualizado_em, ativo, ultimo_acesso
     
1.5. Adicionar índices para performance
     └─ username UNIQUE, email UNIQUE, ativo
```

---

### FASE 2: Implementação Backend
**Duração: 2-3 horas**

#### 2.1 Criar Entidades JPA
**Arquivos a criar:**
```
src/main/java/br/com/dio/warehouse/
  └─ domain/
     └─ model/
        ├─ Usuario.java (Entity)
        ├─ Role.java (Entity)
        └─ UsuarioRole.java (Entity - Composite)
```

**Estrutura Usuario.java:**
```java
@Entity
@Table(name = "usuarios")
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  
  @Column(nullable = false, unique = true, length = 50)
  private String username;
  
  @Column(nullable = false, unique = true, length = 100)
  private String email;
  
  @Column(nullable = false, length = 255)
  private String senhaHash; // BCrypt
  
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "usuario_roles",
    joinColumns = @JoinColumn(name = "usuario_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Role> roles = new HashSet<>();
  
  @Column(nullable = false)
  private Boolean ativo = true;
  
  @Column
  private LocalDateTime ultimoAcesso;
  
  @Column(nullable = false, updatable = false)
  private LocalDateTime criadoEm = LocalDateTime.now();
  
  @Column(nullable = false)
  private LocalDateTime atualizadoEm = LocalDateTime.now();
  
  // factory methods, getters, setters...
}
```

**Estrutura Role.java:**
```java
@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false, unique = true, length = 50)
  @Enumerated(EnumType.STRING)
  private RoleEnum nome; // ADMIN, WAREHOUSE_MANAGER, SALES
  
  @Column(length = 255)
  private String descricao;
  
  // getters, setters...
}
```

#### 2.2 Criar Repositórios
**Arquivos a criar:**
```
src/main/java/br/com/dio/warehouse/
  └─ infrastructure/
     └─ persistence/
        ├─ UsuarioRepository.java
        └─ RoleRepository.java
```

**UsuarioRepository.java:**
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
  Optional<Usuario> findByUsername(String username);
  Optional<Usuario> findByEmail(String email);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  List<Usuario> findAllByAtivoTrue();
}
```

#### 2.3 Implementar DatabaseUserDetailsService
**Arquivo a criar:**
```
src/main/java/br/com/dio/warehouse/
  └─ infrastructure/
     └─ security/
        └─ DatabaseUserDetailsService.java
```

**Substitui:** InMemoryUserDetailsService.java

**Estrutura:**
```java
@Service
@Slf4j
public class DatabaseUserDetailsService implements UserDetailsService {
  
  private final UsuarioRepository usuarioRepository;
  
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    
    if (!usuario.getAtivo()) {
      throw new UserDisabledException("User account is disabled");
    }
    
    Collection<GrantedAuthority> authorities = usuario.getRoles()
      .stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNome().name()))
      .collect(Collectors.toSet());
    
    return new org.springframework.security.core.userdetails.User(
      usuario.getUsername(),
      usuario.getSenhaHash(),
      authorities
    );
  }
}
```

#### 2.4 Criar DTO para Registro
**Arquivo a criar:**
```
src/main/java/br/com/dio/warehouse/
  └─ adapter/
     └─ in/
        └─ web/
           └─ dto/
              └─ auth/
                 └─ RegisterRequest.java
```

**Estrutura:**
```java
public record RegisterRequest(
  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50)
  String username,
  
  @NotBlank(message = "Email is required")
  @Email
  String email,
  
  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100)
  String password,
  
  @NotBlank(message = "Confirm password is required")
  String passwordConfirm,
  
  String nome,
  String cpf,
  String telefone
) {}
```

#### 2.5 Criar Service de Registro
**Arquivo a criar:**
```
src/main/java/br/com/dio/warehouse/
  └─ application/
     └─ service/
        └─ UsuarioRegistrationService.java
```

**Métodos:**
```java
@Service
@Slf4j
public class UsuarioRegistrationService {
  
  private final UsuarioRepository usuarioRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  
  public Usuario registrarNovoUsuario(RegisterRequest request) {
    // Validar campos
    if (!request.password().equals(request.passwordConfirm())) {
      throw new PasswordMismatchException("Passwords do not match");
    }
    
    // Verificar se username existe
    if (usuarioRepository.existsByUsername(request.username())) {
      throw new UsernameAlreadyExistsException("Username already taken");
    }
    
    // Verificar se email existe
    if (usuarioRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException("Email already registered");
    }
    
    // Criar usuario
    Usuario usuario = Usuario.builder()
      .username(request.username())
      .email(request.email())
      .senhaHash(passwordEncoder.encode(request.password()))
      .ativo(true)
      .build();
    
    // Adicionar role padrão (SALES ou CUSTOMER)
    Role rolePadrao = roleRepository.findByNome(RoleEnum.SALES)
      .orElseThrow(() -> new RoleNotFoundException("Default role not found"));
    usuario.getRoles().add(rolePadrao);
    
    return usuarioRepository.save(usuario);
  }
}
```

#### 2.6 Adicionar Endpoint de Registro
**Modificar:** AuthenticationController.java

**Novo método:**
```java
@PostMapping("/register")
public ResponseEntity<JwtAuthenticationResponse> register(
    @Valid @RequestBody RegisterRequest registerRequest
) {
  try {
    // Registrar novo usuario
    Usuario novoUsuario = usuarioRegistrationService.registrarNovoUsuario(registerRequest);
    
    // Fazer login automático
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        novoUsuario.getUsername(),
        registerRequest.password()
      )
    );
    
    SecurityContextHolder.getContext().setAuthentication(authentication);
    
    // Gerar token JWT
    String token = tokenProvider.generateToken(authentication);
    String roles = authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(","));
    
    return ResponseEntity.ok(JwtAuthenticationResponse.of(
      token,
      Instant.now().plusMillis(tokenProvider.getExpirationMs()),
      novoUsuario.getUsername(),
      roles
    ));
    
  } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
    return ResponseEntity.status(409).build(); // Conflict
  } catch (Exception e) {
    log.error("Registration failed", e);
    return ResponseEntity.status(400).build(); // Bad Request
  }
}
```

---

### FASE 3: Criar Migrações Flyway
**Duração: 1 hora**

#### 3.1 V4__Create_usuario_e_roles.sql
```sql
-- Tabela de Roles (Papéis/Funções)
CREATE TABLE roles (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(50) NOT NULL UNIQUE,
  descricao VARCHAR(255),
  criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Usuários (substitui InMemory)
CREATE TABLE usuarios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT true,
  ultimo_acesso TIMESTAMP,
  criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Relacionamento (Many-to-Many)
CREATE TABLE usuario_roles (
  usuario_id UUID NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (usuario_id, role_id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);
CREATE INDEX idx_usuario_roles_usuario ON usuario_roles(usuario_id);
CREATE INDEX idx_usuario_roles_role ON usuario_roles(role_id);

-- Função para atualizar atualizado_em automaticamente
CREATE OR REPLACE FUNCTION update_atualizado_em()
RETURNS TRIGGER AS $$
BEGIN
  NEW.atualizado_em = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para usuarios
CREATE TRIGGER trg_usuarios_atualizado_em
BEFORE UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION update_atualizado_em();

-- Trigger para roles
CREATE TRIGGER trg_roles_atualizado_em
BEFORE UPDATE ON roles
FOR EACH ROW
EXECUTE FUNCTION update_atualizado_em();
```

#### 3.2 V5__Seed_default_roles.sql
```sql
-- Inserir roles padrão
INSERT INTO roles (nome, descricao) VALUES
  ('ADMIN', 'Administrador com acesso total ao sistema'),
  ('WAREHOUSE_MANAGER', 'Gerenciador de warehouse'),
  ('SALES', 'Vendedor/Cliente padrão'),
  ('CUSTOMER', 'Cliente da loja')
ON CONFLICT (nome) DO NOTHING;
```

#### 3.3 V6__Migrate_dev_users.sql
```sql
-- Migrar usuários de desenvolvimento (com senhas hasheadas)
-- NOTA: Senhas abaixo são BCrypt hashed (10 rounds)
-- Admin2025Secure = $2a$10$...
-- Manager2025Secure = $2a$10$...
-- Sales2025Secure = $2a$10$...

INSERT INTO usuarios (username, email, senha_hash, ativo) VALUES
  ('admin', 'admin@warehouse.local', '$2a$10$YourHashedPasswordHere', true),
  ('manager', 'manager@warehouse.local', '$2a$10$YourHashedPasswordHere', true),
  ('sales', 'sales@warehouse.local', '$2a$10$YourHashedPasswordHere', true)
ON CONFLICT (username) DO NOTHING;

-- Relacionar usuários com roles
INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'admin' AND r.nome = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'manager' AND r.nome = 'WAREHOUSE_MANAGER'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_roles (usuario_id, role_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.username = 'sales' AND r.nome = 'SALES'
ON CONFLICT DO NOTHING;
```

---

### FASE 4: Atualizar Configuração Spring Security
**Duração: 30 minutos**

#### 4.1 Remover InMemoryUserDetailsService
**Ação:** Deletar arquivo ou comentar `@Service`

#### 4.2 Ativar DatabaseUserDetailsService
**Ação:** Adicionar `@Service` e garantir que seja único UserDetailsService

#### 4.3 Atualizar SecurityConfig
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
  
  private final DatabaseUserDetailsService userDetailsService;
  
  @Bean
  public UserDetailsService userDetailsService() {
    return userDetailsService; // Database-backed
  }
  
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
  
  // ... resto da config
}
```

---

### FASE 5: Atualizar Frontend
**Duração: 1 hora**

#### 5.1 AuthContext.tsx
```typescript
const register = async (username: string, email: string, password: string) => {
  try {
    const response = await axios.post('http://localhost:8080/api/v1/auth/register', {
      username,
      email,
      password,
      passwordConfirm: password,
      nome: username,
      cpf: '',
      telefone: ''
    });

    const { token, username: userName, roles } = response.data;
    
    setToken(token);
    setUser({ username: userName, roles });
    
    localStorage.setItem('auth_token', token);
    localStorage.setItem('user_data', JSON.stringify({ username: userName, roles }));
  } catch (error) {
    console.error('Erro no registro:', error);
    throw new Error('Falha ao criar conta');
  }
};
```

#### 5.2 AuthPage.tsx
```typescript
const handleRegister = async (e: React.FormEvent) => {
  e.preventDefault();
  setError('');
  
  if (registerData.password !== registerData.confirmPassword) {
    setError('As senhas não coincidem');
    return;
  }
  
  setLoading(true);
  
  try {
    await register(
      registerData.username,
      registerData.email,
      registerData.password
    );
    navigate('/dashboard');
  } catch (err) {
    setError('Erro ao criar conta. Tente novamente.');
  } finally {
    setLoading(false);
  }
};
```

---

### FASE 6: Testes e Validação
**Duração: 1 hora**

#### 6.1 Testes Unitários Backend
```
src/test/java/br/com/dio/warehouse/
  └─ infrastructure/
     └─ security/
        └─ DatabaseUserDetailsServiceTest.java
        └─ UsuarioRegistrationServiceTest.java
```

#### 6.2 Testes de Integração
```
src/test/java/br/com/dio/warehouse/
  └─ adapter/
     └─ in/
        └─ web/
           └─ controller/
              └─ AuthenticationControllerIT.java
```

#### 6.3 Testes Manuais
```
1. ✅ Login com admin/Admin2025Secure
2. ✅ Login com manager/Manager2025Secure
3. ✅ Criar novo usuário via /api/v1/auth/register
4. ✅ Login com novo usuário
5. ✅ Verificar token JWT
6. ✅ Logout
7. ✅ Tentar login com credenciais inválidas
8. ✅ Frontend: Registrar novo cliente
9. ✅ Frontend: Login com novo cliente
```

---

## 📋 CHECKLIST DE EXECUÇÃO

### ANTES DE COMEÇAR
- [ ] Fazer backup do banco de dados
- [ ] Criar branch git: `git checkout -b refactor/auth-to-database`
- [ ] Documentar estado atual dos testes

### FASE 1: Banco de Dados
- [ ] Criar V4__Create_usuario_e_roles.sql
- [ ] Criar V5__Seed_default_roles.sql
- [ ] Criar V6__Migrate_dev_users.sql
- [ ] Testar migrações localmente

### FASE 2: Backend - Entidades
- [ ] Criar Usuario.java
- [ ] Criar Role.java
- [ ] Criar UsuarioRole.java
- [ ] Criar UsuarioRepository.java
- [ ] Criar RoleRepository.java

### FASE 3: Backend - Services
- [ ] Criar DatabaseUserDetailsService.java
- [ ] Criar UsuarioRegistrationService.java
- [ ] Adicionar endpoint /api/v1/auth/register
- [ ] Adicionar DTOs (RegisterRequest, etc)

### FASE 4: Configuração
- [ ] Atualizar SecurityConfig.java
- [ ] Remover/desabilitar InMemoryUserDetailsService
- [ ] Atualizar application.yml (remover dev.users.*)
- [ ] Compilar sem erros

### FASE 5: Frontend
- [ ] Atualizar AuthContext.tsx
- [ ] Atualizar AuthPage.tsx
- [ ] Testar formulário de registro
- [ ] Testar fluxo de login

### FASE 6: Testes
- [ ] Testes unitários passando
- [ ] Testes de integração passando
- [ ] Testes manuais todos OK
- [ ] Sem regressions em funcionalidades existentes

### FINALIZAÇÃO
- [ ] Commit com mensagem descritiva
- [ ] Push para branch
- [ ] Criar Pull Request
- [ ] Code review
- [ ] Merge para main
- [ ] Deploy

---

## 🚀 ORDEM DE EXECUÇÃO RECOMENDADA

```
1️⃣  FASE 1: Banco de Dados (30 min)
    └─ Criar migrações Flyway
    └─ Testar localmente

2️⃣  FASE 2.1-2.2: Entidades + Repos (30 min)
    └─ Criar entidades JPA
    └─ Criar repositórios
    └─ Compilar sem erros

3️⃣  FASE 2.3-2.5: Services (1 hora)
    └─ DatabaseUserDetailsService
    └─ UsuarioRegistrationService
    └─ Novo endpoint /register

4️⃣  FASE 3: Testes Backend (30 min)
    └─ Testes unitários
    └─ Testes de integração

5️⃣  FASE 4: Configuração (30 min)
    └─ Atualizar SecurityConfig
    └─ Limpar configurações antigas

6️⃣  FASE 5: Frontend (1 hora)
    └─ Atualizar contexto auth
    └─ Atualizar formulários

7️⃣  FASE 6: Testes Finais (30 min)
    └─ Testes manuais
    └─ Validação completa
```

**TEMPO TOTAL: ~5-6 horas**

---

## ⚠️ PONTOS DE ATENÇÃO

1. **Senhas hasheadas em BD:**
   - Usar BCrypt sempre
   - Nunca armazenar em plain text
   - Validar durante teste

2. **Migração de dados dev users:**
   - Testar senha hasheada manualmente
   - Garantir que login ainda funciona
   - Documentar novo processo

3. **Compatibilidade com JWT:**
   - Verificar se roles ainda são extraídas corretamente
   - Testar token com novo UserDetailsService
   - Validar permissões

4. **Performance:**
   - Índices em username e email
   - Fetch EAGER em roles (pequeno set)
   - Considerar cache se muitos logins

5. **Segurança:**
   - Validar senha forte (8+ chars)
   - Rate limiting em /register
   - Log de tentativas de login falhas

---

## 📚 REFERÊNCIAS DE CÓDIGO

- Spring Security UserDetailsService: https://docs.spring.io/spring-security/reference/servlet/authentication/userdetails/index.html
- BCrypt Password Encoder: https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/password-encoders.html
- JPA Many-to-Many: https://hibernate.org/orm/documentation/
- Flyway Migrations: https://flywaydb.org/

---

## ✅ CRITÉRIO DE SUCESSO

Implementação será considerada **SUCESSO** quando:

1. ✅ Todos os 3 usuários de dev fazem login com sucesso
2. ✅ Novo usuário pode se registrar via `/api/v1/auth/register`
3. ✅ Novo usuário recebe token JWT válido
4. ✅ Novo usuário pode fazer login após registro
5. ✅ Roles e permissões funcionam corretamente
6. ✅ Frontend consegue registrar e fazer login
7. ✅ Todos os testes passam
8. ✅ Sem regressions em funcionalidades existentes
9. ✅ Código segue padrões da base (estrutura, naming, conventions)
10. ✅ Documentação atualizada

