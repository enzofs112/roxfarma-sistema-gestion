package com.roxfarma.controller;

import com.roxfarma.dto.AuthResponse;
import com.roxfarma.dto.LoginRequest;
import com.roxfarma.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación de usuarios.
 * 
 * Endpoints:
 * - POST /api/auth/login: Autenticar usuario y obtener token JWT
 * 
 * Este controlador es público (no requiere autenticación).
 * 
 * @author Sistema RoxFarma
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Permitir CORS para desarrollo
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    /**
     * Endpoint de login.
     * 
     * POST /api/auth/login
     * 
     * Request body:
     * {
     *   "usuario": "admin",
     *   "contrasena": "password123"
     * }
     * 
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tipo": "Bearer",
     *   "idUsuario": 1,
     *   "nombre": "Administrador Sistema",
     *   "rol": "ADMINISTRADOR"
     * }
     * 
     * @param request Credenciales del usuario
     * @return Respuesta con token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Usuario: {}", request.getUsuario());
        
        AuthResponse response = authenticationService.login(request);
        
        return ResponseEntity.ok(response);
    }
}
