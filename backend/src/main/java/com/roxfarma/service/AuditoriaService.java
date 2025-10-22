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
 * Servicio para gestión de auditoría
 * Para mantener trazabilidad de cambios
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaService {
    
    private final AuditoriaRepository auditoriaRepository;

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
        }
    }

    @Transactional(readOnly = true)
    public List<Auditoria> consultarAuditoria(String entidad, String usuario, 
                                              LocalDateTime inicio, LocalDateTime fin) {
        log.debug("Consultando auditoría: entidad={}, usuario={}, desde={}, hasta={}", 
                entidad, usuario, inicio, fin);
        
        return auditoriaRepository.buscarConFiltros(entidad, usuario, inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<Auditoria> obtenerUltimosRegistros() {
        log.debug("Obteniendo últimos registros de auditoría");
        return auditoriaRepository.findUltimosRegistros();
    }
}
