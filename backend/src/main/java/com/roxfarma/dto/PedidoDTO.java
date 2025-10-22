package com.roxfarma.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para transferencia de datos de Pedido
 * Contiene el proveedor y la lista de productos a pedir
 * El estado inicial es PENDIENTE (se establece automáticamente)
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    
    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;

    @NotEmpty(message = "Debe incluir al menos un producto en el pedido")
    @Valid
    private List<DetallePedidoDTO> detalles;
}
