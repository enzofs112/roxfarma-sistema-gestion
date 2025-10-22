package com.roxfarma.model;

/**
 * Enumeración que representa los estados de un pedido en el sistema
 * Flujo de estados:
 * PENDIENTE → ENVIADO → RECIBIDO
 * Reglas de negocio
 * - Un pedido inicia en estado PENDIENTE
 * - Solo se puede cambiar al siguiente estado en secuencia
 * - Cuando un pedido llega a RECIBIDO, se actualiza el stock automáticamente
 * - Un pedido en estado RECIBIDO no puede modificarse
 * @author grupo2
 */
public enum EstadoPedido {

    PENDIENTE,
    
    ENVIADO,
    
    RECIBIDO
}
