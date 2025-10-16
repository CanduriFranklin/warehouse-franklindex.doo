package br.com.dio.storefront.infrastructure.web.mapper;

import br.com.dio.storefront.domain.model.*;
import br.com.dio.storefront.domain.valueobject.*;
import br.com.dio.storefront.infrastructure.web.dto.*;
import br.com.dio.storefront.infrastructure.web.dto.request.CadastrarClienteRequest;
import br.com.dio.storefront.infrastructure.web.dto.response.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para conversão entre entidades de domínio e DTOs.
 * Alternativa simplificada ao MapStruct.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Component
public class StorefrontMapper {
    
    // ========== Dinheiro ==========
    
    public DinheiroDTO toDTO(Dinheiro dinheiro) {
        if (dinheiro == null) return null;
        return new DinheiroDTO(dinheiro.getValor(), "BRL");
    }
    
    public Dinheiro toDomain(DinheiroDTO dto) {
        if (dto == null) return null;
        return Dinheiro.de(dto.valor());
    }
    
    // ========== Endereco ==========
    
    public EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) return null;
        return new EnderecoDTO(
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep()
        );
    }
    
    public Endereco toDomain(EnderecoDTO dto) {
        if (dto == null) return null;
        return Endereco.de(
                dto.logradouro(),
                dto.numero(),
                dto.complemento(),
                dto.bairro(),
                dto.cidade(),
                dto.estado(),
                dto.cep()
        );
    }
    
    // ========== InformacaoPagamento ==========
    
    public InformacaoPagamento toDomain(InformacaoPagamentoDTO dto) {
        if (dto == null) return null;
        return InformacaoPagamento.cartao(
                dto.numeroCartao(),
                dto.nomeTitular(),
                String.format("%02d/%02d", dto.mesValidade(), dto.anoValidade() % 100),
                dto.cvv()
        );
    }
    
    // ========== Produto ==========
    
    public ProdutoResponse toResponse(Produto produto) {
        if (produto == null) return null;
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                toDTO(produto.getPreco()),
                produto.getCategoria(),
                produto.getQuantidadeEstoque(),
                produto.getAtivo(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }
    
    // ========== Cliente ==========
    
    public ClienteResponse toResponse(Cliente cliente) {
        if (cliente == null) return null;
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf(),
                cliente.getTelefone(),
                toDTO(cliente.getEndereco()),
                cliente.getCriadoEm(),
                cliente.getAtualizadoEm()
        );
    }
    
    public Cliente toDomain(CadastrarClienteRequest request) {
        if (request == null) return null;
        return Cliente.criar(
                request.nomeCompleto(),
                request.email(),
                request.cpf(),
                request.telefone(),
                toDomain(request.endereco())
        );
    }
    
    // ========== CarrinhoCompras ==========
    
    public CarrinhoResponse toResponse(CarrinhoCompras carrinho) {
        if (carrinho == null) return null;
        return new CarrinhoResponse(
                carrinho.getId(),
                carrinho.getCliente().getId(),
                carrinho.getItens().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                toDTO(carrinho.calcularTotal()),
                carrinho.getStatus().name(),
                carrinho.getCriadoEm(),
                carrinho.getAtualizadoEm()
        );
    }
    
    // ========== ItemCarrinho ==========
    
    public ItemCarrinhoResponse toResponse(ItemCarrinho item) {
        if (item == null) return null;
        return new ItemCarrinhoResponse(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                toDTO(item.getPrecoUnitario()),
                item.getQuantidade(),
                toDTO(item.calcularSubtotal())
        );
    }
    
    // ========== Pedido ==========
    
    public PedidoResponse toResponse(Pedido pedido) {
        if (pedido == null) return null;
        return new PedidoResponse(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getCliente().getId(),
                pedido.getItens().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()),
                toDTO(pedido.getValorTotal()),
                toDTO(pedido.getEnderecoEntrega()),
                pedido.getStatus().name(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm()
        );
    }
    
    // ========== ItemPedido ==========
    
    public ItemPedidoResponse toResponse(ItemPedido item) {
        if (item == null) return null;
        return new ItemPedidoResponse(
                item.getProduto().getId(),
                item.getNomeProduto(),
                toDTO(item.getPrecoUnitario()),
                item.getQuantidade(),
                toDTO(item.getSubtotal())
        );
    }
}
