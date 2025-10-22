package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Pedido de reabastecimiento a un proveedor.
 * 
 * Lógica de negocio:
 * - RoxFarma crea pedidos para reabastecerse de productos
 * - Un pedido tiene estados: PENDIENTE → ENVIADO → RECIBIDO
 * - Cuando el estado cambia a RECIBIDO, el stock se actualiza automáticamente
 * - Un pedido puede tener múltiples productos (relación con DetallePedido)
 * 
 * Aplicación de SOLID:
 * - SRP: Solo representa un pedido, la lógica de actualización de stock está en el servicio
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    
    /**
     * Identificador único del pedido.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;
    
    /**
     * Fecha y hora de creación del pedido.
     */
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    /**
     * Estado actual del pedido.
     * Valores posibles: PENDIENTE, ENVIADO, RECIBIDO
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;
    
    /**
     * Relación muchos a uno con Proveedor.
     * Muchos pedidos pueden ser del mismo proveedor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;
    
    /**
     * Relación uno a muchos con DetallePedido.
     * Un pedido puede tener múltiples productos.
     * 
     * CascadeType.ALL: Las operaciones en el pedido se propagan a los detalles
     * orphanRemoval = true: Si se elimina un detalle de la lista, se elimina de la BD
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPedido.PENDIENTE;
        }
    }
    
    /**
     * Método auxiliar para agregar un detalle al pedido.
     * Mantiene la consistencia bidireccional de la relación.
     */
    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }
    
    /**
     * Método auxiliar para remover un detalle del pedido.
     */
    public void removerDetalle(DetallePedido detalle) {
        detalles.remove(detalle);
        detalle.setPedido(null);
    }
}
