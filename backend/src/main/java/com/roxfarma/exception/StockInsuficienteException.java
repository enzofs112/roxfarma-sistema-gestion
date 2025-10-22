package com.roxfarma.exception;

/**
 * Excepción lanzada cuando se intenta vender más productos de los disponibles en stock.
 * Esta excepción es manejada por GlobalExceptionHandler
 * y retorna un error HTTP 400 (Bad Request).
 * @author grupo2
 */
public class StockInsuficienteException extends RuntimeException {
    
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
