package com.roxfarma.model;

/**
 * Enumeración que representa los roles de usuario en el sistema
 * Roles disponibles:
 * - ADMINISTRADOR: Acceso completo a todos los módulos
 * - TRABAJADOR: Acceso limitado a ventas, pedidos e inventario
 * @author grupo2
 */
public enum Rol {
// Permisos: Gestión de usuarios, productos, clientes, proveedores, ventas, pedidos, reportes.
    ADMINISTRADOR,
    
// Permisos: Registro de ventas, pedidos y consulta de inventario.
    TRABAJADOR
}
