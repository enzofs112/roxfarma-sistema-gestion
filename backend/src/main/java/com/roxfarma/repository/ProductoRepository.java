package com.roxfarma.repository;

import com.roxfarma.model.Categoria;
import com.roxfarma.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio (DAO) para la entidad Producto.
 * 
 * Además de los métodos CRUD heredados de JpaRepository, este repositorio
 * define métodos personalizados para consultas específicas del negocio:
 * - Buscar productos con stock bajo (para alertas)
 * - Buscar productos próximos a vencer (para alertas)
 * - Buscar productos por categoría
 * - Buscar productos por nombre
 * 
 * Spring Data JPA genera automáticamente la implementación basándose en:
 * 1. Convenciones de nombres de métodos (Query Methods)
 * 2. Anotaciones @Query con JPQL
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    /**
     * Busca productos con stock menor al umbral especificado.
     * Usado para generar alertas de stock bajo.
     * 
     * Convención de nombres: findBy + NombreCampo + Operador
     * Spring genera automáticamente: SELECT * FROM producto WHERE stock < ?
     * 
     * @param umbral Cantidad mínima de stock
     * @return Lista de productos con stock bajo
     */
    List<Producto> findByStockLessThan(Integer umbral);
    
    /**
     * Busca productos que vencen antes de la fecha especificada.
     * Usado para generar alertas de productos próximos a vencer.
     * 
     * Convención: findBy + NombreCampo + Before
     * SQL generado: SELECT * FROM producto WHERE fecha_vencimiento < ?
     * 
     * @param fecha Fecha límite
     * @return Lista de productos próximos a vencer
     */
    List<Producto> findByFechaVencimientoBefore(LocalDate fecha);
    
    /**
     * Busca productos por categoría.
     * Útil para filtrar productos en el frontend.
     * 
     * @param categoria Categoría a buscar
     * @return Lista de productos de la categoría
     */
    List<Producto> findByCategoria(Categoria categoria);
    
    /**
     * Busca productos por nombre (búsqueda parcial, case-insensitive).
     * Usa JPQL personalizado con LIKE para búsqueda flexible.
     * 
     * @Query: Permite escribir consultas JPQL personalizadas
     * %:nombre%: Búsqueda parcial (contiene)
     * LOWER: Case-insensitive
     * 
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de productos que coinciden
     */
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> buscarPorNombre(@Param("nombre") String nombre);
    
    /**
     * Busca productos por categoría (usando ID de categoría).
     * Alternativa usando el ID en lugar del objeto Categoria.
     * 
     * @param idCategoria ID de la categoría
     * @return Lista de productos de la categoría
     */
    @Query("SELECT p FROM Producto p WHERE p.categoria.idCategoria = :idCategoria")
    List<Producto> findByCategoriaId(@Param("idCategoria") Long idCategoria);
}
