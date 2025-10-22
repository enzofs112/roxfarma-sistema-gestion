package com.roxfarma.controller;

import com.roxfarma.dto.ClienteDTO;
import com.roxfarma.model.Cliente;
import com.roxfarma.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Cliente>> listarClientes() {
        log.info("GET /api/clientes");
        return ResponseEntity.ok(clienteService.listarClientes());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable Long id) {
        log.info("GET /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody ClienteDTO dto) {
        log.info("POST /api/clientes");
        return ResponseEntity.ok(clienteService.crearCliente(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, 
                                                     @Valid @RequestBody ClienteDTO dto) {
        log.info("PUT /api/clientes/{}", id);
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{}", id);
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
