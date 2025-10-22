package com.roxfarma.service;

import com.roxfarma.dto.DetalleVentaDTO;
import com.roxfarma.dto.VentaDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.exception.StockInsuficienteException;
import com.roxfarma.model.*;
import com.roxfarma.repository.ClienteRepository;
import com.roxfarma.repository.ProductoRepository;
import com.roxfarma.repository.UsuarioRepository;
import com.roxfarma.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de lógica de negocio para gestión de Ventas.
 * 
 * Responsabilidades:
 * - Registro de ventas
 * - Cálculo de totales (subtotal, IGV, total)
 * - Actualización automática de stock
 * - Validación de stock disponible
 * - Consultas de ventas
 * 
 * Lógica de negocio:
 * 1. Validar que el cliente existe
 * 2. Validar que todos los productos existen
 * 3. Validar que hay stock suficiente para cada producto
 * 4. Calcular subtotal, IGV (18%) y total
 * 5. Crear venta con detalles
 * 6. Disminuir stock de cada producto (mediante InventarioService)
 * 7. Guardar venta
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;
    
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18"); // 18%
    
    /**
     * Registra una nueva venta.
     * 
     * Este método es transaccional: si algo falla, se revierte todo.
     * 
     * @param dto Datos de la venta
     * @return Venta registrada
     * @throws ResourceNotFoundException si cliente o producto no existe
     * @throws StockInsuficienteException si no hay stock suficiente
     */
    @Transactional
    public Venta registrarVenta(VentaDTO dto) {
        log.info("Iniciando registro de venta para cliente ID: {}", dto.getIdCliente());
        
        // 1. Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con ID: " + dto.getIdCliente()));
        
        // 2. Obtener usuario actual
        Usuario usuario = obtenerUsuarioActual();
        
        // 3. Validar productos y stock
        validarProductosYStock(dto.getDetalles());
        
        // 4. Crear venta
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        
        // 5. Crear detalles y calcular total
        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (DetalleVentaDTO detalleDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getIdProducto()).get();
            
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecio(producto.getPrecio()); // Guardar precio actual
            
            detalles.add(detalle);
            
            // Calcular subtotal
            BigDecimal precioDetalle = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));
            subtotal = subtotal.add(precioDetalle);
        }
        
        venta.setDetalles(detalles);
        
        // 6. Calcular IGV y total
        BigDecimal igv = subtotal.multiply(IGV_RATE);
        BigDecimal total = subtotal.add(igv);
        venta.setTotal(total);
        
        // 7. Guardar venta
        Venta ventaGuardada = ventaRepository.save(venta);
        
        log.info("Venta guardada con ID: {}", ventaGuardada.getIdVenta());
        
        // 8. Actualizar stock de cada producto
        for (DetalleVenta detalle : detalles) {
            inventarioService.disminuirStock(
                    detalle.getProducto().getIdProducto(),
                    detalle.getCantidad(),
                    "VENTA"
            );
        }
        
        log.info("Venta registrada exitosamente. ID: {}, Total: {}", 
                ventaGuardada.getIdVenta(), ventaGuardada.getTotal());
        
        return ventaGuardada;
    }
    
    /**
     * Valida que todos los productos existan y tengan stock suficiente.
     * 
     * @param detalles Lista de detalles de venta
     * @throws ResourceNotFoundException si un producto no existe
     * @throws StockInsuficienteException si no hay stock suficiente
     */
    private void validarProductosYStock(List<DetalleVentaDTO> detalles) {
        for (DetalleVentaDTO detalleDTO : detalles) {
            Producto producto = productoRepository.findById(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detalleDTO.getIdProducto()));
            
            if (producto.getStock() < detalleDTO.getCantidad()) {
                String mensaje = String.format(
                        "Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                        producto.getNombre(), producto.getStock(), detalleDTO.getCantidad());
                throw new StockInsuficienteException(mensaje);
            }
        }
    }
    
    /**
     * Obtiene el usuario actual desde el SecurityContext.
     * 
     * @return Usuario autenticado
     * @throws ResourceNotFoundException si no se encuentra el usuario
     */
    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado: " + username));
    }
    
    /**
     * Lista todas las ventas.
     * 
     * @return Lista de ventas
     */
    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        log.debug("Listando todas las ventas");
        return ventaRepository.findAll();
    }
    
    /**
     * Obtiene una venta por ID.
     * 
     * @param id ID de la venta
     * @return Venta encontrada
     * @throws ResourceNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public Venta obtenerVentaPorId(Long id) {
        log.debug("Buscando venta ID: {}", id);
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));
    }
    
    /**
     * Lista ventas en un rango de fechas.
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de ventas en el rango
     */
    @Transactional(readOnly = true)
    public List<Venta> listarVentasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Listando ventas entre {} y {}", inicio, fin);
        return ventaRepository.findByFechaBetween(inicio, fin);
    }
}
