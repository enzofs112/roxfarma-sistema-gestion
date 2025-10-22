package com.roxfarma.repository;

import com.roxfarma.model.DetalleVenta;
import com.roxfarma.model.Producto;
import com.roxfarma.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio (DAO) para la entidad DetalleVenta.
 * 
 * Los detalles de venta contienen los productos, cantidades y precios de cada venta.
 * Este repositorio permite consultas para reportes y análisis de productos vendidos.
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    
    /**
     * Busca todos los detalles de una venta específica.
     * 
     * @param venta Venta a buscar
     * @return Lista de detalles de la venta
     */
    List<DetalleVenta> findByVenta(Venta venta);
    
    /**
     * Busca detalles que contienen un producto específico.
     * Útil para ver el historial de ventas de un producto.
     * 
     * @param producto Producto a buscar
     * @return Lista de detalles que contienen el producto
     */
    List<DetalleVenta> findByProducto(Producto producto);
    
    /**
     * Busca los productos más vendidos en un período.
     * Agrupa por producto y suma las cantidades vendidas.
     * Útil para reportes de productos más vendidos.
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de productos con sus cantidades vendidas
     */
    @Query("SELECT dv.producto, SUM(dv.cantidad) as totalVendido " +
           "FROM DetalleVenta dv " +
           "WHERE dv.venta.fecha BETWEEN :inicio AND :fin " +
           "GROUP BY dv.producto " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos(@Param("inicio") LocalDateTime inicio, 
                                           @Param("fin") LocalDateTime fin);
    
    /**
     * Calcula la cantidad total vendida de un producto en un período.
     * 
     * @param producto Producto a consultar
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Cantidad total vendida
     */
    @Query("SELECT SUM(dv.cantidad) FROM DetalleVenta dv " +
           "WHERE dv.producto = :producto " +
           "AND dv.venta.fecha BETWEEN :inicio AND :fin")
    Long calcularCantidadVendidaPorProducto(@Param("producto") Producto producto,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);
}
