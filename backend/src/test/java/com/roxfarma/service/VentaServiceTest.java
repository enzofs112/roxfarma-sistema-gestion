package com.roxfarma.service;

import com.roxfarma.dto.DetalleVentaDTO;
import com.roxfarma.dto.VentaDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.exception.StockInsuficienteException;
import com.roxfarma.model.*;
import com.roxfarma.repository.ClienteRepository;
import com.roxfarma.repository.ProductoRepository;
import com.roxfarma.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para VentaService.
 * Valida la lógica de negocio del módulo de ventas.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioService inventarioService;

    @Mock
    private com.roxfarma.repository.UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VentaService ventaService;

    private Cliente cliente;
    private Producto producto;
    private Usuario usuario;
    private VentaDTO ventaDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setDocumento("12345678");

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Analgésicos");

        producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Paracetamol 500mg");
        producto.setPrecio(new BigDecimal("5.50"));
        producto.setStock(100);
        producto.setFechaVencimiento(LocalDate.now().plusMonths(12));
        producto.setCategoria(categoria);

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Admin");
        usuario.setUsuario("admin");
        usuario.setRol(Rol.ADMINISTRADOR);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.setContext(securityContext);
        when(usuarioRepository.findByUsuario("admin")).thenReturn(Optional.of(usuario));

        // Configurar DTO de venta
        DetalleVentaDTO detalleDTO = new DetalleVentaDTO();
        detalleDTO.setIdProducto(1L);
        detalleDTO.setCantidad(10);

        ventaDTO = new VentaDTO();
        ventaDTO.setIdCliente(1L);
        ventaDTO.setDetalles(Arrays.asList(detalleDTO));
    }

    @Test
    void deberiaRegistrarVentaCorrectamente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(inventarioService).disminuirStock(anyLong(), anyInt(), anyString());

        Venta ventaGuardada = new Venta();
        ventaGuardada.setIdVenta(1L);
        ventaGuardada.setCliente(cliente);
        ventaGuardada.setTotal(new BigDecimal("64.90")); // (5.50 * 10) * 1.18 IGV

        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaGuardada);

        // Act
        Venta resultado = ventaService.registrarVenta(ventaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        assertTrue(resultado.getTotal().compareTo(BigDecimal.ZERO) > 0);
        verify(inventarioService, times(1)).disminuirStock(anyLong(), anyInt(), anyString());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoClienteNoExiste() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            ventaService.registrarVenta(ventaDTO);
        });
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            ventaService.registrarVenta(ventaDTO);
        });
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void deberiaCalcularTotalConIGVCorrectamente() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        doNothing().when(inventarioService).disminuirStock(anyLong(), anyInt(), anyString());

        Venta ventaGuardada = new Venta();
        ventaGuardada.setIdVenta(1L);
        ventaGuardada.setTotal(new BigDecimal("64.90")); // Subtotal: 55.00, IGV 18%: 9.90, Total: 64.90

        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaGuardada);

        // Act
        Venta resultado = ventaService.registrarVenta(ventaDTO);

        // Assert
        assertNotNull(resultado);
        // El total debe incluir IGV (18%)
        BigDecimal subtotal = new BigDecimal("5.50").multiply(new BigDecimal("10")); // 55.00
        BigDecimal totalEsperado = subtotal.multiply(new BigDecimal("1.18")); // 64.90
        assertEquals(0, totalEsperado.compareTo(resultado.getTotal()));
    }

    @Test
    void deberiaObtenerVentaPorId() {
        // Arrange
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        venta.setCliente(cliente);
        venta.setTotal(new BigDecimal("64.90"));

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        // Act
        Venta resultado = ventaService.obtenerVentaPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        assertEquals("Juan Pérez", resultado.getCliente().getNombre());
    }
}
