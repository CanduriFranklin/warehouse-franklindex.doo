package br.com.dio.storefront.domain.model;

import br.com.dio.storefront.domain.valueobject.Dinheiro;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um Produto no catálogo da loja.
 * Aggregate Root do contexto de Catálogo.
 */
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "preco", nullable = false))
    private Dinheiro preco;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Column(length = 100)
    private String categoria;

    @Column(length = 500)
    private String imagemUrl;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    /**
     * Construtor protegido para JPA.
     */
    protected Produto() {
    }

    /**
     * Construtor privado para criação de produto.
     */
    private Produto(String nome, String descricao, Dinheiro preco, 
                    Integer quantidadeEstoque, String categoria, String imagemUrl) {
        validar(nome, preco, quantidadeEstoque);
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
        this.imagemUrl = imagemUrl;
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Factory method para criar um novo produto.
     */
    public static Produto criar(String nome, String descricao, Dinheiro preco,
                                Integer quantidadeEstoque, String categoria, String imagemUrl) {
        return new Produto(nome, descricao, preco, quantidadeEstoque, categoria, imagemUrl);
    }

    /**
     * Valida os dados obrigatórios do produto.
     */
    private void validar(String nome, Dinheiro preco, Integer quantidadeEstoque) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (preco == null || preco.ehZero()) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        if (quantidadeEstoque == null || quantidadeEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa");
        }
    }

    /**
     * Atualiza as informações do produto.
     */
    public void atualizar(String nome, String descricao, Dinheiro preco, String categoria, String imagemUrl) {
        validar(nome, preco, this.quantidadeEstoque);
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.imagemUrl = imagemUrl;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Adiciona quantidade ao estoque.
     */
    public void adicionarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        this.quantidadeEstoque += quantidade;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Remove quantidade do estoque.
     */
    public void removerEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        if (this.quantidadeEstoque < quantidade) {
            throw new IllegalStateException("Estoque insuficiente");
        }
        this.quantidadeEstoque -= quantidade;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Verifica se o produto está disponível para venda.
     */
    public boolean estaDisponivel() {
        return ativo && quantidadeEstoque > 0;
    }

    /**
     * Verifica se há estoque suficiente para a quantidade solicitada.
     */
    public boolean temEstoqueSuficiente(int quantidade) {
        return quantidadeEstoque >= quantidade;
    }

    /**
     * Ativa o produto.
     */
    public void ativar() {
        this.ativo = true;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Desativa o produto (não pode mais ser vendido).
     */
    public void desativar() {
        this.ativo = false;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Calcula o valor total para uma quantidade específica.
     */
    public Dinheiro calcularValorTotal(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        return preco.multiplicar(quantidade);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Dinheiro getPreco() {
        return preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public Boolean getAtivo() {
        return ativo;
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
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", ativo=" + ativo +
                '}';
    }
}
