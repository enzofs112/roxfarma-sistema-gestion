package com.roxfarma.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Categoría.
 * 
 * Los DTOs (Data Transfer Objects) se usan para:
 * - Transferir datos entre capas (Controller ↔ Service)
 * - Validar datos de entrada del usuario
 * - Evitar exponer entidades JPA directamente
 * - Controlar qué campos se envían/reciben
 * 
 * @author Sistema RoxFarma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    
    /**
     * Nombre de la categoría.
     * Validaciones:
     * - No puede estar vacío (@NotBlank)
     * - Máximo 100 caracteres
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    /**
     * Descripción de la categoría.
     * Campo opcional, sin validaciones obligatorias.
     */
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
}
