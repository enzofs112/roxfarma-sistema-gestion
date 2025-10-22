package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Cliente en el sistema RoxFarma.
 * Los clientes son farmacias o boticas que compran productos a RoxFarma.
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    /**
     * Identificador único del cliente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;
    
    /**
     * Nombre del cliente (ej: "Farmacia San Juan").
     */
    @Column(nullable = false, length = 200)
    private String nombre;
    
    /**
     * Número de documento (RUC) del cliente.
     * Debe ser único en el sistema.
     */
    @Column(nullable = false, unique = true, length = 20)
    private String documento;
    
    /**
     * Dirección del cliente.
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
