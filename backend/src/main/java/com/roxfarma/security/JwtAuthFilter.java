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
 * Se ejecuta en cada petición HTTP para:
 * - Extraer y validar el token JWT enviado en el header Authorization
 * - Autenticar al usuario si el token es válido
 * - Registrar la autenticación en el SecurityContext
 * 
 * Responsabilidad: Validar el acceso antes de llegar al controlador.
 * Flujo: Cliente → JwtAuthFilter → SecurityContext → Controller
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    /**
     * Método principal del filtro que se ejecuta en cada petición
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
        
        // Permitir acceso sin autenticación a endpoints de test y auth
        String path = request.getRequestURI();
        if (path.startsWith("/api/test/") || path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No se encontró token JWT en la petición a: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            final String jwt = authHeader.substring(7);
            
            final String usuario = jwtService.extraerUsuario(jwt);
            
            if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(usuario);
                
                if (jwtService.validarToken(jwt, userDetails)) {
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
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
        
        filterChain.doFilter(request, response);
    }
}
