package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Producto en el sistema RoxFarma.
 * Los productos son medicamentos que se venden a clientes y se compran a proveedores.
 * 
 * Características:
 * - Tiene stock que se actualiza automáticamente con ventas y pedidos
 * - Tiene fecha de vencimiento para generar alertas
 * - Pertenece a una categoría
 * 
 * Aplicación de SOLID:
 * - SRP: Solo representa un producto, sin lógica de negocio
 * - OCP: Extensible mediante herencia si se necesitan tipos especiales de productos
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    /**
     * Identificador único del producto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;
    
    /**
     * Nombre del producto (ej: "Paracetamol 500mg").
     */
    @Column(nullable = false, length = 200)
    private String nombre;
    
    /**
     * Descripción detallada del producto.
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    /**
     * Precio unitario del producto en soles.
     * Usa BigDecimal para precisión en cálculos monetarios.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    /**
     * Fecha de vencimiento del producto.
     * Se usa para generar alertas de productos próximos a vencer.
     */
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    
    /**
     * Cantidad disponible en inventario.
     * Se actualiza automáticamente:
     * - Disminuye al registrar ventas
     * - Aumenta al recibir pedidos
     */
    @Column(nullable = false)
    private Integer stock;
    
    /**
     * Relación muchos a uno con Categoría.
     * Muchos productos pertenecen a una categoría.
     * 
     * FetchType.LAZY: La categoría se carga solo cuando se accede a ella.
     * Esto mejora el rendimiento al no cargar datos innecesarios.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
    
    /**
     * Fecha de creación del registro.
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    /**
     * Fecha de última actualización.
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    /**
     * Se ejecuta antes de INSERT.
     */
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Se ejecuta antes de UPDATE.
     */
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
