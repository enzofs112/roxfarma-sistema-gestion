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
 * Controlador REST para autenticación de usuarios
 * Endpoints:
 * - POST /api/auth/login: Autenticar usuario y obtener token JWT
 * Este controlador es público, no requiere autenticación
 * @author grupo2
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") 
public class AuthController {
    
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Usuario: {}", request.getUsuario());
        
        AuthResponse response = authenticationService.login(request);
        
        return ResponseEntity.ok(response);
    }
}
