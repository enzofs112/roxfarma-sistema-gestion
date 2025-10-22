package com.roxfarma.controller;

import com.roxfarma.model.Producto;
import com.roxfarma.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *Controlador REST para el Dashboard, inventario y alertas
 *@author grupo2
 **/
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final ProductoService productoService;
    
    @GetMapping("/alertas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Map<String, Object>> obtenerAlertas() {
        log.info("GET /api/dashboard/alertas");
        
        Map<String, Object> alertas = new HashMap<>();
        
        // stock bajo
        List<Producto> stockBajo = productoService.obtenerProductosConStockBajo(10);
        alertas.put("stockBajo", stockBajo);
        alertas.put("cantidadStockBajo", stockBajo.size());
        
        // vencimiento
        List<Producto> proximosVencer = productoService.obtenerProductosProximosAVencer(30);
        alertas.put("proximosVencer", proximosVencer);
        alertas.put("cantidadProximosVencer", proximosVencer.size());
        
        return ResponseEntity.ok(alertas);
    }
    
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("GET /api/dashboard/estadisticas");
        
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Total de productos
        List<Producto> productos = productoService.listarTodosLosProductos();
        estadisticas.put("totalProductos", productos.size());
        
        int stockTotal = productos.stream()
                .mapToInt(Producto::getStock)
                .sum();
        estadisticas.put("stockTotal", stockTotal);
        
        estadisticas.put("alertasStockBajo", productoService.obtenerProductosConStockBajo(10).size());
        estadisticas.put("alertasVencimiento", productoService.obtenerProductosProximosAVencer(30).size());
        
        return ResponseEntity.ok(estadisticas);
    }
}
