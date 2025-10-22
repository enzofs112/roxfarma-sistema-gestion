package com.roxfarma.service;

import com.roxfarma.dto.DetallePedidoDTO;
import com.roxfarma.dto.PedidoDTO;
import com.roxfarma.exception.EstadoPedidoInvalidoException;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.*;
import com.roxfarma.repository.PedidoRepository;
import com.roxfarma.repository.ProductoRepository;
import com.roxfarma.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de lógica de negocio para gestión de Pedidos
 * - Un pedido en estado RECIBIDO no puede modificarse
 * - Al cambiar a RECIBIDO, se actualiza el stock automáticamente
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    
    @Transactional
    public Pedido crearPedido(PedidoDTO dto) {
        log.info("Creando pedido para proveedor ID: {}", dto.getIdProveedor());
        
        Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con ID: " + dto.getIdProveedor()));
        
        validarProductos(dto.getDetalles());
        
        Pedido pedido = new Pedido();
        pedido.setProveedor(proveedor);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        
        List<DetallePedido> detalles = new ArrayList<>();
        
        for (DetallePedidoDTO detalleDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getIdProducto()).get();
            
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            
            detalles.add(detalle);
        }
        
        pedido.setDetalles(detalles);
        
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        log.info("Pedido creado con ID: {} en estado PENDIENTE", pedidoGuardado.getIdPedido());
        
        return pedidoGuardado;
    }

    @Transactional
    public Pedido actualizarEstadoPedido(Long id, EstadoPedido nuevoEstado) {
        log.info("Actualizando estado del pedido ID: {} a {}", id, nuevoEstado);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        
        EstadoPedido estadoActual = pedido.getEstado();
        
        validarTransicionEstado(estadoActual, nuevoEstado);
        
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        log.info("Estado del pedido actualizado de {} a {}", estadoActual, nuevoEstado);
        
        if (nuevoEstado == EstadoPedido.RECIBIDO) {
            actualizarStockPorPedidoRecibido(pedido);
        }
        
        return pedidoActualizado;
    }

    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        if (estadoActual == EstadoPedido.RECIBIDO) {
            throw new EstadoPedidoInvalidoException(
                    "No se puede modificar un pedido en estado RECIBIDO");
        }
        
        boolean transicionValida = false;
        
        if (estadoActual == EstadoPedido.PENDIENTE && nuevoEstado == EstadoPedido.ENVIADO) {
            transicionValida = true;
        } else if (estadoActual == EstadoPedido.ENVIADO && nuevoEstado == EstadoPedido.RECIBIDO) {
            transicionValida = true;
        }
        
        if (!transicionValida) {
            String mensaje = String.format(
                    "Transición de estado inválida: %s → %s. " +
                    "Las transiciones válidas son: PENDIENTE → ENVIADO → RECIBIDO",
                    estadoActual, nuevoEstado);
            throw new EstadoPedidoInvalidoException(mensaje);
        }
    }
    
    private void actualizarStockPorPedidoRecibido(Pedido pedido) {
        log.info("Actualizando stock por pedido recibido ID: {}", pedido.getIdPedido());
        
        for (DetallePedido detalle : pedido.getDetalles()) {
            inventarioService.aumentarStock(
                    detalle.getProducto().getIdProducto(),
                    detalle.getCantidad(),
                    "PEDIDO_RECIBIDO"
            );
        }
        
        log.info("Stock actualizado para {} productos del pedido", pedido.getDetalles().size());
    }
    

    private void validarProductos(List<DetallePedidoDTO> detalles) {
        for (DetallePedidoDTO detalleDTO : detalles) {
            productoRepository.findById(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getIdProducto()));
        }
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos() {
        log.debug("Listando todos los pedidos");
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(Long id) {
        log.debug("Buscando pedido ID: {}", id);
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosPorEstado(EstadoPedido estado) {
        log.debug("Listando pedidos con estado: {}", estado);
        return pedidoRepository.findByEstado(estado);
    }
}
