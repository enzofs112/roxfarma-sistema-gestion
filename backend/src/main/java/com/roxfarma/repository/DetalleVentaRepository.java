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
 * Repositorio (DAO) para la entidad DetalleVenta
 * @author grupo2
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVenta(Venta venta);

    List<DetalleVenta> findByProducto(Producto producto);

    @Query("SELECT dv.producto, SUM(dv.cantidad) as totalVendido " +
           "FROM DetalleVenta dv " +
           "WHERE dv.venta.fecha BETWEEN :inicio AND :fin " +
           "GROUP BY dv.producto " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos(@Param("inicio") LocalDateTime inicio, 
                                           @Param("fin") LocalDateTime fin);
    
    @Query("SELECT SUM(dv.cantidad) FROM DetalleVenta dv " +
           "WHERE dv.producto = :producto " +
           "AND dv.venta.fecha BETWEEN :inicio AND :fin")
    Long calcularCantidadVendidaPorProducto(@Param("producto") Producto producto,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);
}
