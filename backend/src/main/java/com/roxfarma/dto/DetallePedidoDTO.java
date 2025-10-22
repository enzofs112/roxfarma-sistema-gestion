package com.roxfarma.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Detalle de Pedido
 * Representa un producto dentro de un pedido con su cantidad
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {
    
    @NotNull(message = "El producto es obligatorio")
    private Long idProducto;
    
    // Cantidad de unidades a pedir
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
