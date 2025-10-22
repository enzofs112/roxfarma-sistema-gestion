package com.roxfarma.service;

import com.roxfarma.model.Auditoria;
import com.roxfarma.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de auditoría.
 * 
 * Responsabilidades:
 * - Registrar operaciones del sistema
 * - Consultar historial de auditoría
 * - Mantener trazabilidad de cambios
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaService {
    
    private final AuditoriaRepository auditoriaRepository;
    
    /**
     * Registra una operación en la auditoría.
     * 
     * @param operacion Tipo de operación (CREATE, UPDATE, DELETE, etc.)
     * @param entidad Nombre de la entidad afectada
     * @param idEntidad ID del registro afectado
     * @param usuario Usuario que realizó la operación
     * @param detalles Detalles adicionales de la operación
     */
    @Transactional
    public void registrarOperacion(String operacion, String entidad, Long idEntidad, 
                                   String usuario, String detalles) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setOperacion(operacion);
            auditoria.setEntidad(entidad);
            auditoria.setIdEntidad(idEntidad);
            auditoria.setUsuario(usuario);
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles(detalles);
            
            auditoriaRepository.save(auditoria);
            
            log.debug("Operación registrada en auditoría: {} {} por {}", 
                    operacion, entidad, usuario);
        } catch (Exception e) {
            log.error("Error al registrar operación en auditoría: {}", e.getMessage());
            // No lanzar excepción para no afectar la operación principal
        }
    }
    
    /**
     * Consulta auditoría con filtros.
     * 
     * @param entidad Nombre de la entidad (opcional)
     * @param usuario Nombre del usuario (opcional)
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de registros de auditoría
     */
    @Transactional(readOnly = true)
    public List<Auditoria> consultarAuditoria(String entidad, String usuario, 
                                              LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Consultando auditoría: entidad={}, usuario={}, desde={}, hasta={}", 
                entidad, usuario, inicio, fin);
        
        return auditoriaRepository.buscarConFiltros(entidad, usuario, inicio, fin);
    }
    
    /**
     * Obtiene los últimos registros de auditoría.
     * 
     * @return Lista de últimos registros
     */
    @Transactional(readOnly = true)
    public List<Auditoria> obtenerUltimosRegistros() {
        log.debug("Obteniendo últimos registros de auditoría");
        return auditoriaRepository.findUltimosRegistros();
    }
}
