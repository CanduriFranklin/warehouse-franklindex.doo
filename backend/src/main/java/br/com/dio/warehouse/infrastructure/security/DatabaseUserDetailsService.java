package br.com.dio.warehouse.infrastructure.security;

import br.com.dio.warehouse.domain.model.Usuario;
import br.com.dio.warehouse.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementação de UserDetailsService que carrega usuários do banco de dados.
 * 
 * Substitui o InMemoryUserDetailsService anterior. Agora todos os usuários
 * são armazenados em banco de dados com senhas hasheadas em BCrypt.
 * 
 * Integração com Spring Security:
 * - Carregado automaticamente durante autenticação
 * - Busca usuário por username
 * - Carrega roles e permissões
 * - Retorna UserDetails para validação de senha
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Carrega detalhes de um usuário pelo username.
     * 
     * Chamado durante o processo de autenticação (login).
     * 
     * @param username O nome de usuário
     * @return UserDetails contendo credenciais e roles do usuário
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Tentando carregar usuário: {}", username);
        
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.warn("Usuário não encontrado: {}", username);
                return new UsernameNotFoundException("Usuário não encontrado: " + username);
            });
        
        // Verifica se usuário está ativo
        if (!usuario.getAtivo()) {
            log.warn("Tentativa de login com usuário desativado: {}", username);
            throw new UsernameNotFoundException("Usuário inativo: " + username);
        }
        
        log.debug("Usuário carregado com sucesso: {}. Roles: {}", 
            username, 
            usuario.getRoles().stream()
                .map(r -> r.getNome().name())
                .collect(Collectors.joining(", ")));
        
        return buildUserDetails(usuario);
    }
    
    /**
     * Constrói um UserDetails do Spring Security a partir de um Usuario.
     * 
     * Converte as roles do usuário para GrantedAuthority.
     * 
     * @param usuario O usuário do banco de dados
     * @return UserDetails configurado para autenticação
     */
    private UserDetails buildUserDetails(Usuario usuario) {
        Collection<GrantedAuthority> authorities = buildAuthorities(usuario);
        
        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getSenhaHash())
            .authorities(authorities)
            .accountLocked(false)
            .accountExpired(false)
            .credentialsExpired(false)
            .disabled(!usuario.getAtivo())
            .build();
    }
    
    /**
     * Constrói a coleção de GrantedAuthority a partir das roles do usuário.
     * 
     * Cada role é convertida em um SimpleGrantedAuthority com prefixo "ROLE_".
     * Exemplo: Role.ADMIN → GrantedAuthority "ROLE_ADMIN"
     * 
     * @param usuario O usuário com suas roles
     * @return Coleção de GrantedAuthority
     */
    private Collection<GrantedAuthority> buildAuthorities(Usuario usuario) {
        return usuario.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getNome().name()))
            .collect(Collectors.toSet());
    }
    
    /**
     * Atualiza o timestamp do último acesso do usuário.
     * 
     * Deve ser chamado após autenticação bem-sucedida.
     * 
     * @param username O nome de usuário que fez login
     */
    @Transactional
    public void updateUltimoAcesso(String username) {
        usuarioRepository.findByUsername(username)
            .ifPresent(usuario -> {
                usuario.updateUltimoAcesso();
                usuarioRepository.save(usuario);
                log.debug("Último acesso atualizado para usuário: {}", username);
            });
    }
}
