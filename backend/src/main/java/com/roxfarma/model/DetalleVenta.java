package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad que representa el detalle de una Venta.
 * Cada detalle contiene un producto, su cantidad y precio al momento de la venta.
 * 
 * Nota importante:
 * - El precio se guarda en el detalle para mantener histórico
 * - Si el precio del producto cambia después, las ventas anteriores mantienen el precio original
 * 
 * Relaciones:
 * - Pertenece a una Venta (muchos detalles por venta)
 * - Referencia a un Producto
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "detalle_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {
    
    /**
     * Identificador único del detalle.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long idDetalleVenta;
    
    /**
     * Relación muchos a uno con Venta.
     * Muchos detalles pertenecen a una venta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
    
    /**
     * Relación muchos a uno con Producto.
     * Referencia al producto vendido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    /**
     * Cantidad de unidades vendidas del producto.
     */
    @Column(nullable = false)
    private Integer cantidad;
    
    /**
     * Precio unitario del producto al momento de la venta.
     * Se guarda para mantener histórico, aunque el precio del producto cambie después.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    /**
     * Calcula el subtotal de este detalle (precio * cantidad).
     * No se persiste en la BD, se calcula en tiempo de ejecución.
     */
    @Transient
    public BigDecimal getSubtotal() {
        if (precio != null && cantidad != null) {
            return precio.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
}
