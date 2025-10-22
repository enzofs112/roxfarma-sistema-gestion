package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Categoría de productos en el sistema RoxFarma.
 * Las categorías permiten organizar los productos (ej: Analgésicos, Antibióticos).
 * 
 * Aplicación de SOLID:
 * - SRP: Esta clase solo representa una categoría, sin lógica de negocio
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    
    /**
     * Identificador único de la categoría.
     * Se genera automáticamente con estrategia IDENTITY (auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;
    
    /**
     * Nombre de la categoría (ej: "Analgésicos", "Antibióticos").
     * Campo obligatorio.
     */
    @Column(nullable = false, length = 100)
    private String nombre;
    
    /**
     * Descripción detallada de la categoría.
     * Campo opcional.
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    /**
     * Fecha de creación del registro.
     * Se establece automáticamente al crear la categoría.
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    /**
     * Método que se ejecuta antes de persistir (INSERT).
     * Establece la fecha de creación automáticamente.
     */
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
