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
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Configuración de seguridad de Spring Security para RoxFarma
 * Implementa:
 * - Autenticación con JWT (JSON Web Tokens)
 * - Autorización basada en roles (ADMINISTRADOR, TRABAJADOR)
 * - Sesiones stateless (sin estado en el servidor)
 * - Cifrado de contraseñas con BCrypt
 * Aplicación de SOLID:
 * - Esta clase solo configura la seguridad
 * - Depende de abstracciones (UserDetailsService, JwtAuthFilter)
 * 
 * @author grupo2
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - DEBEN IR PRIMERO
                .requestMatchers("/api/auth/**", "/api/test/**").permitAll()
                // Permitir peticiones OPTIONS (CORS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Permitir acceso a recursos estáticos
                .requestMatchers("/favicon.ico", "/static/**", "/public/**").permitAll()

                .requestMatchers("/api/productos/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/ventas/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/pedidos/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/clientes/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/proveedores/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/reportes/**").hasRole("ADMINISTRADOR")
                
                .anyRequest().authenticated()
            )
            
            // No se guardan sesiones en el servidor, todo se maneja con JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authenticationProvider(authenticationProvider())

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
