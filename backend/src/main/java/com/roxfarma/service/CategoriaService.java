package com.roxfarma.service;

import com.roxfarma.dto.CategoriaDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Categoria;
import com.roxfarma.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para gestión de Categorías
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    @Transactional
    public Categoria crearCategoria(CategoriaDTO dto) {
        log.info("Creando categoría: {}", dto.getNombre());
        
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        
        log.info("Categoría creada con ID: {}", categoriaGuardada.getIdCategoria());
        
        return categoriaGuardada;
    }
    
    @Transactional
    public Categoria actualizarCategoria(Long id, CategoriaDTO dto) {
        log.info("Actualizando categoría ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        
        return categoriaRepository.save(categoria);
    }
    
    @Transactional
    public void eliminarCategoria(Long id) {
        log.info("Eliminando categoría ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        
        categoriaRepository.delete(categoria);
        
        log.info("Categoría eliminada: {}", categoria.getNombre());
    }
    
    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        log.debug("Listando todas las categorías");
        return categoriaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Categoria obtenerCategoriaPorId(Long id) {
        log.debug("Buscando categoría ID: {}", id);
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }
}
