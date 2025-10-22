package com.roxfarma.controller;

import com.roxfarma.dto.UsuarioDTO;
import com.roxfarma.model.Usuario;
import com.roxfarma.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Usuarios, solo para admin
 * @author grupo2
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        log.info("GET /api/usuarios");
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        log.info("GET /api/usuarios/{}", id);
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioDTO dto) {
        log.info("POST /api/usuarios - Creando usuario: {}", dto.getUsuario());
        Usuario usuario = usuarioService.crearUsuario(dto);
        return ResponseEntity.ok(usuario);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, 
                                                     @Valid @RequestBody UsuarioDTO dto) {
        log.info("PUT /api/usuarios/{}", id);
        Usuario usuario = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(usuario);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        log.info("DELETE /api/usuarios/{}", id);
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
