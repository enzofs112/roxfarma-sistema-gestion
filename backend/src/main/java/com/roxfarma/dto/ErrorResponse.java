package com.roxfarma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para respuestas de error estandarizadas
   Proporciona información consistente sobre errores al frontend
 * @author grupo2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    //Código de estado HTTP (400, 404, 500, etc.)
    private int status;
    
    //Mensaje de error principal
    private String mensaje;
    
    private Map<String, String> detalles;

    private LocalDateTime timestamp;
    
    public ErrorResponse(int status, String mensaje, LocalDateTime timestamp) {
        this.status = status;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
    }
}
