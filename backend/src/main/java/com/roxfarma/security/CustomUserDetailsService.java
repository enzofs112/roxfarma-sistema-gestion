package com.roxfarma.security;

import com.roxfarma.model.Usuario;
import com.roxfarma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementación personalizada de UserDetailsService para Spring Security.
 * - Cargar usuarios desde la base de datos
 * - Convertir entidad Usuario a UserDetails de Spring Security
 * - Mapear roles a authorities
 * 
 * Spring Security usa UserDetailsService para:
 * 1. Autenticación: Cargar usuario y verificar contraseña
 * 2. Autorización: Obtener roles y permisos del usuario
 *
 * - Solo se encarga de cargar usuarios para Spring Security
 * - Depende de la abstracción UsuarioRepository
 * 
 * Inyección de Dependencias:
 * - UsuarioRepository se inyecta mediante constructor
 * - Spring gestiona el ciclo de vida automáticamente
 * 
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Carga un usuario por su nombre de usuario.
     * Este método es llamado por Spring Security durante:
     * - El proceso de autenticación login
     * - La validación del token JWT
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Cargando usuario: {}", username);
        
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });
        
        if (!usuario.getActivo()) {
            log.warn("Usuario inactivo: {}", username);
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }
        
        log.debug("Usuario '{}' cargado exitosamente con rol: {}", username, usuario.getRol());
        
        // Convertir entidad user a UserDetails de Spring Security
        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getContrasena()) 
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }
    
    //Convierte el rol del usuario a authorities de Spring Security.

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        String authority = "ROLE_" + usuario.getRol().name();
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }
}
