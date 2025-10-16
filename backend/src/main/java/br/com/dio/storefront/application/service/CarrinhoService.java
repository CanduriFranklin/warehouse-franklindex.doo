package br.com.dio.storefront.application.service;

import br.com.dio.storefront.application.port.in.*;
import br.com.dio.storefront.application.port.out.PublicarEventoPort;
import br.com.dio.storefront.application.port.out.ValidarEstoquePort;
import br.com.dio.storefront.domain.event.*;
import br.com.dio.storefront.domain.exception.*;
import br.com.dio.storefront.domain.model.*;
import br.com.dio.storefront.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Service de Carrinho de Compras.
 * Implementa os Use Cases relacionados ao carrinho.
 * 
 * Hexagonal Architecture:
 * - Application Layer: Orquestra domain entities e repositories
 * - Implements Ports In: Use Cases
 * - Depends on Ports Out: PublicarEventoPort, ValidarEstoquePort
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Service
@Transactional
public class CarrinhoService implements 
        AdicionarProdutoAoCarrinhoUseCase,
        RemoverProdutoDoCarrinhoUseCase,
        AtualizarQuantidadeCarrinhoUseCase,
        ObterCarrinhoUseCase,
        FinalizarCarrinhoUseCase {
    
    private final CarrinhoComprasRepository carrinhoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;
    private final PublicarEventoPort publicarEventoPort;
    private final ValidarEstoquePort validarEstoquePort;
    
    public CarrinhoService(
            CarrinhoComprasRepository carrinhoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            PedidoRepository pedidoRepository,
            PublicarEventoPort publicarEventoPort,
            ValidarEstoquePort validarEstoquePort) {
        this.carrinhoRepository = carrinhoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
        this.publicarEventoPort = publicarEventoPort;
        this.validarEstoquePort = validarEstoquePort;
    }
    
    @Override
    public UUID adicionar(AdicionarProdutoCommand command) {
        Objects.requireNonNull(command, "Command não pode ser null");
        
        // Busca ou cria carrinho ativo para o cliente
        Cliente cliente = clienteRepository.findById(command.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException(command.clienteId()));
        
        CarrinhoCompras carrinho = carrinhoRepository.findByClienteIdAndStatus(
                        command.clienteId(), 
                        CarrinhoCompras.StatusCarrinho.ATIVO)
                .orElseGet(() -> {
                    CarrinhoCompras novoCarrinho = CarrinhoCompras.criar(cliente);
                    return carrinhoRepository.save(novoCarrinho);
                });
        
        // Busca produto
        Produto produto = produtoRepository.findById(command.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(command.produtoId()));
        
        // Valida estoque
        if (!validarEstoquePort.temEstoqueSuficiente(produto, command.quantidade())) {
            throw new EstoqueInsuficienteException(
                produto.getNome(), 
                command.quantidade(), 
                0  // estoque disponível (será ajustado quando integrar com warehouse)
            );
        }
        
        // Adiciona produto ao carrinho
        carrinho.adicionarProduto(produto, command.quantidade());
        carrinhoRepository.save(carrinho);
        
        // Publica evento
        publicarEventoPort.publicar(new ProdutoAdicionadoAoCarrinhoEvent(
                carrinho.getId(),
                cliente.getId(),
                produto.getId(),
                produto.getNome(),
                command.quantidade()
        ));
        
        return carrinho.getId();
    }
    
    @Override
    public void remover(RemoverProdutoCommand command) {
        Objects.requireNonNull(command, "Command não pode ser null");
        
        CarrinhoCompras carrinho = buscarCarrinhoAtivo(command.clienteId());
        
        // Verifica se produto existe
        produtoRepository.findById(command.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(command.produtoId()));
        
        carrinho.removerProduto(command.produtoId());
        carrinhoRepository.save(carrinho);
    }
    
    @Override
    public void atualizar(AtualizarQuantidadeCommand command) {
        Objects.requireNonNull(command, "Command não pode ser null");
        
        CarrinhoCompras carrinho = buscarCarrinhoAtivo(command.clienteId());
        
        Produto produto = produtoRepository.findById(command.produtoId())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(command.produtoId()));
        
        // Valida novo estoque
        if (!validarEstoquePort.temEstoqueSuficiente(produto, command.novaQuantidade())) {
            throw new EstoqueInsuficienteException(
                produto.getNome(), 
                command.novaQuantidade(), 
                0  // estoque disponível (será ajustado quando integrar com warehouse)
            );
        }
        
        carrinho.atualizarQuantidade(command.produtoId(), command.novaQuantidade());
        carrinhoRepository.save(carrinho);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CarrinhoCompras obter(UUID clienteId) {
        Objects.requireNonNull(clienteId, "ID do cliente não pode ser null");
        
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
        
        return carrinhoRepository.findByClienteIdAndStatus(
                        clienteId, 
                        CarrinhoCompras.StatusCarrinho.ATIVO)
                .orElseGet(() -> {
                    CarrinhoCompras novoCarrinho = CarrinhoCompras.criar(cliente);
                    return carrinhoRepository.save(novoCarrinho);
                });
    }
    
    @Override
    public UUID finalizar(FinalizarCarrinhoCommand command) {
        Objects.requireNonNull(command, "Command não pode ser null");
        
        // Busca carrinho
        CarrinhoCompras carrinho = buscarCarrinhoAtivo(command.clienteId());
        
        if (carrinho.getItens().isEmpty()) {
            throw new CarrinhoInvalidoException("Carrinho está vazio");
        }
        
        // Valida estoque de todos os produtos
        for (ItemCarrinho item : carrinho.getItens()) {
            if (!validarEstoquePort.temEstoqueSuficiente(item.getProduto(), item.getQuantidade())) {
                throw new EstoqueInsuficienteException(
                        item.getProduto().getNome(), 
                        item.getQuantidade(),
                        0  // estoque disponível
                );
            }
        }
        
        // Cria pedido a partir do carrinho
        Cliente cliente = carrinho.getCliente();
        Pedido pedido = Pedido.doDe(
                carrinho,
                command.enderecoEntrega(),
                command.informacaoPagamento(),
                "" // observações vazias
        );
        
        // Finaliza o carrinho
        carrinho.finalizar();
        
        // Reserva estoque
        for (ItemCarrinho item : carrinho.getItens()) {
            validarEstoquePort.reservarEstoque(
                    item.getProduto().getId(), 
                    item.getQuantidade()
            );
        }
        
        // Salva pedido e carrinho
        pedidoRepository.save(pedido);
        carrinhoRepository.save(carrinho);
        
        // Publica eventos
        publicarEventoPort.publicar(new CarrinhoFinalizadoEvent(
                carrinho.getId(),
                cliente.getId(),
                carrinho.calcularQuantidadeTotal()
        ));
        
        publicarEventoPort.publicar(new PedidoCriadoEvent(
                pedido.getId(),
                pedido.getNumeroPedido(),
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail()
        ));
        
        return pedido.getId();
    }
    
    /**
     * Helper para buscar carrinho ativo do cliente.
     */
    private CarrinhoCompras buscarCarrinhoAtivo(UUID clienteId) {
        return carrinhoRepository.findByClienteIdAndStatus(
                        clienteId, 
                        CarrinhoCompras.StatusCarrinho.ATIVO)
                .orElseThrow(() -> new CarrinhoNaoEncontradoException(clienteId));
    }
}
