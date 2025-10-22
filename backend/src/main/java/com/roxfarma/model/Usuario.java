package com.roxfarma.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Usuario del sistema RoxFarma.
 * Los usuarios pueden ser Administradores o Trabajadores.
 * 
 * Seguridad:
 * - La contraseña se almacena cifrada con BCrypt
 * - El rol determina los permisos de acceso
 * 
 * @author Sistema RoxFarma
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    /**
     * Nombre completo del usuario (ej: "Juan Pérez").
     */
    @Column(nullable = false, length = 100)
    private String nombre;
    
    /**
     * Nombre de usuario para login (ej: "jperez").
     * Debe ser único en el sistema.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String usuario;
    
    /**
     * Contraseña cifrada con BCrypt.
     * NUNCA se almacena en texto plano.
     */
    @Column(nullable = false, length = 255)
    private String contrasena;
    
    /**
     * Rol del usuario (ADMINISTRADOR o TRABAJADOR).
     * Determina los permisos de acceso al sistema.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    
    /**
     * Indica si el usuario está activo.
     * Los usuarios inactivos no pueden iniciar sesión.
     */
    @Column(nullable = false)
    private Boolean activo = true;
    
    /**
     * Fecha de creación del usuario.
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }
}
