package com.roxfarma.repository;

import com.roxfarma.model.Categoria;
import com.roxfarma.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


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
