package br.com.dio.storefront.domain.valueobject;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object para representar valores monetários.
 * Imutável e sempre representado com 2 casas decimais.
 */
@Embeddable
public class Dinheiro {

    private BigDecimal valor;

    /**
     * Construtor protegido para JPA.
     */
    protected Dinheiro() {
    }

    /**
     * Construtor privado.
     * Use os métodos estáticos para criar instâncias.
     */
    private Dinheiro(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Cria uma instância de Dinheiro a partir de um BigDecimal.
     */
    public static Dinheiro de(BigDecimal valor) {
        return new Dinheiro(valor);
    }

    /**
     * Cria uma instância de Dinheiro a partir de um double.
     */
    public static Dinheiro de(double valor) {
        return new Dinheiro(BigDecimal.valueOf(valor));
    }

    /**
     * Cria uma instância de Dinheiro com valor zero.
     */
    public static Dinheiro zero() {
        return new Dinheiro(BigDecimal.ZERO);
    }

    /**
     * Adiciona outro valor de Dinheiro.
     */
    public Dinheiro adicionar(Dinheiro outro) {
        return new Dinheiro(this.valor.add(outro.valor));
    }

    /**
     * Subtrai outro valor de Dinheiro.
     */
    public Dinheiro subtrair(Dinheiro outro) {
        BigDecimal resultado = this.valor.subtract(outro.valor);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resultado não pode ser negativo");
        }
        return new Dinheiro(resultado);
    }

    /**
     * Multiplica por uma quantidade.
     */
    public Dinheiro multiplicar(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        return new Dinheiro(this.valor.multiply(BigDecimal.valueOf(quantidade)));
    }

    /**
     * Verifica se este valor é maior que outro.
     */
    public boolean maiorQue(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) > 0;
    }

    /**
     * Verifica se este valor é menor que outro.
     */
    public boolean menorQue(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) < 0;
    }

    /**
     * Verifica se o valor é zero.
     */
    public boolean ehZero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Retorna o valor como BigDecimal.
     */
    public BigDecimal getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dinheiro dinheiro = (Dinheiro) o;
        return valor.compareTo(dinheiro.valor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return "R$ " + valor.toString();
    }
}
