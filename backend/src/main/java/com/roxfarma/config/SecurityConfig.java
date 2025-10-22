package com.roxfarma.config;

import com.roxfarma.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security para RoxFarma.
 * 
 * Implementa:
 * - Autenticación con JWT (JSON Web Tokens)
 * - Autorización basada en roles (ADMINISTRADOR, TRABAJADOR)
 * - Sesiones stateless (sin estado en el servidor)
 * - Cifrado de contraseñas con BCrypt
 * 
 * Aplicación de SOLID:
 * - SRP: Esta clase solo configura la seguridad
 * - DIP: Depende de abstracciones (UserDetailsService, JwtAuthFilter)
 * 
 * @author Sistema RoxFarma
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    /**
     * Configura la cadena de filtros de seguridad.
     * 
     * Define:
     * - Qué endpoints son públicos y cuáles requieren autenticación
     * - Qué roles pueden acceder a cada endpoint
     * - Política de sesiones (stateless para JWT)
     * - Orden de filtros (JWT antes de autenticación)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (no necesario para API REST con JWT)
            .csrf(csrf -> csrf.disable())
            
            // Configurar autorización de endpoints
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/api/auth/**").permitAll()
                
                // Endpoints para ADMINISTRADOR y TRABAJADOR
                .requestMatchers("/api/productos/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/ventas/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/pedidos/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                
                // Endpoints solo para ADMINISTRADOR
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/clientes/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/proveedores/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/reportes/**").hasRole("ADMINISTRADOR")
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configurar sesiones como STATELESS (sin estado)
            // No se guardan sesiones en el servidor, todo se maneja con JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configurar proveedor de autenticación
            .authenticationProvider(authenticationProvider())
            
            // Agregar filtro JWT antes del filtro de autenticación de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Bean de PasswordEncoder para cifrar contraseñas.
     * 
     * Usa BCrypt con factor de trabajo 12:
     * - Factor 12 = 2^12 = 4096 iteraciones
     * - Más seguro pero más lento
     * - Cada cifrado genera un salt aleatorio
     * 
     * Ejemplo:
     * password123 → $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.eLb4W6
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    /**
     * Proveedor de autenticación que usa UserDetailsService y PasswordEncoder.
     * 
     * Proceso de autenticación:
     * 1. Recibe usuario y contraseña
     * 2. Carga el usuario desde UserDetailsService
     * 3. Compara la contraseña con BCrypt
     * 4. Si coincide, autentica al usuario
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    /**
     * Bean de AuthenticationManager para autenticación programática.
     * Usado en el servicio de autenticación para validar credenciales.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
