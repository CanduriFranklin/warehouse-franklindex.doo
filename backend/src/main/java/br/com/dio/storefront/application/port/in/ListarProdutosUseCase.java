package br.com.dio.storefront.application.port.in;

import br.com.dio.storefront.domain.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Use Case para listar produtos do catálogo.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface ListarProdutosUseCase {
    
    /**
     * Lista todos os produtos ativos do catálogo com paginação.
     * 
     * @param pageable Configuração de paginação
     * @return Página de produtos
     */
    Page<Produto> listar(Pageable pageable);
    
    /**
     * Lista produtos por categoria com paginação.
     * 
     * @param categoria Categoria para filtrar
     * @param pageable Configuração de paginação
     * @return Página de produtos da categoria
     */
    Page<Produto> listarPorCategoria(String categoria, Pageable pageable);
    
    /**
     * Busca produtos por nome com paginação.
     * 
     * @param nome Nome ou parte do nome do produto
     * @param pageable Configuração de paginação
     * @return Página de produtos encontrados
     */
    Page<Produto> buscarPorNome(String nome, Pageable pageable);
}
