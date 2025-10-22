package com.roxfarma.controller;

import com.roxfarma.dto.ProveedorDTO;
import com.roxfarma.model.Proveedor;
import com.roxfarma.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProveedorController {
    
    private final ProveedorService proveedorService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        log.info("GET /api/proveedores");
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Proveedor> obtenerProveedor(@PathVariable Long id) {
        log.info("GET /api/proveedores/{}", id);
        return ResponseEntity.ok(proveedorService.obtenerProveedorPorId(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Proveedor> crearProveedor(@Valid @RequestBody ProveedorDTO dto) {
        log.info("POST /api/proveedores");
        return ResponseEntity.ok(proveedorService.crearProveedor(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Proveedor> actualizarProveedor(@PathVariable Long id, 
                                                         @Valid @RequestBody ProveedorDTO dto) {
        log.info("PUT /api/proveedores/{}", id);
        return ResponseEntity.ok(proveedorService.actualizarProveedor(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        log.info("DELETE /api/proveedores/{}", id);
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
