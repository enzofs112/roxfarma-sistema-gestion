package com.roxfarma.repository;

import com.roxfarma.model.DetallePedido;
import com.roxfarma.model.Pedido;
import com.roxfarma.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio (DAO) para la entidad DetallePedido.
 * 
 * Los detalles de pedido contienen los productos y cantidades de cada pedido.
 * Normalmente se accede a través de la relación con Pedido, pero este
 * repositorio permite consultas específicas si es necesario.
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    
    /**
     * Busca todos los detalles de un pedido específico.
     * 
     * @param pedido Pedido a buscar
     * @return Lista de detalles del pedido
     */
    List<DetallePedido> findByPedido(Pedido pedido);
    
    /**
     * Busca detalles que contienen un producto específico.
     * Útil para ver en qué pedidos se ha solicitado un producto.
     * 
     * @param producto Producto a buscar
     * @return Lista de detalles que contienen el producto
     */
    List<DetallePedido> findByProducto(Producto producto);
}
