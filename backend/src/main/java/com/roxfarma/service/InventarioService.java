package com.roxfarma.service;

import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.exception.StockInsuficienteException;
import com.roxfarma.model.Auditoria;
import com.roxfarma.model.Producto;
import com.roxfarma.repository.AuditoriaRepository;
import com.roxfarma.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de la gestión de inventario.
 * Funciones:
 * - Control de stock de productos
 * - Registro de movimientos (entrada/salida)
 * - Validación de stock disponible
 * - Auditoría de cambios
 * Uso:
 * - VentaService reduce stock
 * - PedidoService aumenta stock
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {
    
    private final ProductoRepository productoRepository;
    private final AuditoriaRepository auditoriaRepository;
    
    /**
 * Reduce el stock de un producto.
 * Usado en:
 * - Registro de ventas
 * - Ajustes de inventario
 * Pasos:
 * 1. Buscar producto
 * 2. Validar stock disponible
 * 3. Actualizar stock
 * 4. Registrar movimiento
     */
    @Transactional
    public void disminuirStock(Long idProducto, Integer cantidad, String motivo) {
        log.info("Disminuyendo stock del producto ID: {} en {} unidades. Motivo: {}", 
                idProducto, cantidad, motivo);
        
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + idProducto));
        
        if (producto.getStock() < cantidad) {
            String mensaje = String.format(
                    "Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                    producto.getNombre(), producto.getStock(), cantidad);
            log.warn(mensaje);
            throw new StockInsuficienteException(mensaje);
        }
        
        int stockAnterior = producto.getStock();
        producto.setStock(producto.getStock() - cantidad);
        
        productoRepository.save(producto);
        
        log.info("Stock actualizado para producto '{}'. Anterior: {}, Nuevo: {}", 
                producto.getNombre(), stockAnterior, producto.getStock());
        
        registrarMovimiento(producto, cantidad, motivo, "SALIDA", stockAnterior);
    }
    
    /**
 * Aumenta el stock de un producto.
 * Usado en:
 * - Recepción de pedidos de proveedor
 * - Ajustes de inventario
 * Pasos:
 * 1. Buscar producto
 * 2. Incrementar stock
 * 3. Guardar cambios y registrar movimiento
     */
    @Transactional
    public void aumentarStock(Long idProducto, Integer cantidad, String motivo) {
        log.info("Aumentando stock del producto ID: {} en {} unidades. Motivo: {}", 
                idProducto, cantidad, motivo);
        
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + idProducto));
        
        int stockAnterior = producto.getStock();
        producto.setStock(producto.getStock() + cantidad);
        
        productoRepository.save(producto);
        
        log.info("Stock actualizado para producto '{}'. Anterior: {}, Nuevo: {}", 
                producto.getNombre(), stockAnterior, producto.getStock());
        
        registrarMovimiento(producto, cantidad, motivo, "ENTRADA", stockAnterior);
    }
    
    /**
     * Registro de un movimiento de inventario en la auditoría
     */
    private void registrarMovimiento(Producto producto, Integer cantidad, 
                                     String motivo, String tipo, int stockAnterior) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setOperacion("MOVIMIENTO_INVENTARIO");
            auditoria.setEntidad("Producto");
            auditoria.setIdEntidad(producto.getIdProducto());
            auditoria.setUsuario(obtenerUsuarioActual());
            
            String detalles = String.format(
                    "Tipo: %s, Motivo: %s, Producto: %s, Cantidad: %d, Stock anterior: %d, Stock nuevo: %d",
                    tipo, motivo, producto.getNombre(), cantidad, stockAnterior, producto.getStock());
            auditoria.setDetalles(detalles);
            
            auditoriaRepository.save(auditoria);
            
            log.debug("Movimiento de inventario registrado en auditoría");
        } catch (Exception e) {
            log.error("Error al registrar movimiento en auditoría: {}", e.getMessage());
        }
    }
    
    // Obtiene el nombre del usuario actual desde el SecurityContext.
    private String obtenerUsuarioActual() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener usuario actual: {}", e.getMessage());
        }
        return "SYSTEM";
    }
}
