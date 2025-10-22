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
 * Servicio de lógica de negocio para gestión de Pedidos.
 * 
 * Responsabilidades:
 * - Registro de pedidos a proveedores
 * - Gestión de estados (PENDIENTE → ENVIADO → RECIBIDO)
 * - Actualización automática de stock al recibir pedidos
 * - Validación de transiciones de estado
 * 
 * Flujo de estados:
 * PENDIENTE: Pedido creado, esperando envío del proveedor
 * ENVIADO: Proveedor envió el pedido, en tránsito
 * RECIBIDO: Pedido recibido, stock actualizado automáticamente
 * 
 * Reglas de negocio:
 * - Solo se puede cambiar al siguiente estado en secuencia
 * - Un pedido en estado RECIBIDO no puede modificarse
 * - Al cambiar a RECIBIDO, se actualiza el stock automáticamente
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    
    /**
     * Crea un nuevo pedido a un proveedor.
     * 
     * El pedido se crea en estado PENDIENTE.
     * 
     * @param dto Datos del pedido
     * @return Pedido creado
     * @throws ResourceNotFoundException si proveedor o producto no existe
     */
    @Transactional
    public Pedido crearPedido(PedidoDTO dto) {
        log.info("Creando pedido para proveedor ID: {}", dto.getIdProveedor());
        
        // 1. Validar que el proveedor existe
        Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con ID: " + dto.getIdProveedor()));
        
        // 2. Validar que todos los productos existen
        validarProductos(dto.getDetalles());
        
        // 3. Crear pedido
        Pedido pedido = new Pedido();
        pedido.setProveedor(proveedor);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        
        // 4. Crear detalles
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
        
        // 5. Guardar pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        log.info("Pedido creado con ID: {} en estado PENDIENTE", pedidoGuardado.getIdPedido());
        
        return pedidoGuardado;
    }
    
    /**
     * Actualiza el estado de un pedido.
     * 
     * Validaciones:
     * - Solo se puede cambiar al siguiente estado en secuencia
     * - No se puede modificar un pedido en estado RECIBIDO
     * 
     * Acciones especiales:
     * - Al cambiar a RECIBIDO, se actualiza el stock automáticamente
     * 
     * @param id ID del pedido
     * @param nuevoEstado Nuevo estado
     * @return Pedido actualizado
     * @throws ResourceNotFoundException si el pedido no existe
     * @throws EstadoPedidoInvalidoException si la transición no es válida
     */
    @Transactional
    public Pedido actualizarEstadoPedido(Long id, EstadoPedido nuevoEstado) {
        log.info("Actualizando estado del pedido ID: {} a {}", id, nuevoEstado);
        
        // 1. Buscar pedido
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        
        EstadoPedido estadoActual = pedido.getEstado();
        
        // 2. Validar transición de estado
        validarTransicionEstado(estadoActual, nuevoEstado);
        
        // 3. Actualizar estado
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        log.info("Estado del pedido actualizado de {} a {}", estadoActual, nuevoEstado);
        
        // 4. Si el nuevo estado es RECIBIDO, actualizar stock
        if (nuevoEstado == EstadoPedido.RECIBIDO) {
            actualizarStockPorPedidoRecibido(pedido);
        }
        
        return pedidoActualizado;
    }
    
    /**
     * Valida que la transición de estado sea válida.
     * 
     * Transiciones válidas:
     * - PENDIENTE → ENVIADO
     * - ENVIADO → RECIBIDO
     * 
     * Transiciones inválidas:
     * - PENDIENTE → RECIBIDO (debe pasar por ENVIADO)
     * - RECIBIDO → cualquier otro (estado final)
     * - Retroceder estados
     * 
     * @param estadoActual Estado actual del pedido
     * @param nuevoEstado Nuevo estado solicitado
     * @throws EstadoPedidoInvalidoException si la transición no es válida
     */
    private void validarTransicionEstado(EstadoPedido estadoActual, EstadoPedido nuevoEstado) {
        // No se puede modificar un pedido recibido
        if (estadoActual == EstadoPedido.RECIBIDO) {
            throw new EstadoPedidoInvalidoException(
                    "No se puede modificar un pedido en estado RECIBIDO");
        }
        
        // Validar transiciones válidas
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
    
    /**
     * Actualiza el stock de los productos cuando un pedido es recibido.
     * 
     * @param pedido Pedido recibido
     */
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
    
    /**
     * Valida que todos los productos existan.
     * 
     * @param detalles Lista de detalles del pedido
     * @throws ResourceNotFoundException si un producto no existe
     */
    private void validarProductos(List<DetallePedidoDTO> detalles) {
        for (DetallePedidoDTO detalleDTO : detalles) {
            productoRepository.findById(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getIdProducto()));
        }
    }
    
    /**
     * Lista todos los pedidos.
     * 
     * @return Lista de pedidos
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos() {
        log.debug("Listando todos los pedidos");
        return pedidoRepository.findAll();
    }
    
    /**
     * Obtiene un pedido por ID.
     * 
     * @param id ID del pedido
     * @return Pedido encontrado
     * @throws ResourceNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(Long id) {
        log.debug("Buscando pedido ID: {}", id);
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
    }
    
    /**
     * Lista pedidos por estado.
     * 
     * @param estado Estado a filtrar
     * @return Lista de pedidos con el estado especificado
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosPorEstado(EstadoPedido estado) {
        log.debug("Listando pedidos con estado: {}", estado);
        return pedidoRepository.findByEstado(estado);
    }
}
