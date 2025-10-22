package com.roxfarma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Proveedor.
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 100, message = "El contacto no puede exceder 100 caracteres")
    private String contacto;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;
}
