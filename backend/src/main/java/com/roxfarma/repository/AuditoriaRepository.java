package com.roxfarma.repository;

import com.roxfarma.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio (DAO) para la entidad Auditoria.
 * La auditoría registra todas las operaciones importantes del sistema
 * para mantener trazabilidad y cumplir con requerimientos de seguridad.
 * @author grupo2
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    
    List<Auditoria> findByOperacion(String operacion);

    List<Auditoria> findByEntidad(String entidad);

    List<Auditoria> findByUsuario(String usuario);

    List<Auditoria> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Auditoria> findByEntidadAndIdEntidad(String entidad, Long idEntidad);

    @Query("SELECT a FROM Auditoria a WHERE " +
           "(:entidad IS NULL OR a.entidad = :entidad) AND " +
           "(:usuario IS NULL OR a.usuario = :usuario) AND " +
           "a.fecha BETWEEN :inicio AND :fin " +
           "ORDER BY a.fecha DESC")
    List<Auditoria> buscarConFiltros(@Param("entidad") String entidad,
                                     @Param("usuario") String usuario,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin);

    @Query("SELECT a FROM Auditoria a ORDER BY a.fecha DESC")
    List<Auditoria> findUltimosRegistros();
}
