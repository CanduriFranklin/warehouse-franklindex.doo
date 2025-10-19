package br.com.dio.warehouse.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um papél (role) no sistema.
 * 
 * Um papel define um conjunto de permissões e tipos de acesso para usuários.
 * Relacionamento: Uma role pode ser atribuída a múltiplos usuários (1:N).
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_roles_nome", columnList = "nome")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Role {
    
    /**
     * Identificador único da role (chave primária).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nome da role (ADMIN, WAREHOUSE_MANAGER, SALES, CUSTOMER).
     * Campo único e obrigatório.
     */
    @Column(name = "nome", length = 50, nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleEnum nome;
    
    /**
     * Descrição da role e suas permissões.
     * Campo opcional.
     */
    @Column(name = "descricao", length = 255)
    private String descricao;
    
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
     * Callback executado antes de persistir a entidade no banco de dados.
     * Define as datas de criação e atualização.
     */
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }
    
    /**
     * Callback executado antes de atualizar a entidade no banco de dados.
     * Atualiza a data de modificação.
     */
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
