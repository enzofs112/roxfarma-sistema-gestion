package com.roxfarma.service;

import com.roxfarma.dto.ProductoDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Categoria;
import com.roxfarma.model.Producto;
import com.roxfarma.repository.CategoriaRepository;
import com.roxfarma.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProductoService
 * Usa JUnit 5 y Mockito para probar la lógica de negocio
 * @author grupo2
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    private Categoria categoria;
    private Producto producto;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Analgésicos");

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Paracetamol");
        producto.setDescripcion("500mg");
        producto.setPrecio(new BigDecimal("5.50"));
        producto.setStock(100);
        producto.setFechaVencimiento(LocalDate.now().plusMonths(6));
        producto.setCategoria(categoria);

        productoDTO = new ProductoDTO();
        productoDTO.setNombre("Paracetamol");
        productoDTO.setDescripcion("500mg");
        productoDTO.setPrecio(new BigDecimal("5.50"));
        productoDTO.setStock(100);
        productoDTO.setFechaVencimiento(LocalDate.now().plusMonths(6));
        productoDTO.setIdCategoria(1L);
    }

    @Test
    void debeCrearProductoCorrectamente() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.crearProducto(productoDTO);

        assertNotNull(resultado);
        assertEquals("Paracetamol", resultado.getNombre());
        assertEquals(new BigDecimal("5.50"), resultado.getPrecio());
        verify(categoriaRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void debeLanzarExcepcionCuandoCategoriaNoExiste() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.crearProducto(productoDTO);
        });
        
        verify(categoriaRepository, times(1)).findById(1L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void debeListarTodosLosProductos() {
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> resultado = productoService.listarTodosLosProductos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Paracetamol", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void debeObtenerProductosConStockBajo() {
        producto.setStock(5);
        List<Producto> productos = Arrays.asList(producto);
        when(productoRepository.findByStockLessThan(10)).thenReturn(productos);

        List<Producto> resultado = productoService.obtenerProductosConStockBajo(10);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getStock() < 10);
        verify(productoRepository, times(1)).findByStockLessThan(10);
    }

    @Test
    void debeObtenerProductoPorId() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerProductoPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Paracetamol", resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void debeLanzarExcepcionCuandoProductoNoExiste() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.obtenerProductoPorId(999L);
        });
        
        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    void debeActualizarProductoCorrectamente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoDTO.setPrecio(new BigDecimal("6.00"));
        productoDTO.setIdCategoria(1L); // Misma categoría, no debería buscarla

        Producto resultado = productoService.actualizarProducto(1L, productoDTO);

        assertNotNull(resultado);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
}
