package br.com.dio.storefront.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.dio.storefront.domain.valueobject.Dinheiro;
import br.com.dio.storefront.domain.valueobject.Endereco;
import br.com.dio.storefront.domain.valueobject.InformacaoPagamento;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entidade que representa um Pedido.
 * Aggregate Root do contexto de Pedido.
 * Este é o coração do fluxo de vendas - conecta Storefront com Warehouse.
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroPedido; // Formato: PED-YYYYMMDD-XXXXX

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<ItemPedido> itens = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "rua", column = @Column(name = "endereco_entrega_logradouro")),
        @AttributeOverride(name = "numero", column = @Column(name = "endereco_entrega_numero")),
        @AttributeOverride(name = "complemento", column = @Column(name = "endereco_entrega_complemento")),
        @AttributeOverride(name = "bairro", column = @Column(name = "endereco_entrega_bairro")),
        @AttributeOverride(name = "cidade", column = @Column(name = "endereco_entrega_cidade")),
        @AttributeOverride(name = "estado", column = @Column(name = "endereco_entrega_estado")),
        @AttributeOverride(name = "cep", column = @Column(name = "endereco_entrega_cep"))
    })
    private Endereco enderecoEntrega;

    @Transient
    private InformacaoPagamento informacaoPagamento;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "valor_total", nullable = false))
    private Dinheiro valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @Column
    private LocalDateTime pagamentoConfirmadoEm;

    @Column
    private LocalDateTime enviadoEm;

    @Column
    private LocalDateTime entregueEm;

    @Column
    private LocalDateTime canceladoEm;

    /**
     * Construtor protegido para JPA.
     */
    protected Pedido() {
    }

    /**
     * Construtor privado para criação de pedido.
     */
    private Pedido(Cliente cliente, List<ItemPedido> itens, Endereco enderecoEntrega, 
                   InformacaoPagamento informacaoPagamento, String observacoes) {
        validar(cliente, itens, enderecoEntrega, informacaoPagamento);
        this.id = UUID.randomUUID();
        this.numeroPedido = gerarNumeroPedido();
        this.cliente = cliente;
        this.itens = new ArrayList<>(itens);
        this.enderecoEntrega = enderecoEntrega;
        this.informacaoPagamento = informacaoPagamento;
        this.observacoes = observacoes;
        this.valorTotal = calcularValorTotal();
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Factory method para criar pedido a partir de um carrinho de compras.
     */
    public static Pedido doDe(CarrinhoCompras carrinho, Endereco enderecoEntrega, 
                              InformacaoPagamento informacaoPagamento, String observacoes) {
        if (carrinho == null) {
            throw new IllegalArgumentException("Carrinho não pode ser nulo");
        }
        if (carrinho.estaVazio()) {
            throw new IllegalStateException("Não é possível criar pedido de carrinho vazio");
        }
        
        // Converte itens do carrinho em itens de pedido
        List<ItemPedido> itensPedido = carrinho.getItens().stream()
                .map(ItemPedido::doDe)
                .toList();
        
        return new Pedido(
            carrinho.getCliente(),
            itensPedido,
            enderecoEntrega,
            informacaoPagamento,
            observacoes
        );
    }

    /**
     * Factory method para criar pedido diretamente.
     */
    public static Pedido criar(Cliente cliente, List<ItemPedido> itens, Endereco enderecoEntrega,
                               InformacaoPagamento informacaoPagamento, String observacoes) {
        return new Pedido(cliente, itens, enderecoEntrega, informacaoPagamento, observacoes);
    }

    /**
     * Valida os dados obrigatórios do pedido.
     */
    private void validar(Cliente cliente, List<ItemPedido> itens, 
                        Endereco enderecoEntrega, InformacaoPagamento informacaoPagamento) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter pelo menos um item");
        }
        if (enderecoEntrega == null) {
            throw new IllegalArgumentException("Endereço de entrega é obrigatório");
        }
        if (informacaoPagamento == null) {
            throw new IllegalArgumentException("Informação de pagamento é obrigatória");
        }
    }

    /**
     * Gera número único do pedido no formato PED-YYYYMMDD-XXXXX.
     */
    private String gerarNumeroPedido() {
        LocalDateTime agora = LocalDateTime.now();
        String dataParte = String.format("%04d%02d%02d", 
            agora.getYear(), agora.getMonthValue(), agora.getDayOfMonth());
        String randomParte = String.format("%05d", (int) (Math.random() * 100000));
        return "PED-" + dataParte + "-" + randomParte;
    }

    /**
     * Calcula o valor total do pedido somando os subtotais dos itens.
     */
    private Dinheiro calcularValorTotal() {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(Dinheiro.zero(), Dinheiro::adicionar);
    }

    /**
     * Confirma o pagamento do pedido.
     * Transição: AGUARDANDO_PAGAMENTO -> PAGAMENTO_CONFIRMADO
     */
    public void confirmarPagamento() {
        if (status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Apenas pedidos aguardando pagamento podem ter pagamento confirmado");
        }
        this.status = StatusPedido.PAGAMENTO_CONFIRMADO;
        this.pagamentoConfirmadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Marca o pedido como em preparação no armazém.
     * Transição: PAGAMENTO_CONFIRMADO -> EM_PREPARACAO
     */
    public void iniciarPreparacao() {
        if (status != StatusPedido.PAGAMENTO_CONFIRMADO) {
            throw new IllegalStateException("Apenas pedidos com pagamento confirmado podem entrar em preparação");
        }
        this.status = StatusPedido.EM_PREPARACAO;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Marca o pedido como enviado.
     * Transição: EM_PREPARACAO -> ENVIADO
     */
    public void enviar() {
        if (status != StatusPedido.EM_PREPARACAO) {
            throw new IllegalStateException("Apenas pedidos em preparação podem ser enviados");
        }
        this.status = StatusPedido.ENVIADO;
        this.enviadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Marca o pedido como entregue.
     * Transição: ENVIADO -> ENTREGUE
     */
    public void entregar() {
        if (status != StatusPedido.ENVIADO) {
            throw new IllegalStateException("Apenas pedidos enviados podem ser entregues");
        }
        this.status = StatusPedido.ENTREGUE;
        this.entregueEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Cancela o pedido.
     * Permitido apenas se não foi enviado ainda.
     */
    public void cancelar(String motivo) {
        if (status == StatusPedido.ENVIADO || status == StatusPedido.ENTREGUE) {
            throw new IllegalStateException("Pedidos enviados ou entregues não podem ser cancelados");
        }
        if (status == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Pedido já está cancelado");
        }
        
        this.status = StatusPedido.CANCELADO;
        this.canceladoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        
        if (motivo != null && !motivo.trim().isEmpty()) {
            this.observacoes = (this.observacoes != null ? this.observacoes + "\n" : "") 
                             + "Cancelado: " + motivo;
        }
    }

    /**
     * Verifica se o pedido pode ser cancelado.
     */
    public boolean podeCancelar() {
        return status != StatusPedido.ENVIADO 
            && status != StatusPedido.ENTREGUE 
            && status != StatusPedido.CANCELADO;
    }

    /**
     * Verifica se o pedido está finalizado (entregue ou cancelado).
     */
    public boolean estaFinalizado() {
        return status == StatusPedido.ENTREGUE || status == StatusPedido.CANCELADO;
    }

    /**
     * Calcula a quantidade total de itens no pedido.
     */
    public int calcularQuantidadeTotalItens() {
        return itens.stream()
                .mapToInt(ItemPedido::getQuantidade)
                .sum();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public InformacaoPagamento getInformacaoPagamento() {
        return informacaoPagamento;
    }

    public Dinheiro getValorTotal() {
        return valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public LocalDateTime getPagamentoConfirmadoEm() {
        return pagamentoConfirmadoEm;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public LocalDateTime getEntregueEm() {
        return entregueEm;
    }

    public LocalDateTime getCanceladoEm() {
        return canceladoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "numeroPedido='" + numeroPedido + '\'' +
                ", cliente=" + cliente.getNome() +
                ", quantidadeItens=" + itens.size() +
                ", valorTotal=" + valorTotal +
                ", status=" + status +
                ", criadoEm=" + criadoEm +
                '}';
    }

    /**
     * Enum para representar o status do pedido.
     * Fluxo: AGUARDANDO_PAGAMENTO -> PAGAMENTO_CONFIRMADO -> EM_PREPARACAO -> ENVIADO -> ENTREGUE
     * ou CANCELADO (pode ocorrer antes de ENVIADO)
     */
    public enum StatusPedido {
        AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
        PAGAMENTO_CONFIRMADO("Pagamento Confirmado"),
        EM_PREPARACAO("Em Preparação"),
        ENVIADO("Enviado"),
        ENTREGUE("Entregue"),
        CANCELADO("Cancelado");

        private final String descricao;

        StatusPedido(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
