package br.com.dio.storefront.domain.model;

import br.com.dio.storefront.domain.valueobject.Endereco;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa um Cliente da loja.
 * Aggregate Root do contexto de Cliente.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(length = 15)
    private String telefone;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "rua", column = @Column(name = "endereco_rua")),
        @AttributeOverride(name = "numero", column = @Column(name = "endereco_numero")),
        @AttributeOverride(name = "complemento", column = @Column(name = "endereco_complemento")),
        @AttributeOverride(name = "bairro", column = @Column(name = "endereco_bairro")),
        @AttributeOverride(name = "cidade", column = @Column(name = "endereco_cidade")),
        @AttributeOverride(name = "estado", column = @Column(name = "endereco_estado")),
        @AttributeOverride(name = "cep", column = @Column(name = "endereco_cep"))
    })
    private Endereco endereco;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    /**
     * Construtor protegido para JPA.
     */
    protected Cliente() {
    }

    /**
     * Construtor privado para criação de cliente.
     */
    private Cliente(String nome, String email, String cpf, String telefone, Endereco endereco) {
        validar(nome, email, cpf);
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.email = email;
        this.cpf = limparCpf(cpf);
        this.telefone = telefone;
        this.endereco = endereco;
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Factory method para criar um novo cliente.
     */
    public static Cliente criar(String nome, String email, String cpf, String telefone, Endereco endereco) {
        return new Cliente(nome, email, cpf, telefone, endereco);
    }

    /**
     * Valida os dados obrigatórios do cliente.
     */
    private void validar(String nome, String email, String cpf) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (!validarCpf(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
    }

    /**
     * Valida o CPF usando o algoritmo de dígitos verificadores.
     */
    private boolean validarCpf(String cpf) {
        if (cpf == null) {
            return false;
        }
        
        String cpfLimpo = limparCpf(cpf);
        
        if (cpfLimpo.length() != 11 || cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Calcula primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;

            // Calcula segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;

            // Verifica se os dígitos calculados conferem
            return Character.getNumericValue(cpfLimpo.charAt(9)) == primeiroDigito &&
                   Character.getNumericValue(cpfLimpo.charAt(10)) == segundoDigito;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove caracteres não numéricos do CPF.
     */
    private String limparCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Atualiza as informações do cliente.
     */
    public void atualizar(String nome, String email, String telefone, Endereco endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Atualiza o endereço do cliente.
     */
    public void atualizarEndereco(Endereco novoEndereco) {
        if (novoEndereco == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo");
        }
        this.endereco = novoEndereco;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Ativa o cliente.
     */
    public void ativar() {
        this.ativo = true;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Desativa o cliente.
     */
    public void desativar() {
        this.ativo = false;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Formata o CPF para exibição.
     */
    public String getCpfFormatado() {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public Endereco getEndereco() {
        return endereco;
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
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + getCpfFormatado() + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
