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
 * 
 * La auditoría registra todas las operaciones importantes del sistema
 * para mantener trazabilidad y cumplir con requerimientos de seguridad.
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    
    /**
     * Busca registros de auditoría por tipo de operación.
     * Ejemplos: "CREATE", "UPDATE", "DELETE", "LOGIN"
     * 
     * @param operacion Tipo de operación
     * @return Lista de registros de auditoría
     */
    List<Auditoria> findByOperacion(String operacion);
    
    /**
     * Busca registros de auditoría por entidad afectada.
     * Ejemplos: "Producto", "Venta", "Usuario"
     * 
     * @param entidad Nombre de la entidad
     * @return Lista de registros de auditoría
     */
    List<Auditoria> findByEntidad(String entidad);
    
    /**
     * Busca registros de auditoría por usuario.
     * Útil para ver todas las operaciones de un usuario específico.
     * 
     * @param usuario Nombre del usuario
     * @return Lista de registros de auditoría
     */
    List<Auditoria> findByUsuario(String usuario);
    
    /**
     * Busca registros de auditoría en un rango de fechas.
     * Útil para reportes de auditoría por período.
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de registros de auditoría
     */
    List<Auditoria> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    /**
     * Busca registros de auditoría por entidad y ID de entidad.
     * Útil para ver el historial completo de un registro específico.
     * 
     * @param entidad Nombre de la entidad
     * @param idEntidad ID del registro
     * @return Lista de registros de auditoría
     */
    List<Auditoria> findByEntidadAndIdEntidad(String entidad, Long idEntidad);
    
    /**
     * Busca registros de auditoría con filtros combinados.
     * Permite consultas complejas para reportes de auditoría.
     * 
     * @param entidad Nombre de la entidad (opcional)
     * @param usuario Nombre del usuario (opcional)
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a WHERE " +
           "(:entidad IS NULL OR a.entidad = :entidad) AND " +
           "(:usuario IS NULL OR a.usuario = :usuario) AND " +
           "a.fecha BETWEEN :inicio AND :fin " +
           "ORDER BY a.fecha DESC")
    List<Auditoria> buscarConFiltros(@Param("entidad") String entidad,
                                     @Param("usuario") String usuario,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin);
    
    /**
     * Busca los últimos N registros de auditoría.
     * Útil para mostrar actividad reciente en el dashboard.
     * 
     * @return Lista de los últimos registros de auditoría
     */
    @Query("SELECT a FROM Auditoria a ORDER BY a.fecha DESC")
    List<Auditoria> findUltimosRegistros();
}
