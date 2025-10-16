package br.com.dio.storefront.domain.model;

import br.com.dio.storefront.domain.valueobject.Dinheiro;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um item no carrinho de compras.
 * Entity dentro do Aggregate CarrinhoCompras.
 */
@Entity
@Table(name = "itens_carrinho")
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "preco_unitario", nullable = false))
    private Dinheiro precoUnitario;

    /**
     * Construtor protegido para JPA.
     */
    protected ItemCarrinho() {
    }

    /**
     * Construtor privado para criação de item.
     */
    private ItemCarrinho(Produto produto, Integer quantidade) {
        validar(produto, quantidade);
        this.id = UUID.randomUUID();
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
    }

    /**
     * Factory method para criar um novo item no carrinho.
     */
    public static ItemCarrinho criar(Produto produto, Integer quantidade) {
        return new ItemCarrinho(produto, quantidade);
    }

    /**
     * Valida os dados do item.
     */
    private void validar(Produto produto, Integer quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        if (!produto.estaDisponivel()) {
            throw new IllegalStateException("Produto não está disponível para venda");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (!produto.temEstoqueSuficiente(quantidade)) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
        }
    }

    /**
     * Atualiza a quantidade do item.
     */
    public void atualizarQuantidade(Integer novaQuantidade) {
        if (novaQuantidade == null || novaQuantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (!produto.temEstoqueSuficiente(novaQuantidade)) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
        }
        this.quantidade = novaQuantidade;
    }

    /**
     * Incrementa a quantidade do item.
     */
    public void incrementarQuantidade(Integer adicional) {
        if (adicional == null || adicional <= 0) {
            throw new IllegalArgumentException("Valor adicional deve ser maior que zero");
        }
        int novaQuantidade = this.quantidade + adicional;
        if (!produto.temEstoqueSuficiente(novaQuantidade)) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + produto.getNome());
        }
        this.quantidade = novaQuantidade;
    }

    /**
     * Decrementa a quantidade do item.
     */
    public void decrementarQuantidade(Integer reducao) {
        if (reducao == null || reducao <= 0) {
            throw new IllegalArgumentException("Valor de redução deve ser maior que zero");
        }
        int novaQuantidade = this.quantidade - reducao;
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        this.quantidade = novaQuantidade;
    }

    /**
     * Calcula o subtotal do item (preço unitário * quantidade).
     */
    public Dinheiro calcularSubtotal() {
        return precoUnitario.multiplicar(quantidade);
    }

    /**
     * Verifica se o item é do produto especificado.
     */
    public boolean ehDoProduto(UUID produtoId) {
        return produto.getId().equals(produtoId);
    }

    /**
     * Verifica se o preço do produto mudou desde que foi adicionado ao carrinho.
     */
    public boolean precoMudou() {
        return !precoUnitario.equals(produto.getPreco());
    }

    /**
     * Atualiza o preço unitário com o preço atual do produto.
     */
    public void atualizarPreco() {
        this.precoUnitario = produto.getPreco();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Dinheiro getPrecoUnitario() {
        return precoUnitario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCarrinho that = (ItemCarrinho) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ItemCarrinho{" +
                "produto=" + produto.getNome() +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                ", subtotal=" + calcularSubtotal() +
                '}';
    }
}
