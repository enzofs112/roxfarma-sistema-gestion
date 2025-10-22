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
 * Repositorio (DAO) para la entidad Venta
 * transacciones de venta y consulta reportes
 * @author grupo2
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Venta> findByCliente(Cliente cliente);

    List<Venta> findByUsuario(Usuario usuario);

    @Query("SELECT v FROM Venta v WHERE v.cliente.idCliente = :idCliente")
    List<Venta> findByClienteId(@Param("idCliente") Long idCliente);

    @Query("SELECT v FROM Venta v WHERE v.usuario.idUsuario = :idUsuario")
    List<Venta> findByUsuarioId(@Param("idUsuario") Long idUsuario);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin")
    BigDecimal calcularTotalVentasPorPeriodo(@Param("inicio") LocalDateTime inicio, 
                                             @Param("fin") LocalDateTime fin);

    long countByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT v FROM Venta v ORDER BY v.fecha DESC")
    List<Venta> findTopVentas(@Param("limit") int limit);
}
