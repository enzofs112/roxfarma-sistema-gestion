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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoService.
 * Valida la lógica de negocio del módulo de productos.
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
        // Configurar datos de prueba
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Analgésicos");

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Paracetamol 500mg");
        producto.setDescripcion("Analgésico y antipirético");
        producto.setPrecio(5.50);
        producto.setStock(100);
        producto.setFechaVencimiento(LocalDate.now().plusMonths(12));
        producto.setCategoria(categoria);

        productoDTO = new ProductoDTO();
        productoDTO.setNombre("Paracetamol 500mg");
        productoDTO.setDescripcion("Analgésico y antipirético");
        productoDTO.setPrecio(5.50);
        productoDTO.setStock(100);
        productoDTO.setFechaVencimiento(LocalDate.now().plusMonths(12).toString());
        productoDTO.setIdCategoria(1L);
    }

    @Test
    void deberiaCrearProductoCorrectamente() {
        // Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // Act
        Producto resultado = productoService.crear(productoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Paracetamol 500mg", resultado.getNombre());
        assertEquals(5.50, resultado.getPrecio());
        assertEquals(100, resultado.getStock());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoCategoriaNoExiste() {
        // Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.crear(productoDTO);
        });
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void deberiaObtenerProductoPorId() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        Producto resultado = productoService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Paracetamol 500mg", resultado.getNombre());
    }

    @Test
    void deberiaObtenerAlertasDeStockBajo() {
        // Arrange
        Producto productoStockBajo = new Producto();
        productoStockBajo.setNombre("Ibuprofeno");
        productoStockBajo.setStock(5);
        productoStockBajo.setCategoria(categoria);

        when(productoRepository.findByStockLessThan(10))
                .thenReturn(Arrays.asList(productoStockBajo));

        // Act
        List<Producto> alertas = productoService.obtenerAlertasStockBajo();

        // Assert
        assertNotNull(alertas);
        assertEquals(1, alertas.size());
        assertTrue(alertas.get(0).getStock() < 10);
    }

    @Test
    void deberiaActualizarProductoCorrectamente() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        productoDTO.setNombre("Paracetamol 750mg");
        productoDTO.setPrecio(7.50);

        // Act
        Producto resultado = productoService.actualizar(1L, productoDTO);

        // Assert
        assertNotNull(resultado);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void deberiaEliminarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).delete(producto);

        // Act
        productoService.eliminar(1L);

        // Assert
        verify(productoRepository, times(1)).delete(producto);
    }
}
