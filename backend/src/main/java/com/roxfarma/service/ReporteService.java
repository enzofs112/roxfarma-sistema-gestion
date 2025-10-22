package com.roxfarma.service;

import com.roxfarma.model.Producto;
import com.roxfarma.repository.ProductoRepository;
import com.roxfarma.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para generación de reportes.
 * Por periodo, de inventario y estad. del negocio
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {
    
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> generarReporteVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Generando reporte de ventas desde {} hasta {}", fechaInicio, fechaFin);
        
        Map<String, Object> reporte = new HashMap<>();
        
        BigDecimal totalVentas = ventaRepository.calcularTotalVentasPorPeriodo(fechaInicio, fechaFin);
        reporte.put("totalVentas", totalVentas != null ? totalVentas : BigDecimal.ZERO);
        
        long cantidadTransacciones = ventaRepository.countByFechaBetween(fechaInicio, fechaFin);
        reporte.put("cantidadTransacciones", cantidadTransacciones);
        
        BigDecimal promedio = BigDecimal.ZERO;
        if (cantidadTransacciones > 0 && totalVentas != null) {
            promedio = totalVentas.divide(BigDecimal.valueOf(cantidadTransacciones), 2, BigDecimal.ROUND_HALF_UP);
        }
        reporte.put("promedioVenta", promedio);
        
        log.info("Reporte de ventas generado: {} transacciones, total: {}", 
                cantidadTransacciones, totalVentas);
        
        return reporte;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generarReporteInventario() {
        log.info("Generando reporte de inventario");
        
        Map<String, Object> reporte = new HashMap<>();
        
        long totalProductos = productoRepository.count();
        reporte.put("totalProductos", totalProductos);
        
        List<Producto> stockBajo = productoRepository.findByStockLessThan(10);
        reporte.put("productosStockBajo", stockBajo.size());
        reporte.put("listaStockBajo", stockBajo);
        
        List<Producto> proximosVencer = productoRepository.findByFechaVencimientoBefore(
                java.time.LocalDate.now().plusDays(30));
        reporte.put("productosProximosVencer", proximosVencer.size());
        reporte.put("listaProximosVencer", proximosVencer);
        
        log.info("Reporte de inventario generado: {} productos, {} con stock bajo, {} próximos a vencer",
                totalProductos, stockBajo.size(), proximosVencer.size());
        
        return reporte;
    }
}
