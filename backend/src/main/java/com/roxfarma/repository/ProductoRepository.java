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
 * Repositorio (DAO) para la entidad Producto
 * Además de los métodos CRUD heredados de JpaRepository, este repositorio
 * define métodos personalizados para consultas específicas del negocio:
 * búsqueda de productos: stock bajo, vencimiento, categ; nombre
 * Spring Data JPA genera automáticamente la implementación basándose en query methods
 * @author grupo2
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByStockLessThan(Integer umbral);

    List<Producto> findByFechaVencimientoBefore(LocalDate fecha);

    List<Producto> findByCategoria(Categoria categoria);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT p FROM Producto p WHERE p.categoria.idCategoria = :idCategoria")
    List<Producto> findByCategoriaId(@Param("idCategoria") Long idCategoria);
}
