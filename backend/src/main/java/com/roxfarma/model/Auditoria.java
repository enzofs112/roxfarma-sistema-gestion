package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un registro de Auditoría en el sistema
 * - Registrar todas las operaciones importantes del sistema
 * - Mantener trazabilidad de quién hizo qué y cuándo
 * - Cumplir con requerimientos de seguridad y auditoría
 * Se registran operaciones como:
 * - Creación, actualización y eliminación de registros
 * - Cambios de estado en pedidos
 * - Movimientos de inventario
 * - Accesos al sistema
 * 
 * @author grupo2
 */
@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Tipo de operación realizada
     * Ejemplos: "CREATE", "UPDATE", "DELETE", "LOGIN", "VENTA", "PEDIDO"
     */
    @Column(nullable = false, length = 50)
    private String operacion;
    
    /**
     * Nombre de la entidad afectada.
     * Ejemplos: "Producto", "Venta", "Usuario", "Pedido"
     */
    @Column(nullable = false, length = 50)
    private String entidad;
    
    // ID del registro afectado
    @Column(name = "id_entidad")
    private Long idEntidad;
    
    // Nombre del usuario que realizó la operación

    @Column(nullable = false, length = 50)
    private String usuario;
    
    // Fecha y hora de la operación

    @Column(nullable = false)
    private LocalDateTime fecha;
    
    /**
     * Detalles adicionales de la operación
     * Puede contener información como valores anteriores, nuevos valores, etc.
     */
    @Column(columnDefinition = "TEXT")
    private String detalles;
    
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
