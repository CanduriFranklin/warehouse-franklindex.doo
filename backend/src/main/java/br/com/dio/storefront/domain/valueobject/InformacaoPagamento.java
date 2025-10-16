package br.com.dio.storefront.domain.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Value Object para representar informações de pagamento.
 * Imutável e validado.
 */
@Embeddable
public class InformacaoPagamento {

    @Enumerated(EnumType.STRING)
    private TipoPagamento tipo;
    
    private String numeroCartao; // Apenas últimos 4 dígitos
    private String nomeNoCartao;
    private String mesAnoValidade; // Formato: MM/AAAA
    private String codigoSeguranca; // Não será persistido (apenas validação)

    /**
     * Construtor protegido para JPA.
     */
    protected InformacaoPagamento() {
    }

    /**
     * Construtor privado.
     */
    private InformacaoPagamento(TipoPagamento tipo, String numeroCartao, 
                                String nomeNoCartao, String mesAnoValidade) {
        validar(tipo, numeroCartao, nomeNoCartao, mesAnoValidade);
        this.tipo = tipo;
        this.numeroCartao = mascarar(numeroCartao);
        this.nomeNoCartao = nomeNoCartao;
        this.mesAnoValidade = mesAnoValidade;
    }

    /**
     * Cria uma instância de InformacaoPagamento para cartão.
     */
    public static InformacaoPagamento cartao(String numeroCartao, String nomeNoCartao, 
                                             String mesAnoValidade, String codigoSeguranca) {
        validarCodigoSeguranca(codigoSeguranca);
        validarValidade(mesAnoValidade);
        return new InformacaoPagamento(TipoPagamento.CARTAO_CREDITO, numeroCartao, 
                                       nomeNoCartao, mesAnoValidade);
    }

    /**
     * Cria uma instância de InformacaoPagamento para PIX.
     */
    public static InformacaoPagamento pix() {
        return new InformacaoPagamento(TipoPagamento.PIX, null, null, null);
    }

    /**
     * Cria uma instância de InformacaoPagamento para boleto.
     */
    public static InformacaoPagamento boleto() {
        return new InformacaoPagamento(TipoPagamento.BOLETO, null, null, null);
    }

    /**
     * Valida os dados de pagamento.
     */
    private void validar(TipoPagamento tipo, String numeroCartao, 
                        String nomeNoCartao, String mesAnoValidade) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de pagamento é obrigatório");
        }
        
        if (tipo == TipoPagamento.CARTAO_CREDITO || tipo == TipoPagamento.CARTAO_DEBITO) {
            if (numeroCartao == null || !numeroCartao.matches("\\d{16}")) {
                throw new IllegalArgumentException("Número do cartão inválido");
            }
            if (nomeNoCartao == null || nomeNoCartao.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome no cartão é obrigatório");
            }
            if (mesAnoValidade == null || !mesAnoValidade.matches("(0[1-9]|1[0-2])/\\d{4}")) {
                throw new IllegalArgumentException("Validade inválida. Use o formato: MM/AAAA");
            }
        }
    }

    /**
     * Valida o código de segurança (não será persistido).
     */
    private static void validarCodigoSeguranca(String codigoSeguranca) {
        if (codigoSeguranca == null || !codigoSeguranca.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("Código de segurança inválido");
        }
    }

    /**
     * Valida se o cartão não está vencido.
     */
    private static void validarValidade(String mesAnoValidade) {
        if (mesAnoValidade != null && mesAnoValidade.matches("(0[1-9]|1[0-2])/\\d{4}")) {
            String[] partes = mesAnoValidade.split("/");
            int mes = Integer.parseInt(partes[0]);
            int ano = Integer.parseInt(partes[1]);
            YearMonth validade = YearMonth.of(ano, mes);
            
            if (validade.isBefore(YearMonth.now())) {
                throw new IllegalArgumentException("Cartão vencido");
            }
        }
    }

    /**
     * Mascara o número do cartão, mantendo apenas os últimos 4 dígitos.
     */
    private String mascarar(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.length() < 4) {
            return numeroCartao;
        }
        return "**** **** **** " + numeroCartao.substring(numeroCartao.length() - 4);
    }

    /**
     * Verifica se é pagamento com cartão.
     */
    public boolean ehCartao() {
        return tipo == TipoPagamento.CARTAO_CREDITO || tipo == TipoPagamento.CARTAO_DEBITO;
    }

    /**
     * Verifica se é pagamento instantâneo.
     */
    public boolean ehPagamentoInstantaneo() {
        return tipo == TipoPagamento.PIX || tipo == TipoPagamento.CARTAO_DEBITO;
    }

    // Getters
    public TipoPagamento getTipo() {
        return tipo;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getNomeNoCartao() {
        return nomeNoCartao;
    }

    public String getMesAnoValidade() {
        return mesAnoValidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformacaoPagamento that = (InformacaoPagamento) o;
        return tipo == that.tipo &&
               Objects.equals(numeroCartao, that.numeroCartao) &&
               Objects.equals(nomeNoCartao, that.nomeNoCartao) &&
               Objects.equals(mesAnoValidade, that.mesAnoValidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, numeroCartao, nomeNoCartao, mesAnoValidade);
    }

    @Override
    public String toString() {
        return "InformacaoPagamento{tipo=" + tipo + ", numeroCartao='" + numeroCartao + "'}";
    }

    /**
     * Enum para tipos de pagamento.
     */
    public enum TipoPagamento {
        CARTAO_CREDITO("Cartão de Crédito"),
        CARTAO_DEBITO("Cartão de Débito"),
        PIX("PIX"),
        BOLETO("Boleto");

        private final String descricao;

        TipoPagamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
