package com.roxfarma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Producto en el sistema
 Extensible mediante herencia si se necesitan tipos especiales de productos
 * @author grupo2
 */
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 100)
    private String presentacion;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
    
    @Column(nullable = false)
    private Integer stock;
    
    /** 
     * FetchType.LAZY: La categoría se carga solo cuando se accede a ella.
     * Esto mejora el rendimiento al no cargar datos innecesarios.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
