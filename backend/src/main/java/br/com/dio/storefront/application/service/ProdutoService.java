package br.com.dio.storefront.application.service;

import br.com.dio.storefront.application.port.in.BuscarProdutoUseCase;
import br.com.dio.storefront.application.port.in.ListarProdutosUseCase;
import br.com.dio.storefront.domain.exception.ProdutoNaoEncontradoException;
import br.com.dio.storefront.domain.model.Produto;
import br.com.dio.storefront.domain.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Service de Produtos.
 * Implementa os Use Cases relacionados ao catálogo de produtos.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Service
@Transactional(readOnly = true)
public class ProdutoService implements ListarProdutosUseCase, BuscarProdutoUseCase {
    
    private final ProdutoRepository produtoRepository;
    
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }
    
    @Override
    public Page<Produto> listar(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable não pode ser null");
        return produtoRepository.findByAtivoTrue(pageable);
    }
    
    @Override
    public Page<Produto> listarPorCategoria(String categoria, Pageable pageable) {
        Objects.requireNonNull(categoria, "Categoria não pode ser null");
        Objects.requireNonNull(pageable, "Pageable não pode ser null");
        
        return produtoRepository.findByCategoriaAndAtivoTrue(categoria, pageable);
    }
    
    @Override
    public Page<Produto> buscarPorNome(String termo, Pageable pageable) {
        Objects.requireNonNull(termo, "Termo não pode ser null");
        Objects.requireNonNull(pageable, "Pageable não pode ser null");
        
        return produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(termo, pageable);
    }
    
    @Override
    public Produto buscar(UUID produtoId) {
        Objects.requireNonNull(produtoId, "ID do produto não pode ser null");
        
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));
    }
}
