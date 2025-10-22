package com.roxfarma.repository;

import com.roxfarma.model.EstadoPedido;
import com.roxfarma.model.Pedido;
import com.roxfarma.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio (DAO) para la entidad Pedido
 * @author grupo2
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByProveedor(Proveedor proveedor);

    @Query("SELECT p FROM Pedido p WHERE p.proveedor.idProveedor = :idProveedor")
    List<Pedido> findByProveedorId(@Param("idProveedor") Long idProveedor);

    List<Pedido> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Pedido> findByEstadoAndProveedor(EstadoPedido estado, Proveedor proveedor);

    long countByEstado(EstadoPedido estado);
}
