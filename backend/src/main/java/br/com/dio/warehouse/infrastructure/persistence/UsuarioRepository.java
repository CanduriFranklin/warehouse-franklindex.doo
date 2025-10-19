package br.com.dio.warehouse.infrastructure.persistence;

import br.com.dio.warehouse.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para a entidade Usuario.
 * 
 * Fornece operações CRUD e consultas customizadas para usuários no banco de dados.
 * Utilizado pela autenticação e gerenciamento de usuários.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    /**
     * Busca um usuário pelo seu username.
     * 
     * Utilizado durante o processo de autenticação (login).
     * 
     * @param username O nome de usuário
     * @return Optional contendo o usuário se encontrado, vazio caso contrário
     */
    Optional<Usuario> findByUsername(String username);
    
    /**
     * Busca um usuário pelo seu email.
     * 
     * Utilizado durante validação de email único e recuperação de conta.
     * 
     * @param email O email do usuário
     * @return Optional contendo o usuário se encontrado, vazio caso contrário
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica se um username já existe no banco de dados.
     * 
     * Utilizado em validação de registro de novo usuário.
     * 
     * @param username O nome de usuário a verificar
     * @return true se o username existe, false caso contrário
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica se um email já existe no banco de dados.
     * 
     * Utilizado em validação de registro de novo usuário.
     * 
     * @param email O email a verificar
     * @return true se o email existe, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca todos os usuários ativos no sistema.
     * 
     * @return Lista de usuários com ativo = true
     */
    List<Usuario> findAllByAtivoTrue();
    
    /**
     * Busca todos os usuários inativos no sistema.
     * 
     * @return Lista de usuários com ativo = false
     */
    List<Usuario> findAllByAtivoFalse();
    
    /**
     * Query customizada para buscar usuários com uma determinada role.
     * 
     * @param roleNome O nome da role (ex: "ADMIN")
     * @return Lista de usuários que possuem a role especificada
     */
    @Query(value = """
        SELECT DISTINCT u FROM Usuario u
        INNER JOIN u.roles r
        WHERE r.nome = :roleNome
        """)
    List<Usuario> findUsuariosByRole(@Param("roleNome") String roleNome);
    
    /**
     * Query customizada para buscar usuários por username com busca case-insensitive.
     * 
     * @param username O nome de usuário (parcial ou completo)
     * @return Lista de usuários que correspondem ao critério de busca
     */
    @Query(value = """
        SELECT u FROM Usuario u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))
        AND u.ativo = true
        """)
    List<Usuario> searchUsuariosByUsername(@Param("username") String username);
}
