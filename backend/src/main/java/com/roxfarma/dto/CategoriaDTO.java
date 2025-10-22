package com.roxfarma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de categoría
 * Los DTOs (Data Transfer Objects) se usan para:
 * - Transferir datos entre capas (Controller ↔ Service)
 * - Validar datos de entrada del usuario
 * - Evitar exponer entidades JPA directamente
 * - Controlar qué campos se envían/reciben
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
}
