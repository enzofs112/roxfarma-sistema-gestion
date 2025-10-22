package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Proveedor en el sistema RoxFarma.
 * Los proveedores son laboratorios o distribuidoras que suministran productos a RoxFarma.
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "proveedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {
    
    /**
     * Identificador único del proveedor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;
    
    /**
     * Nombre del proveedor (ej: "Laboratorios Perú SAC").
     */
    @Column(nullable = false, length = 200)
    private String nombre;
    
    /**
     * Información de contacto del proveedor (teléfono, email).
     */
    @Column(length = 100)
    private String contacto;
    
    /**
     * Dirección del proveedor.
     */
    @Column(length = 255)
    private String direccion;
    
    /**
     * Fecha de creación del registro.
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
