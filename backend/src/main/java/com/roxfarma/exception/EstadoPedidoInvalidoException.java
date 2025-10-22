package com.roxfarma.exception;

/**
 * Excepción lanzada cuando se intenta una transición de estado inválida en un pedido
 * Ejemplos de uso:
 * - Intentar cambiar de PENDIENTE a RECIBIDO (debe pasar por ENVIADO)
 * - Intentar modificar un pedido en estado RECIBIDO
 * Esta excepción es manejada por GlobalExceptionHandler
 * y retorna un error HTTP 400 (Bad Request).
 * @author grupo2
 */
public class EstadoPedidoInvalidoException extends RuntimeException {
    
    /**
     * Constructor con mensaje personalizado.
     * @param mensaje Descripción del error de estado
     */
    public EstadoPedidoInvalidoException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa.
     * @param mensaje Descripción del error de estado
     * @param causa Excepción que causó este error
     */
    public EstadoPedidoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
