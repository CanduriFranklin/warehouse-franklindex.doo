package br.com.dio.warehouse.infrastructure.persistence;

import br.com.dio.warehouse.domain.model.Role;
import br.com.dio.warehouse.domain.model.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para a entidade Role.
 * 
 * Fornece operações CRUD e consultas customizadas para roles no banco de dados.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Busca uma role pelo seu nome (enum).
     * 
     * @param nome O enum RoleEnum a buscar
     * @return Optional contendo a role se encontrada, vazio caso contrário
     */
    Optional<Role> findByNome(RoleEnum nome);
    
    /**
     * Verifica se uma role com determinado nome existe no banco de dados.
     * 
     * @param nome O enum RoleEnum a verificar
     * @return true se a role existe, false caso contrário
     */
    boolean existsByNome(RoleEnum nome);
}
