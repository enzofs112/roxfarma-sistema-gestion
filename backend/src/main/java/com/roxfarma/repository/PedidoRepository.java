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
 * Repositorio (DAO) para la entidad Pedido.
 * 
 * Los pedidos son solicitudes de reabastecimiento a proveedores.
 * Este repositorio incluye métodos para filtrar pedidos por estado y proveedor.
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    /**
     * Busca pedidos por estado.
     * Útil para filtrar pedidos pendientes, enviados o recibidos.
     * 
     * @param estado Estado del pedido (PENDIENTE, ENVIADO, RECIBIDO)
     * @return Lista de pedidos con el estado especificado
     */
    List<Pedido> findByEstado(EstadoPedido estado);
    
    /**
     * Busca pedidos de un proveedor específico.
     * Útil para ver el historial de pedidos por proveedor.
     * 
     * @param proveedor Proveedor a buscar
     * @return Lista de pedidos del proveedor
     */
    List<Pedido> findByProveedor(Proveedor proveedor);
    
    /**
     * Busca pedidos por ID de proveedor.
     * Alternativa usando el ID en lugar del objeto Proveedor.
     * 
     * @param idProveedor ID del proveedor
     * @return Lista de pedidos del proveedor
     */
    @Query("SELECT p FROM Pedido p WHERE p.proveedor.idProveedor = :idProveedor")
    List<Pedido> findByProveedorId(@Param("idProveedor") Long idProveedor);
    
    /**
     * Busca pedidos en un rango de fechas.
     * Útil para reportes y análisis.
     * 
     * Convención: findBy + Campo + Between
     * SQL: SELECT * FROM pedido WHERE fecha BETWEEN ? AND ?
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de pedidos en el rango
     */
    List<Pedido> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    /**
     * Busca pedidos por estado y proveedor.
     * Útil para filtros combinados.
     * 
     * @param estado Estado del pedido
     * @param proveedor Proveedor
     * @return Lista de pedidos que cumplen ambos criterios
     */
    List<Pedido> findByEstadoAndProveedor(EstadoPedido estado, Proveedor proveedor);
    
    /**
     * Cuenta pedidos por estado.
     * Útil para estadísticas del dashboard.
     * 
     * @param estado Estado a contar
     * @return Cantidad de pedidos con ese estado
     */
    long countByEstado(EstadoPedido estado);
}
