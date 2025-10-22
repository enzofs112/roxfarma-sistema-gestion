package com.roxfarma.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 DTO para transferencia de datos de Detalle de Venta
 Representa un producto dentro de una venta con su cantidad
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {
    
    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
