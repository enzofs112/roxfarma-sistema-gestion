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
 * Servicio para la gestión de ventas.
 * Funciones:
 * - Registrar ventas con detalle
 * - Calcular subtotal, IGV (18%) y total
 * - Actualizar stock automáticamente
 * Flujo:
 * 1. Validar cliente y productos
 * 2. Verificar stock disponible
 * 3. Registrar venta y detalles
 * 4. Descontar stock usando InventarioService
 * @author grupo2
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
     * Registro venta
     * Este método es transaccional: si algo falla, se revierte todo.
     */
    @Transactional
    public Venta registrarVenta(VentaDTO dto) {
        log.info("Iniciando registro de venta para cliente ID: {}", dto.getIdCliente());
        
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con ID: " + dto.getIdCliente()));
        
        Usuario usuario = obtenerUsuarioActual();
        
        validarProductosYStock(dto.getDetalles());
        
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        
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
            
            BigDecimal precioDetalle = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));
            subtotal = subtotal.add(precioDetalle);
        }
        
        venta.setDetalles(detalles);
        
        BigDecimal igv = subtotal.multiply(IGV_RATE);
        BigDecimal total = subtotal.add(igv);
        venta.setTotal(total);
        
        Venta ventaGuardada = ventaRepository.save(venta);
        
        log.info("Venta guardada con ID: {}", ventaGuardada.getIdVenta());
        
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
     * Se obtiene el usuario actual desde el SecurityContext
     */
    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado: " + username));
    }
    
    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        log.debug("Listando todas las ventas");
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta obtenerVentaPorId(Long id) {
        log.debug("Buscando venta ID: {}", id);
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentasPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Listando ventas entre {} y {}", inicio, fin);
        return ventaRepository.findByFechaBetween(inicio, fin);
    }
}
