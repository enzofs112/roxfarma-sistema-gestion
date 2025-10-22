package com.roxfarma.service;

import com.roxfarma.dto.ProveedorDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Proveedor;
import com.roxfarma.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para gestión de Proveedores
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProveedorService {
    
    private final ProveedorRepository proveedorRepository;
    
    @Transactional
    public Proveedor crearProveedor(ProveedorDTO dto) {
        log.info("Creando proveedor: {}", dto.getNombre());
        
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setContacto(dto.getContacto());
        proveedor.setDireccion(dto.getDireccion());
        
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        
        log.info("Proveedor creado con ID: {}", proveedorGuardado.getIdProveedor());
        
        return proveedorGuardado;
    }
    
    @Transactional
    public Proveedor actualizarProveedor(Long id, ProveedorDTO dto) {
        log.info("Actualizando proveedor ID: {}", id);
        
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        
        proveedor.setNombre(dto.getNombre());
        proveedor.setContacto(dto.getContacto());
        proveedor.setDireccion(dto.getDireccion());
        
        return proveedorRepository.save(proveedor);
    }
    
    @Transactional
    public void eliminarProveedor(Long id) {
        log.info("Eliminando proveedor ID: {}", id);
        
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        
        proveedorRepository.delete(proveedor);
        
        log.info("Proveedor eliminado: {}", proveedor.getNombre());
    }
    
    @Transactional(readOnly = true)
    public List<Proveedor> listarProveedores() {
        log.debug("Listando todos los proveedores");
        return proveedorRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Proveedor obtenerProveedorPorId(Long id) {
        log.debug("Buscando proveedor ID: {}", id);
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
    }
}
