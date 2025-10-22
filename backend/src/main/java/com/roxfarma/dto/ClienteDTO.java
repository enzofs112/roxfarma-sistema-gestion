package com.roxfarma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Cliente
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    @NotBlank(message = "El documento es obligatorio")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos")
    private String documento;
    
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;
}
