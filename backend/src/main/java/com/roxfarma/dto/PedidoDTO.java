package com.roxfarma.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para transferencia de datos de Pedido.
 * 
 * Contiene el proveedor y la lista de productos a pedir.
 * El estado inicial es PENDIENTE (se establece automáticamente).
 * 
 * @author Sistema RoxFarma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    
    /**
     * ID del proveedor al que se hace el pedido.
     */
    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;
    
    /**
     * Lista de productos con sus cantidades.
     * Validaciones:
     * - No puede estar vacía
     * - Cada detalle debe ser válido (@Valid)
     */
    @NotEmpty(message = "Debe incluir al menos un producto en el pedido")
    @Valid
    private List<DetallePedidoDTO> detalles;
}
