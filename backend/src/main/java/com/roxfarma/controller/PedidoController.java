package com.roxfarma.controller;

import com.roxfarma.dto.PedidoDTO;
import com.roxfarma.model.EstadoPedido;
import com.roxfarma.model.Pedido;
import com.roxfarma.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *Controlador REST para gestión de Pedidos, para los roles
 *@author grupo2
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PedidoController {
    
    private final PedidoService pedidoService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody PedidoDTO dto) {
        log.info("POST /api/pedidos - Creando pedido");
        Pedido pedido = pedidoService.crearPedido(dto);
        return ResponseEntity.ok(pedido);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<List<Pedido>> listarPedidos() {
        log.info("GET /api/pedidos");
        List<Pedido> pedidos = pedidoService.listarPedidos();
        return ResponseEntity.ok(pedidos);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long id) {
        log.info("GET /api/pedidos/{}", id);
        Pedido pedido = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, 
                                                   @RequestParam EstadoPedido estado) {
        log.info("PUT /api/pedidos/{}/estado - Nuevo estado: {}", id, estado);
        Pedido pedido = pedidoService.actualizarEstadoPedido(id, estado);
        return ResponseEntity.ok(pedido);
    }
}
