package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa el detalle de un Pedido.
 * Cada detalle contiene un producto y su cantidad solicitada.
 * 
 * Relaciones:
 * - Pertenece a un Pedido (muchos detalles por pedido)
 * - Referencia a un Producto
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "detalle_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {
    
    /**
     * Identificador único del detalle.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido")
    private Long idDetallePedido;
    
    /**
     * Relación muchos a uno con Pedido.
     * Muchos detalles pertenecen a un pedido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    
    /**
     * Relación muchos a uno con Producto.
     * Referencia al producto solicitado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    /**
     * Cantidad de unidades solicitadas del producto.
     */
    @Column(nullable = false)
    private Integer cantidad;
}
