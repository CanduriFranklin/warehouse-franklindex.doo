package br.com.dio.storefront.domain.model;

import br.com.dio.storefront.domain.valueobject.Dinheiro;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um item em um pedido.
 * Entity dentro do Aggregate Pedido.
 */
@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false, length = 200)
    private String nomeProduto; // Snapshot do nome no momento do pedido

    @Column(nullable = false)
    private Integer quantidade;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "preco_unitario", nullable = false))
    private Dinheiro precoUnitario; // Snapshot do preço no momento do pedido

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "subtotal", nullable = false))
    private Dinheiro subtotal;

    /**
     * Construtor protegido para JPA.
     */
    protected ItemPedido() {
    }

    /**
     * Construtor privado para criação de item de pedido.
     */
    private ItemPedido(Produto produto, Integer quantidade, Dinheiro precoUnitario) {
        validar(produto, quantidade, precoUnitario);
        this.id = UUID.randomUUID();
        this.produto = produto;
        this.nomeProduto = produto.getNome(); // Snapshot
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = precoUnitario.multiplicar(quantidade);
    }

    /**
     * Factory method para criar um item de pedido a partir de um item de carrinho.
     */
    public static ItemPedido doDe(ItemCarrinho itemCarrinho) {
        if (itemCarrinho == null) {
            throw new IllegalArgumentException("Item do carrinho não pode ser nulo");
        }
        return new ItemPedido(
            itemCarrinho.getProduto(),
            itemCarrinho.getQuantidade(),
            itemCarrinho.getPrecoUnitario()
        );
    }

    /**
     * Factory method para criar um item de pedido diretamente.
     */
    public static ItemPedido criar(Produto produto, Integer quantidade, Dinheiro precoUnitario) {
        return new ItemPedido(produto, quantidade, precoUnitario);
    }

    /**
     * Valida os dados do item de pedido.
     */
    private void validar(Produto produto, Integer quantidade, Dinheiro precoUnitario) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (precoUnitario == null || precoUnitario.ehZero()) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que zero");
        }
    }

    /**
     * Recalcula o subtotal (usado se houver ajustes).
     */
    public void recalcularSubtotal() {
        this.subtotal = precoUnitario.multiplicar(quantidade);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Dinheiro getPrecoUnitario() {
        return precoUnitario;
    }

    public Dinheiro getSubtotal() {
        return subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPedido that = (ItemPedido) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ItemPedido{" +
                "nomeProduto='" + nomeProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
