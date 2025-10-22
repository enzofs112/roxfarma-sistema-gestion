package com.roxfarma.repository;

import com.roxfarma.model.DetallePedido;
import com.roxfarma.model.Pedido;
import com.roxfarma.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio (DAO) para la entidad DetallePedido
 * Los detalles de pedido contienen los productos y cantidades de cada pedido.
 * Normalmente se accede a través de la relación con Pedido, pero este
 * repositorio permite consultas específicas si es necesario.
 * @author grupo2
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedido(Pedido pedido);
    List<DetallePedido> findByProducto(Producto producto);
}
