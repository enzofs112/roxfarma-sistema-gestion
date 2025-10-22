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
 * Servicio de lógica de negocio para gestión de Inventario.
 * 
 * Responsabilidades:
 * - Control centralizado del stock
 * - Registro de movimientos de inventario
 * - Auditoría de cambios
 * - Validación de stock disponible
 * 
 * Este servicio es usado por:
 * - VentaService: Para disminuir stock al vender
 * - PedidoService: Para aumentar stock al recibir pedidos
 * 
 * Aplicación de SOLID:
 * - SRP: Solo gestiona operaciones de inventario
 * - OCP: Extensible para agregar nuevos tipos de movimientos
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {
    
    private final ProductoRepository productoRepository;
    private final AuditoriaRepository auditoriaRepository;
    
    /**
     * Disminuye el stock de un producto.
     * 
     * Usado cuando:
     * - Se registra una venta
     * - Se ajusta inventario manualmente
     * 
     * Proceso:
     * 1. Buscar producto
     * 2. Validar que hay stock suficiente
     * 3. Disminuir stock
     * 4. Guardar cambios
     * 5. Registrar en auditoría
     * 
     * @param idProducto ID del producto
     * @param cantidad Cantidad a disminuir
     * @param motivo Motivo del movimiento (VENTA, AJUSTE, etc.)
     * @throws ResourceNotFoundException si el producto no existe
     * @throws StockInsuficienteException si no hay stock suficiente
     */
    @Transactional
    public void disminuirStock(Long idProducto, Integer cantidad, String motivo) {
        log.info("Disminuyendo stock del producto ID: {} en {} unidades. Motivo: {}", 
                idProducto, cantidad, motivo);
        
        // 1. Buscar producto
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + idProducto));
        
        // 2. Validar stock suficiente
        if (producto.getStock() < cantidad) {
            String mensaje = String.format(
                    "Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                    producto.getNombre(), producto.getStock(), cantidad);
            log.warn(mensaje);
            throw new StockInsuficienteException(mensaje);
        }
        
        // 3. Disminuir stock
        int stockAnterior = producto.getStock();
        producto.setStock(producto.getStock() - cantidad);
        
        // 4. Guardar cambios
        productoRepository.save(producto);
        
        log.info("Stock actualizado para producto '{}'. Anterior: {}, Nuevo: {}", 
                producto.getNombre(), stockAnterior, producto.getStock());
        
        // 5. Registrar en auditoría
        registrarMovimiento(producto, cantidad, motivo, "SALIDA", stockAnterior);
    }
    
    /**
     * Aumenta el stock de un producto.
     * 
     * Usado cuando:
     * - Se recibe un pedido de proveedor
     * - Se ajusta inventario manualmente
     * 
     * Proceso:
     * 1. Buscar producto
     * 2. Aumentar stock
     * 3. Guardar cambios
     * 4. Registrar en auditoría
     * 
     * @param idProducto ID del producto
     * @param cantidad Cantidad a aumentar
     * @param motivo Motivo del movimiento (PEDIDO, AJUSTE, etc.)
     * @throws ResourceNotFoundException si el producto no existe
     */
    @Transactional
    public void aumentarStock(Long idProducto, Integer cantidad, String motivo) {
        log.info("Aumentando stock del producto ID: {} en {} unidades. Motivo: {}", 
                idProducto, cantidad, motivo);
        
        // 1. Buscar producto
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + idProducto));
        
        // 2. Aumentar stock
        int stockAnterior = producto.getStock();
        producto.setStock(producto.getStock() + cantidad);
        
        // 3. Guardar cambios
        productoRepository.save(producto);
        
        log.info("Stock actualizado para producto '{}'. Anterior: {}, Nuevo: {}", 
                producto.getNombre(), stockAnterior, producto.getStock());
        
        // 4. Registrar en auditoría
        registrarMovimiento(producto, cantidad, motivo, "ENTRADA", stockAnterior);
    }
    
    /**
     * Registra un movimiento de inventario en la auditoría.
     * 
     * @param producto Producto afectado
     * @param cantidad Cantidad del movimiento
     * @param motivo Motivo del movimiento
     * @param tipo Tipo de movimiento (ENTRADA o SALIDA)
     * @param stockAnterior Stock antes del movimiento
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
            // No lanzar excepción para no afectar la operación principal
        }
    }
    
    /**
     * Obtiene el nombre del usuario actual desde el SecurityContext.
     * 
     * @return Nombre del usuario autenticado o "SYSTEM" si no hay usuario
     */
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
