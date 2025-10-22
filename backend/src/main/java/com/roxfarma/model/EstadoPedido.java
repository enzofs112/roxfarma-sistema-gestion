package com.roxfarma.model;

/**
 * Enumeración que representa los estados de un Pedido en el sistema RoxFarma.
 * 
 * Flujo de estados:
 * PENDIENTE → ENVIADO → RECIBIDO
 * 
 * Reglas de negocio:
 * - Un pedido inicia en estado PENDIENTE
 * - Solo se puede cambiar al siguiente estado en secuencia
 * - Cuando un pedido llega a RECIBIDO, se actualiza el stock automáticamente
 * - Un pedido en estado RECIBIDO no puede modificarse
 * 
 * @author Sistema RoxFarma
 */
public enum EstadoPedido {
    /**
     * Estado inicial del pedido.
     * El pedido ha sido creado pero aún no ha sido enviado por el proveedor.
     */
    PENDIENTE,
    
    /**
     * El pedido ha sido enviado por el proveedor.
     * Está en tránsito hacia RoxFarma.
     */
    ENVIADO,
    
    /**
     * El pedido ha sido recibido por RoxFarma.
     * El stock de los productos se actualiza automáticamente.
     * Estado final, no se puede modificar después.
     */
    RECIBIDO
}
