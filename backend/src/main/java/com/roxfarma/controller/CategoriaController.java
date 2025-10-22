package com.roxfarma.controller;

import com.roxfarma.dto.CategoriaDTO;
import com.roxfarma.model.Categoria;
import com.roxfarma.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CategoriaController {
    
    private final CategoriaService categoriaService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        log.info("GET /api/categorias");
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Categoria> obtenerCategoria(@PathVariable Long id) {
        log.info("GET /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.obtenerCategoriaPorId(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Categoria> crearCategoria(@Valid @RequestBody CategoriaDTO dto) {
        log.info("POST /api/categorias");
        return ResponseEntity.ok(categoriaService.crearCategoria(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Long id, 
                                                         @Valid @RequestBody CategoriaDTO dto) {
        log.info("PUT /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        log.info("DELETE /api/categorias/{}", id);
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
