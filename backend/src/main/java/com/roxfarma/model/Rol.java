package com.roxfarma.model;

/**
 * Enumeración que representa los roles de usuario en el sistema RoxFarma.
 * 
 * Roles disponibles:
 * - ADMINISTRADOR: Acceso completo a todos los módulos
 * - TRABAJADOR: Acceso limitado a ventas, pedidos e inventario
 * 
 * @author Sistema RoxFarma
 */
public enum Rol {
    /**
     * Rol de Administrador.
     * Permisos: Gestión de usuarios, productos, clientes, proveedores, ventas, pedidos, reportes.
     */
    ADMINISTRADOR,
    
    /**
     * Rol de Trabajador.
     * Permisos: Registro de ventas, pedidos y consulta de inventario.
     */
    TRABAJADOR
}
