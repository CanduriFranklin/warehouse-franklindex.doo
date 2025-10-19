package br.com.dio.warehouse.adapter.in.web.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de registro de novo usuário.
 * 
 * Contém as informações necessárias para criar uma nova conta:
 * - username: Nome único de usuário (3-50 caracteres, alfanuméricos + underscore)
 * - email: Email único do usuário
 * - password: Senha (mínimo 8 caracteres)
 * - passwordConfirm: Confirmação de senha (deve ser igual a password)
 */
public record RegisterRequest(
    /**
     * Nome de usuário único para login.
     * 
     * Requisitos:
     * - Mínimo 3 caracteres
     * - Máximo 50 caracteres
     * - Apenas letras, números e underscore
     * - Case-sensitive (admin != Admin)
     */
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    String username,
    
    /**
     * Email único do usuário.
     * 
     * Requisitos:
     * - Deve ser um email válido
     * - Será usado para recuperação de conta
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    String email,
    
    /**
     * Senha do usuário.
     * 
     * Requisitos:
     * - Mínimo 8 caracteres
     * - Será hasheada com BCrypt antes de ser armazenada
     * - Nunca deve ser armazenada em texto plano
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 100, message = "Senha deve ter entre 8 e 100 caracteres")
    String password,
    
    /**
     * Confirmação da senha.
     * 
     * Requisitos:
     * - Deve ser idêntica ao campo 'password'
     * - Usado para validar que o usuário digitou a senha corretamente
     */
    @NotBlank(message = "Confirmação de senha é obrigatória")
    String passwordConfirm
) {
    /**
     * Validação customizada: confirmar se as senhas são iguais.
     * 
     * @return true se as senhas conferem, false caso contrário
     */
    public boolean senhasConferem() {
        return password != null && password.equals(passwordConfirm);
    }
}
