package com.roxfarma.controller;

import com.roxfarma.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador REST para Reportes, solo accesible para admin.
 * @author grupo2
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReporteController {
    
    private final ReporteService reporteService;
    
    @GetMapping("/ventas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> generarReporteVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        log.info("GET /api/reportes/ventas - Desde: {} hasta: {}", fechaInicio, fechaFin);
        
        Map<String, Object> reporte = reporteService.generarReporteVentas(fechaInicio, fechaFin);
        
        return ResponseEntity.ok(reporte);
    }
    
    @GetMapping("/inventario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> generarReporteInventario() {
        log.info("GET /api/reportes/inventario");
        
        Map<String, Object> reporte = reporteService.generarReporteInventario();
        
        return ResponseEntity.ok(reporte);
    }
}
