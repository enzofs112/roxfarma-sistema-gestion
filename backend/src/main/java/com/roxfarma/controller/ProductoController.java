package com.roxfarma.controller;

import com.roxfarma.dto.ProductoDTO;
import com.roxfarma.model.Producto;
import com.roxfarma.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Productos.
 * Acceso:
 * - GET: ADMINISTRADOR y TRABAJADOR
 * - POST, PUT, DELETE: Solo ADMINISTRADOR
 * @author grupo2
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<List<Producto>> listarProductos() {
        log.info("GET /api/productos");
        List<Producto> productos = productoService.listarTodosLosProductos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id) {
        log.info("GET /api/productos/{}", id);
        Producto producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO dto) {
        log.info("POST /api/productos - Creando producto: {}", dto.getNombre());
        Producto producto = productoService.crearProducto(dto);
        return ResponseEntity.ok(producto);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, 
                                                       @Valid @RequestBody ProductoDTO dto) {
        log.info("PUT /api/productos/{}", id);
        Producto producto = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(producto);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        log.info("DELETE /api/productos/{}", id);
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/alertas/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<List<Producto>> obtenerStockBajo() {
        log.info("GET /api/productos/alertas/stock-bajo");
        List<Producto> productos = productoService.obtenerProductosConStockBajo(10);
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/alertas/proximos-vencer")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<List<Producto>> obtenerProximosVencer() {
        log.info("GET /api/productos/alertas/proximos-vencer");
        List<Producto> productos = productoService.obtenerProductosProximosAVencer(30);
        return ResponseEntity.ok(productos);
    }
}
