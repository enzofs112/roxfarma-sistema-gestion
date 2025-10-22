package com.roxfarma.service;

import com.roxfarma.dto.ProductoDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Categoria;
import com.roxfarma.model.Producto;
import com.roxfarma.repository.CategoriaRepository;
import com.roxfarma.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de lógica de negocio para gestión de Productos.
 * 
 * Responsabilidades:
 * - CRUD de productos
 * - Validación de datos
 * - Generación de alertas de stock bajo
 * - Generación de alertas de vencimiento
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    
    /**
     * Crea un nuevo producto.
     */
    @Transactional
    public Producto crearProducto(ProductoDTO dto) {
        log.info("Creando producto: {}", dto.getNombre());
        
        // Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + dto.getIdCategoria()));
        
        // Crear producto
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        producto.setCategoria(categoria);
        
        Producto productoGuardado = productoRepository.save(producto);
        
        log.info("Producto creado con ID: {}", productoGuardado.getIdProducto());
        
        return productoGuardado;
    }
    
    /**
     * Actualiza un producto existente.
     */
    @Transactional
    public Producto actualizarProducto(Long id, ProductoDTO dto) {
        log.info("Actualizando producto ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        
        // Validar categoría si cambió
        if (!producto.getCategoria().getIdCategoria().equals(dto.getIdCategoria())) {
            Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Categoría no encontrada con ID: " + dto.getIdCategoria()));
            producto.setCategoria(categoria);
        }
        
        // Actualizar campos
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setFechaVencimiento(dto.getFechaVencimiento());
        
        return productoRepository.save(producto);
    }
    
    /**
     * Elimina un producto.
     */
    @Transactional
    public void eliminarProducto(Long id) {
        log.info("Eliminando producto ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        
        productoRepository.delete(producto);
        
        log.info("Producto eliminado: {}", producto.getNombre());
    }
    
    /**
     * Lista todos los productos.
     */
    @Transactional(readOnly = true)
    public List<Producto> listarTodosLosProductos() {
        log.debug("Listando todos los productos");
        return productoRepository.findAll();
    }
    
    /**
     * Obtiene un producto por ID.
     */
    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Long id) {
        log.debug("Buscando producto ID: {}", id);
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }
    
    /**
     * Obtiene productos con stock por debajo del umbral.
     * Usado para generar alertas de stock bajo.
     * 
     * @param umbral Cantidad mínima de stock (por defecto 10)
     * @return Lista de productos con stock bajo
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosConStockBajo(Integer umbral) {
        log.info("Buscando productos con stock menor a: {}", umbral);
        List<Producto> productos = productoRepository.findByStockLessThan(umbral);
        log.info("Se encontraron {} productos con stock bajo", productos.size());
        return productos;
    }
    
    /**
     * Obtiene productos próximos a vencer.
     * Usado para generar alertas de vencimiento.
     * 
     * @param dias Días de anticipación (por defecto 30)
     * @return Lista de productos próximos a vencer
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosProximosAVencer(Integer dias) {
        log.info("Buscando productos que vencen en los próximos {} días", dias);
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        List<Producto> productos = productoRepository.findByFechaVencimientoBefore(fechaLimite);
        log.info("Se encontraron {} productos próximos a vencer", productos.size());
        return productos;
    }
}
