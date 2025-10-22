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
 * 
 * Responsabilidades:
 * - Cargar usuarios desde la base de datos
 * - Convertir entidad Usuario a UserDetails de Spring Security
 * - Mapear roles a authorities (permisos)
 * 
 * Spring Security usa UserDetailsService para:
 * 1. Autenticación: Cargar usuario y verificar contraseña
 * 2. Autorización: Obtener roles y permisos del usuario
 * 
 * Aplicación de SOLID:
 * - SRP: Solo se encarga de cargar usuarios para Spring Security
 * - DIP: Depende de la abstracción UsuarioRepository
 * 
 * Inyección de Dependencias:
 * - UsuarioRepository se inyecta mediante constructor
 * - Spring gestiona el ciclo de vida automáticamente
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Carga un usuario por su nombre de usuario.
     * 
     * Este método es llamado por Spring Security durante:
     * - El proceso de autenticación (login)
     * - La validación del token JWT
     * 
     * Proceso:
     * 1. Buscar usuario en la base de datos
     * 2. Si no existe, lanzar UsernameNotFoundException
     * 3. Si existe, convertir a UserDetails
     * 4. Mapear rol a authority (ROLE_ADMINISTRADOR o ROLE_TRABAJADOR)
     * 
     * @param username Nombre de usuario
     * @return UserDetails con información del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Cargando usuario: {}", username);
        
        // 1. Buscar usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });
        
        // 2. Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            log.warn("Usuario inactivo: {}", username);
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }
        
        log.debug("Usuario '{}' cargado exitosamente con rol: {}", username, usuario.getRol());
        
        // 3. Convertir entidad Usuario a UserDetails de Spring Security
        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getContrasena()) // Contraseña ya cifrada con BCrypt
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }
    
    /**
     * Convierte el rol del usuario a authorities de Spring Security.
     * 
     * Spring Security usa el prefijo "ROLE_" para los roles.
     * Ejemplo:
     * - Rol.ADMINISTRADOR → ROLE_ADMINISTRADOR
     * - Rol.TRABAJADOR → ROLE_TRABAJADOR
     * 
     * Estos authorities se usan en:
     * - @PreAuthorize("hasRole('ADMINISTRADOR')")
     * - SecurityConfig para configurar acceso a endpoints
     * 
     * @param usuario Usuario del sistema
     * @return Collection de authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        // Agregar prefijo "ROLE_" al rol
        String authority = "ROLE_" + usuario.getRol().name();
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }
}
