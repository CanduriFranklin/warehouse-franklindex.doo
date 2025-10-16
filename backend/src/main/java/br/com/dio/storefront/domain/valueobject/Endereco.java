package br.com.dio.storefront.domain.valueobject;

import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Value Object para representar um endereço de entrega.
 * Imutável e validado.
 */
@Embeddable
public class Endereco {

    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    /**
     * Construtor protegido para JPA.
     */
    protected Endereco() {
    }

    /**
     * Construtor privado.
     */
    private Endereco(String rua, String numero, String complemento, 
                     String bairro, String cidade, String estado, String cep) {
        validar(rua, numero, bairro, cidade, estado, cep);
        this.rua = rua;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    /**
     * Cria uma instância de Endereço.
     */
    public static Endereco de(String rua, String numero, String complemento,
                              String bairro, String cidade, String estado, String cep) {
        return new Endereco(rua, numero, complemento, bairro, cidade, estado, cep);
    }

    /**
     * Valida os campos obrigatórios do endereço.
     */
    private void validar(String rua, String numero, String bairro, 
                         String cidade, String estado, String cep) {
        if (rua == null || rua.trim().isEmpty()) {
            throw new IllegalArgumentException("Rua é obrigatória");
        }
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Número é obrigatório");
        }
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        if (cep == null || !cep.matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido. Use o formato: 12345-678");
        }
    }

    /**
     * Retorna o endereço formatado para exibição.
     */
    public String formatado() {
        StringBuilder sb = new StringBuilder();
        sb.append(rua).append(", ").append(numero);
        if (complemento != null && !complemento.trim().isEmpty()) {
            sb.append(" - ").append(complemento);
        }
        sb.append(", ").append(bairro);
        sb.append(", ").append(cidade).append(" - ").append(estado);
        sb.append(", CEP: ").append(cep);
        return sb.toString();
    }

    // Getters
    public String getRua() {
        return rua;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getCep() {
        return cep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(rua, endereco.rua) &&
               Objects.equals(numero, endereco.numero) &&
               Objects.equals(complemento, endereco.complemento) &&
               Objects.equals(bairro, endereco.bairro) &&
               Objects.equals(cidade, endereco.cidade) &&
               Objects.equals(estado, endereco.estado) &&
               Objects.equals(cep, endereco.cep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rua, numero, complemento, bairro, cidade, estado, cep);
    }

    @Override
    public String toString() {
        return formatado();
    }
}
