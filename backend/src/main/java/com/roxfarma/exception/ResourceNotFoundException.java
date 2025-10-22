package com.roxfarma.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * 
 * Ejemplos de uso:
 * - Usuario no encontrado con ID: 5
 * - Producto no encontrado con ID: 10
 * - Cliente no encontrado con ID: 3
 * 
 * Esta excepción es manejada por GlobalExceptionHandler
 * y retorna un error HTTP 404 (Not Found).
 * 
 * @author Sistema RoxFarma
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param mensaje Descripción del recurso no encontrado
     */
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param mensaje Descripción del recurso no encontrado
     * @param causa Excepción que causó este error
     */
    public ResourceNotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
