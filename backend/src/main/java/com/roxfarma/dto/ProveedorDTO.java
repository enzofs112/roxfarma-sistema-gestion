package com.roxfarma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Proveedor.
 * 
 * @author Sistema RoxFarma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    
    /**
     * Nombre del proveedor.
     */
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    /**
     * Información de contacto (teléfono, email).
     */
    @Size(max = 100, message = "El contacto no puede exceder 100 caracteres")
    private String contacto;
    
    /**
     * Dirección del proveedor.
     */
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;
}
