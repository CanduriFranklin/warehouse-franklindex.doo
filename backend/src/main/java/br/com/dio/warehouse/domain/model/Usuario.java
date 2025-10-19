package br.com.dio.warehouse.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidade que representa um usuário do sistema.
 * 
 * Um usuário é uma conta de acesso com credenciais (username/email/senha)
 * e um conjunto de roles que definem suas permissões.
 * 
 * Relacionamento: Um usuário pode ter múltiplas roles (M:N).
 */
@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuarios_username", columnList = "username"),
    @Index(name = "idx_usuarios_email", columnList = "email"),
    @Index(name = "idx_usuarios_ativo", columnList = "ativo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"roles"})
@ToString(exclude = {"roles", "senhaHash"})
public class Usuario {
    
    /**
     * Identificador único do usuário (UUID).
     * Chave primária gerada automaticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    /**
     * Nome de usuário único para login.
     * Campo único, obrigatório, entre 3 e 50 caracteres.
     */
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
    
    /**
     * Email único do usuário.
     * Campo único, obrigatório, máximo 100 caracteres.
     */
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    
    /**
     * Hash BCrypt da senha.
     * Nunca armazena a senha em texto plano. Campo obrigatório e não serializado.
     */
    @Column(name = "senha_hash", length = 255, nullable = false)
    private String senhaHash;
    
    /**
     * Flag indicando se o usuário está ativo (TRUE) ou desativado (FALSE).
     * Padrão: TRUE na criação.
     */
    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;
    
    /**
     * Timestamp do último acesso/login do usuário.
     * Campo opcional, preenchido ao fazer login.
     */
    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;
    
    /**
     * Data e hora da criação do registro.
     * Definido automaticamente ao criar a entidade.
     */
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    /**
     * Data e hora da última atualização do registro.
     * Atualizado automaticamente pelo banco de dados.
     */
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
    
    /**
     * Relacionamento ManyToMany com Role.
     * Um usuário pode ter múltiplas roles, e uma role pode ser atribuída a múltiplos usuários.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    /**
     * Callback executado antes de persistir a entidade no banco de dados.
     * Define as datas de criação e atualização.
     */
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        if (ativo == null) {
            ativo = true;
        }
    }
    
    /**
     * Callback executado antes de atualizar a entidade no banco de dados.
     * Atualiza a data de modificação.
     */
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
    
    /**
     * Adiciona uma role ao usuário.
     * 
     * @param role A role a ser adicionada
     */
    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }
    
    /**
     * Remove uma role do usuário.
     * 
     * @param role A role a ser removida
     */
    public void removeRole(Role role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }
    
    /**
     * Verifica se o usuário possui uma determinada role.
     * 
     * @param roleEnum O enum da role
     * @return true se o usuário possui a role, false caso contrário
     */
    public boolean hasRole(RoleEnum roleEnum) {
        if (this.roles == null || this.roles.isEmpty()) {
            return false;
        }
        return this.roles.stream()
            .anyMatch(role -> role.getNome() == roleEnum);
    }
    
    /**
     * Atualiza o timestamp do último acesso.
     * Chamado quando o usuário faz login com sucesso.
     */
    public void updateUltimoAcesso() {
        this.ultimoAcesso = LocalDateTime.now();
    }
}
