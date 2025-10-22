package com.roxfarma.repository;

import com.roxfarma.model.Cliente;
import com.roxfarma.model.Usuario;
import com.roxfarma.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio (DAO) para la entidad Venta.
 * 
 * Las ventas son transacciones de venta de productos a clientes.
 * Este repositorio incluye métodos para consultas de reportes y análisis.
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    /**
     * Busca ventas en un rango de fechas.
     * Usado para reportes de ventas por período.
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de ventas en el rango
     */
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    /**
     * Busca ventas de un cliente específico.
     * Útil para ver el historial de compras del cliente.
     * 
     * @param cliente Cliente a buscar
     * @return Lista de ventas del cliente
     */
    List<Venta> findByCliente(Cliente cliente);
    
    /**
     * Busca ventas registradas por un usuario específico.
     * Útil para auditoría y seguimiento de desempeño.
     * 
     * @param usuario Usuario que registró las ventas
     * @return Lista de ventas del usuario
     */
    List<Venta> findByUsuario(Usuario usuario);
    
    /**
     * Busca ventas por ID de cliente.
     * Alternativa usando el ID en lugar del objeto Cliente.
     * 
     * @param idCliente ID del cliente
     * @return Lista de ventas del cliente
     */
    @Query("SELECT v FROM Venta v WHERE v.cliente.idCliente = :idCliente")
    List<Venta> findByClienteId(@Param("idCliente") Long idCliente);
    
    /**
     * Busca ventas por ID de usuario.
     * 
     * @param idUsuario ID del usuario
     * @return Lista de ventas del usuario
     */
    @Query("SELECT v FROM Venta v WHERE v.usuario.idUsuario = :idUsuario")
    List<Venta> findByUsuarioId(@Param("idUsuario") Long idUsuario);
    
    /**
     * Calcula el total de ventas en un rango de fechas.
     * Usado para reportes y estadísticas.
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Suma total de ventas en el período
     */
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin")
    BigDecimal calcularTotalVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, 
                                             @Param("fin") LocalDateTime fin);
    
    /**
     * Cuenta ventas en un rango de fechas.
     * Útil para estadísticas de cantidad de transacciones.
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Cantidad de ventas en el período
     */
    long countByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    /**
     * Busca las últimas N ventas ordenadas por fecha descendente.
     * Útil para mostrar ventas recientes en el dashboard.
     * 
     * @param limit Cantidad de ventas a retornar
     * @return Lista de las últimas ventas
     */
    @Query("SELECT v FROM Venta v ORDER BY v.fecha DESC")
    List<Venta> findTopVentas(@Param("limit") int limit);
}
