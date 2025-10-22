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
 * Contiene el cliente y la lista de productos a vender.
 * El total se calcula automáticamente en el servicio.
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    @NotEmpty(message = "Debe incluir al menos un producto en la venta")
    @Valid
    private List<DetalleVentaDTO> detalles;
}
