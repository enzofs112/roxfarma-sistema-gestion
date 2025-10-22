package com.roxfarma.service;

import com.roxfarma.dto.AuthResponse;
import com.roxfarma.dto.LoginRequest;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Usuario;
import com.roxfarma.repository.UsuarioRepository;
import com.roxfarma.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación para login de usuarios.
 * 
 * Responsabilidades:
 * - Validar credenciales de usuario
 * - Generar token JWT para usuarios autenticados
 * - Retornar información del usuario autenticado
 * 
 * Proceso de autenticación:
 * 1. Recibir usuario y contraseña
 * 2. Validar credenciales con AuthenticationManager
 * 3. Si son válidas, buscar usuario en BD
 * 4. Generar token JWT
 * 5. Retornar token y datos del usuario
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    
    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param request Credenciales del usuario
     * @return Respuesta con token JWT y datos del usuario
     * @throws BadCredentialsException si las credenciales son inválidas
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsuario());
        
        try {
            // 1. Autenticar con Spring Security
            // Esto valida usuario y contraseña usando UserDetailsService y PasswordEncoder
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsuario(),
                            request.getContrasena()
                    )
            );
            
            log.debug("Autenticación exitosa para usuario: {}", request.getUsuario());
            
            // 2. Si llega aquí, las credenciales son válidas
            // Buscar usuario en la base de datos
            Usuario usuario = usuarioRepository.findByUsuario(request.getUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario no encontrado: " + request.getUsuario()));
            
            // 3. Generar token JWT
            String token = jwtService.generarToken(usuario);
            
            log.info("Login exitoso para usuario: {} con rol: {}", 
                    usuario.getUsuario(), usuario.getRol());
            
            // 4. Retornar respuesta con token y datos del usuario
            return new AuthResponse(
                    token,
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getRol()
            );
            
        } catch (BadCredentialsException e) {
            log.warn("Intento de login fallido para usuario: {} - Credenciales inválidas", 
                    request.getUsuario());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }
    }
}
