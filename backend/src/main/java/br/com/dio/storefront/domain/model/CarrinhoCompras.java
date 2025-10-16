package br.com.dio.storefront.domain.model;

import br.com.dio.storefront.domain.valueobject.Dinheiro;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um Carrinho de Compras.
 * Aggregate Root do contexto de Carrinho.
 */
@Entity
@Table(name = "carrinhos_compras")
public class CarrinhoCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrinho_id")
    private List<ItemCarrinho> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCarrinho status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    /**
     * Construtor protegido para JPA.
     */
    protected CarrinhoCompras() {
    }

    /**
     * Construtor privado para criação de carrinho.
     */
    private CarrinhoCompras(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        this.id = UUID.randomUUID();
        this.cliente = cliente;
        this.itens = new ArrayList<>();
        this.status = StatusCarrinho.ATIVO;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Factory method para criar um novo carrinho de compras.
     */
    public static CarrinhoCompras criar(Cliente cliente) {
        return new CarrinhoCompras(cliente);
    }

    /**
     * Adiciona um produto ao carrinho.
     */
    public void adicionarProduto(Produto produto, Integer quantidade) {
        validarCarrinhoAtivo();
        
        // Verifica se o produto já está no carrinho
        ItemCarrinho itemExistente = buscarItemPorProduto(produto.getId());
        
        if (itemExistente != null) {
            // Se já existe, incrementa a quantidade
            itemExistente.incrementarQuantidade(quantidade);
        } else {
            // Se não existe, cria novo item
            ItemCarrinho novoItem = ItemCarrinho.criar(produto, quantidade);
            itens.add(novoItem);
        }
        
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Remove um produto do carrinho.
     */
    public void removerProduto(UUID produtoId) {
        validarCarrinhoAtivo();
        
        ItemCarrinho item = buscarItemPorProduto(produtoId);
        if (item == null) {
            throw new IllegalArgumentException("Produto não encontrado no carrinho");
        }
        
        itens.remove(item);
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Atualiza a quantidade de um produto no carrinho.
     */
    public void atualizarQuantidade(UUID produtoId, Integer novaQuantidade) {
        validarCarrinhoAtivo();
        
        ItemCarrinho item = buscarItemPorProduto(produtoId);
        if (item == null) {
            throw new IllegalArgumentException("Produto não encontrado no carrinho");
        }
        
        if (novaQuantidade <= 0) {
            // Se nova quantidade é zero ou negativa, remove o item
            itens.remove(item);
        } else {
            item.atualizarQuantidade(novaQuantidade);
        }
        
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Limpa todos os itens do carrinho.
     */
    public void limpar() {
        validarCarrinhoAtivo();
        itens.clear();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Finaliza o carrinho (converte em pedido).
     */
    public void finalizar() {
        validarCarrinhoAtivo();
        
        if (itens.isEmpty()) {
            throw new IllegalStateException("Carrinho vazio não pode ser finalizado");
        }
        
        this.status = StatusCarrinho.FINALIZADO;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Cancela o carrinho.
     */
    public void cancelar() {
        validarCarrinhoAtivo();
        this.status = StatusCarrinho.CANCELADO;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Atualiza os preços de todos os itens com os preços atuais dos produtos.
     */
    public void atualizarPrecos() {
        validarCarrinhoAtivo();
        
        for (ItemCarrinho item : itens) {
            if (item.precoMudou()) {
                item.atualizarPreco();
            }
        }
        
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Calcula o valor total do carrinho.
     */
    public Dinheiro calcularTotal() {
        return itens.stream()
                .map(ItemCarrinho::calcularSubtotal)
                .reduce(Dinheiro.zero(), Dinheiro::adicionar);
    }

    /**
     * Calcula a quantidade total de itens no carrinho.
     */
    public int calcularQuantidadeTotal() {
        return itens.stream()
                .mapToInt(ItemCarrinho::getQuantidade)
                .sum();
    }

    /**
     * Verifica se o carrinho está vazio.
     */
    public boolean estaVazio() {
        return itens.isEmpty();
    }

    /**
     * Verifica se o carrinho está ativo.
     */
    public boolean estaAtivo() {
        return status == StatusCarrinho.ATIVO;
    }

    /**
     * Busca um item no carrinho por ID do produto.
     */
    private ItemCarrinho buscarItemPorProduto(UUID produtoId) {
        return itens.stream()
                .filter(item -> item.ehDoProduto(produtoId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Valida se o carrinho está ativo para operações.
     */
    private void validarCarrinhoAtivo() {
        if (!estaAtivo()) {
            throw new IllegalStateException("Operação não permitida. Carrinho não está ativo.");
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemCarrinho> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public StatusCarrinho getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrinhoCompras that = (CarrinhoCompras) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CarrinhoCompras{" +
                "id=" + id +
                ", cliente=" + cliente.getNome() +
                ", quantidadeItens=" + itens.size() +
                ", total=" + calcularTotal() +
                ", status=" + status +
                '}';
    }

    /**
     * Enum para representar o status do carrinho.
     */
    public enum StatusCarrinho {
        ATIVO("Ativo"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");

        private final String descricao;

        StatusCarrinho(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
