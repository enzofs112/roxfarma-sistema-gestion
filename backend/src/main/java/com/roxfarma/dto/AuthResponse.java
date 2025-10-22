package com.roxfarma.dto;

import com.roxfarma.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de autenticación exitosa.
 * 
 * Contiene el token JWT y la información del usuario autenticado.
 * 
 * @author Sistema RoxFarma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    /**
     Token JWT generado para el usuario.
     */
    private String token;
    
    private String tipo = "Bearer";
    
    private Long idUsuario;
    
    private String nombre;
    
    private Rol rol;
    
    public AuthResponse(String token, Long idUsuario, String nombre, Rol rol) {
        this.token = token;
        this.tipo = "Bearer";
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rol = rol;
    }
}
