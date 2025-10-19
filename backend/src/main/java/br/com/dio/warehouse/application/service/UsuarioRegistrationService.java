package br.com.dio.warehouse.application.service;

import br.com.dio.warehouse.adapter.in.web.dto.auth.RegisterRequest;
import br.com.dio.warehouse.domain.model.Role;
import br.com.dio.warehouse.domain.model.RoleEnum;
import br.com.dio.warehouse.domain.model.Usuario;
import br.com.dio.warehouse.infrastructure.persistence.RoleRepository;
import br.com.dio.warehouse.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de registro de novos usuários.
 * 
 * Responsável por validar dados, verificar duplicatas, hashear senhas
 * e persistir novos usuários no banco de dados com a role padrão.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioRegistrationService {
    
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Registra um novo usuário no sistema.
     * 
     * Processo:
     * 1. Valida os dados de entrada
     * 2. Verifica se username já existe
     * 3. Verifica se email já existe
     * 4. Verifica se as senhas são iguais
     * 5. Hasheia a senha com BCrypt
     * 6. Cria novo usuário
     * 7. Atribui role padrão (CUSTOMER)
     * 8. Persiste no banco de dados
     * 
     * @param request DTO com dados de registro (username, email, senha)
     * @return Usuario criado com sucesso
     * @throws IllegalArgumentException se validação falhar
     */
    @Transactional
    public Usuario registrarNovoUsuario(RegisterRequest request) {
        log.info("Iniciando registro de novo usuário: {}", request.username());
        
        // Validação 1: Campos obrigatórios
        validarCamposObrigatorios(request);
        
        // Validação 2: Verificar duplicatas
        if (usuarioRepository.existsByUsername(request.username())) {
            log.warn("Tentativa de registro com username duplicado: {}", request.username());
            throw new IllegalArgumentException("Username já existe no sistema");
        }
        
        if (usuarioRepository.existsByEmail(request.email())) {
            log.warn("Tentativa de registro com email duplicado: {}", request.email());
            throw new IllegalArgumentException("Email já existe no sistema");
        }
        
        // Validação 3: Confirmar senha
        if (!request.password().equals(request.passwordConfirm())) {
            log.warn("Senhas não conferem no registro: {}", request.username());
            throw new IllegalArgumentException("As senhas não conferem");
        }
        
        // Validação 4: Comprimento mínimo da senha
        if (request.password().length() < 8) {
            log.warn("Senha muito curta no registro: {}", request.username());
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres");
        }
        
        // Criar novo usuário
        String senhaHash = passwordEncoder.encode(request.password());
        
        Usuario novoUsuario = Usuario.builder()
            .username(request.username())
            .email(request.email())
            .senhaHash(senhaHash)
            .ativo(true)
            .build();
        
        // Atribuir role padrão (CUSTOMER)
        Role roleCustomer = roleRepository.findByNome(RoleEnum.CUSTOMER)
            .orElseThrow(() -> new IllegalArgumentException("Role CUSTOMER não encontrada"));
        
        novoUsuario.addRole(roleCustomer);
        
        // Persistir
        Usuario usuarioCriado = usuarioRepository.save(novoUsuario);
        
        log.info("Usuário registrado com sucesso: {} com role: {}", 
            novoUsuario.getUsername(), 
            RoleEnum.CUSTOMER.getLabel());
        
        return usuarioCriado;
    }
    
    /**
     * Valida se os campos obrigatórios foram informados.
     * 
     * @param request DTO com dados a validar
     * @throws IllegalArgumentException se algum campo obrigatório estiver vazio
     */
    private void validarCamposObrigatorios(RegisterRequest request) {
        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (request.passwordConfirm() == null || request.passwordConfirm().isBlank()) {
            throw new IllegalArgumentException("Confirmação de senha é obrigatória");
        }
        
        // Validar username (apenas alfanuméricos e underscore)
        if (!request.username().matches("^[a-zA-Z0-9_]{3,50}$")) {
            throw new IllegalArgumentException(
                "Username deve conter apenas letras, números e underscore (3-50 caracteres)");
        }
        
        // Validar email
        if (!request.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
}
