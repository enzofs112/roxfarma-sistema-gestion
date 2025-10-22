package com.roxfarma.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de login (autenticación).
 * 
 * Contiene las credenciales del usuario.
 * 
 * @author Sistema RoxFarma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    /**
     * Nombre de usuario para login.
     */
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;
    
    /**
     * Contraseña del usuario.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}
