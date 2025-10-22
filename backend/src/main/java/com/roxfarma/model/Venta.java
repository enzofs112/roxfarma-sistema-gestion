package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Venta en el sistema RoxFarma.
 * 
 * Lógica de negocio:
 * - RoxFarma vende productos a clientes (farmacias/boticas)
 * - Cada venta genera una boleta PDF
 * - El stock de los productos se disminuye automáticamente
 * - El total incluye IGV (18%)
 * - Una venta puede tener múltiples productos (relación con DetalleVenta)
 * 
 * Relaciones:
 * - Pertenece a un Cliente
 * - Registrada por un Usuario
 * - Tiene múltiples DetalleVenta
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    /**
     * Identificador único de la venta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;
    
    /**
     * Fecha y hora de la venta.
     */
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    /**
     * Relación muchos a uno con Cliente.
     * Muchas ventas pueden ser del mismo cliente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    /**
     * Relación muchos a uno con Usuario.
     * Registra qué usuario realizó la venta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    /**
     * Total de la venta (incluye IGV del 18%).
     * Cálculo: Subtotal + IGV = Total
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    /**
     * Relación uno a muchos con DetalleVenta.
     * Una venta puede tener múltiples productos.
     * 
     * CascadeType.ALL: Las operaciones en la venta se propagan a los detalles
     * orphanRemoval = true: Si se elimina un detalle de la lista, se elimina de la BD
     */
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
    
    /**
     * Método auxiliar para agregar un detalle a la venta.
     * Mantiene la consistencia bidireccional de la relación.
     */
    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }
    
    /**
     * Método auxiliar para remover un detalle de la venta.
     */
    public void removerDetalle(DetalleVenta detalle) {
        detalles.remove(detalle);
        detalle.setVenta(null);
    }
}
