package com.roxfarma.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para transferencia de datos de Producto.
 * 
 * Incluye validaciones completas para garantizar la integridad de los datos.
 * 
 * @author Sistema RoxFarma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    
    /**
     * Nombre del producto.
     * Validaciones:
     * - Obligatorio
     * - Máximo 200 caracteres
     */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    /**
     * Descripción del producto.
     * Campo opcional.
     */
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    /**
     * Precio unitario del producto.
     * Validaciones:
     * - Obligatorio
     * - Debe ser mayor a 0
     * - Máximo 2 decimales
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal precio;
    
    /**
     * Fecha de vencimiento del producto.
     * Validaciones:
     * - Obligatorio
     * - Debe ser una fecha futura
     */
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;
    
    /**
     * Stock inicial del producto.
     * Validaciones:
     * - Obligatorio
     * - No puede ser negativo
     */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    /**
     * ID de la categoría a la que pertenece el producto.
     * Validación: Obligatorio
     */
    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;
}
