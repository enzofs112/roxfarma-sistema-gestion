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
     * Token JWT generado para el usuario.
     * Debe enviarse en el header Authorization de las peticiones subsecuentes.
     */
    private String token;
    
    /**
     * Tipo de token (siempre "Bearer" para JWT).
     */
    private String tipo = "Bearer";
    
    /**
     * ID del usuario autenticado.
     */
    private Long idUsuario;
    
    /**
     * Nombre completo del usuario.
     */
    private String nombre;
    
    /**
     * Rol del usuario (ADMINISTRADOR o TRABAJADOR).
     * Usado en el frontend para mostrar/ocultar opciones según permisos.
     */
    private Rol rol;
    
    /**
     * Constructor sin el campo tipo (se establece por defecto).
     */
    public AuthResponse(String token, Long idUsuario, String nombre, Rol rol) {
        this.token = token;
        this.tipo = "Bearer";
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rol = rol;
    }
}
