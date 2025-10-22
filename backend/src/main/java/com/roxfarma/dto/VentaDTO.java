package com.roxfarma.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para transferencia de datos de Venta.
 * 
 * Contiene el cliente y la lista de productos a vender.
 * El total se calcula automáticamente en el servicio.
 * 
 * @author Sistema RoxFarma
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    
    /**
     * ID del cliente que realiza la compra.
     */
    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;
    
    /**
     * Lista de productos con sus cantidades.
     * Validaciones:
     * - No puede estar vacía
     * - Cada detalle debe ser válido (@Valid)
     */
    @NotEmpty(message = "Debe incluir al menos un producto en la venta")
    @Valid
    private List<DetalleVentaDTO> detalles;
}
