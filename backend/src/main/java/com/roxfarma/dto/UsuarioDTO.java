package com.roxfarma.dto;

import com.roxfarma.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Usuario
 * Nota de seguridad:
 * - La contraseña se envía en texto plano desde el frontend
 * - El servicio se encarga de cifrarla con BCrypt antes de guardar
 * - NUNCA se retorna la contraseña en las respuestas
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
    private String usuario;
    
    //Nota: Se cifra con BCrypt antes de guardar en BD
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;
    
    // Rol del usuario ADMINISTRADOR o TRABAJADOR
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
}
