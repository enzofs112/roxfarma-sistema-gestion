package com.roxfarma.exception;

/**
 * Excepción lanzada cuando se intenta vender más productos de los disponibles en stock.
 * 
 * Ejemplos de uso:
 * - Intentar vender 10 unidades cuando solo hay 5 en stock
 * - Stock insuficiente para completar una venta
 * 
 * Esta excepción es manejada por GlobalExceptionHandler
 * y retorna un error HTTP 400 (Bad Request).
 * 
 * @author Sistema RoxFarma
 */
public class StockInsuficienteException extends RuntimeException {
    
    /**
     * Constructor con mensaje personalizado.
     * 
     * @param mensaje Descripción del error de stock
     */
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa.
     * 
     * @param mensaje Descripción del error de stock
     * @param causa Excepción que causó este error
     */
    public StockInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
