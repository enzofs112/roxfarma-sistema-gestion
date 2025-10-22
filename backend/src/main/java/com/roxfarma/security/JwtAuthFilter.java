package com.roxfarma.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT para Spring Security.
 * 
 * Este filtro intercepta TODAS las peticiones HTTP y:
 * 1. Extrae el token JWT del header Authorization
 * 2. Valida el token
 * 3. Si es válido, establece la autenticación en el SecurityContext
 * 4. Permite que la petición continúe
 * 
 * Flujo de autenticación:
 * Cliente → JwtAuthFilter → SecurityContext → Controller
 * 
 * Extiende OncePerRequestFilter para garantizar que se ejecuta una sola vez por petición.
 * 
 * Aplicación de SOLID:
 * - SRP: Solo se encarga de validar el token JWT
 * - DIP: Depende de abstracciones (JwtService, UserDetailsService)
 * 
 * @author Sistema RoxFarma
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    /**
     * Método principal del filtro que se ejecuta en cada petición.
     * 
     * Proceso:
     * 1. Extraer token del header Authorization
     * 2. Si no hay token, continuar sin autenticar
     * 3. Si hay token, validarlo
     * 4. Si es válido, cargar usuario y establecer autenticación
     * 5. Continuar con la cadena de filtros
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extraer el header Authorization
        final String authHeader = request.getHeader("Authorization");
        
        // 2. Verificar que el header existe y empieza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No se encontró token JWT en la petición a: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 3. Extraer el token (remover "Bearer " del inicio)
            final String jwt = authHeader.substring(7);
            
            // 4. Extraer el nombre de usuario del token
            final String usuario = jwtService.extraerUsuario(jwt);
            
            // 5. Si hay usuario y no hay autenticación previa en el contexto
            if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 6. Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(usuario);
                
                // 7. Validar el token
                if (jwtService.validarToken(jwt, userDetails)) {
                    
                    // 8. Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // 9. Agregar detalles de la petición
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 10. Establecer la autenticación en el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Usuario '{}' autenticado exitosamente para: {}", 
                            usuario, request.getRequestURI());
                } else {
                    log.warn("Token JWT inválido para usuario: {}", usuario);
                }
            }
        } catch (Exception e) {
            log.error("Error al procesar token JWT: {}", e.getMessage());
        }
        
        // 11. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
