package com.roxfarma.service;

import com.roxfarma.dto.ClienteDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Cliente;
import com.roxfarma.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para gestión de Clientes
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    @Transactional
    public Cliente crearCliente(ClienteDTO dto) {
        log.info("Creando cliente: {}", dto.getNombre());
        
        // Validar que el documento no exista
        if (clienteRepository.existsByDocumento(dto.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un cliente con el documento: " + dto.getDocumento());
        }
        
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setDocumento(dto.getDocumento());
        cliente.setDireccion(dto.getDireccion());
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        
        log.info("Cliente creado con ID: {}", clienteGuardado.getIdCliente());
        
        return clienteGuardado;
    }
    
    @Transactional
    public Cliente actualizarCliente(Long id, ClienteDTO dto) {
        log.info("Actualizando cliente ID: {}", id);
        
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        
        // Validar cambio de documento
        if (!cliente.getDocumento().equals(dto.getDocumento())) {
            if (clienteRepository.existsByDocumento(dto.getDocumento())) {
                throw new IllegalArgumentException("Ya existe un cliente con el documento: " + dto.getDocumento());
            }
            cliente.setDocumento(dto.getDocumento());
        }
        
        cliente.setNombre(dto.getNombre());
        cliente.setDireccion(dto.getDireccion());
        
        return clienteRepository.save(cliente);
    }
    
    @Transactional
    public void eliminarCliente(Long id) {
        log.info("Eliminando cliente ID: {}", id);
        
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        
        clienteRepository.delete(cliente);
        
        log.info("Cliente eliminado: {}", cliente.getNombre());
    }
    
    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        log.debug("Listando todos los clientes");
        return clienteRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Cliente obtenerClientePorId(Long id) {
        log.debug("Buscando cliente ID: {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }
}
