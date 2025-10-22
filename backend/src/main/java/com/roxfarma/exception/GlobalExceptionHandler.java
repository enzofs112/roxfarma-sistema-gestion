package com.roxfarma.exception;

import com.roxfarma.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación
 * Responsabilidades:
 * - Capturar excepciones lanzadas en cualquier controlador
 * - Convertir excepciones en respuestas HTTP apropiadas
 * - Proporcionar mensajes de error consistentes
 * - Registrar errores en logs
 * Ventajas:
 * - Centraliza el manejo de errores
 * - Evita código repetitivo en controladores
 * - Proporciona respuestas consistentes al frontend
 * - Oculta detalles internos de errores al cliente
 * @author grupo2
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Maneja ResourceNotFoundException (404 Not Found)
     * Se lanza cuando no se encuentra un recurso solicitado
     * Ejemplos: Usuario no encontrado, Producto no encontrado, etc
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Maneja StockInsuficienteException (400 Bad Request)
     * Se lanza cuando se intenta vender más productos de los disponibles
     */
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex) {
        log.warn("Stock insuficiente: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja EstadoPedidoInvalidoException (400 Bad Request)
     * Se lanza cuando se intenta una transición de estado inválida en un pedido.
     */
    @ExceptionHandler(EstadoPedidoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleEstadoPedidoInvalido(EstadoPedidoInvalidoException ex) {
        log.warn("Estado de pedido inválido: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja MethodArgumentNotValidException (400 Bad Request)
     * Se lanza cuando falla la validación de un DTO con @Valid
     * Retorna un mapa con los errores de cada campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Errores de validación: {}", ex.getMessage());
        
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Errores de validación en los datos enviados",
                errores,
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja BadCredentialsException (401 Unauthorized)
     * Se lanza cuando las credenciales de login son incorrectas
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Credenciales inválidas: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Usuario o contraseña incorrectos",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Maneja AccessDeniedException (403 Forbidden)
     * Se lanza cuando un usuario intenta acceder a un recurso sin permisos
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "No tiene permisos para acceder a este recurso",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Maneja IllegalArgumentException (400 Bad Request)
     * Se lanza cuando se proporcionan argumentos inválidos
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Maneja cualquier otra excepción no capturada (500 Internal Server Error)
     * Este es el manejador de último recurso
     * Registra el error completo en logs pero solo retorna un mensaje genérico al cliente
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error no controlado: ", ex);
        
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ha ocurrido un error interno. Por favor contacte al administrador.",
                LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
