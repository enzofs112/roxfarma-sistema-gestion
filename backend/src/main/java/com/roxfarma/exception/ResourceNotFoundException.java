package com.roxfarma.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado
 * Esta excepción es manejada por GlobalExceptionHandler
 * y retorna un error HTTP 404 (Not Found)
 * @author grupo2
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ResourceNotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
