package com.roxfarma.service;

import com.roxfarma.dto.UsuarioDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Rol;
import com.roxfarma.model.Usuario;
import com.roxfarma.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UsuarioService
 * @author grupo2
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Juan Pérez");
        usuario.setUsuario("juan");
        usuario.setContrasena("$2a$12$hashedPassword");
        usuario.setRol(Rol.TRABAJADOR);
        usuario.setActivo(true);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombre("Juan Pérez");
        usuarioDTO.setUsuario("juan");
        usuarioDTO.setContrasena("password123");
        usuarioDTO.setRol(Rol.TRABAJADOR);
    }

    @Test
    void debeCrearUsuarioConContrasenaCifrada() {
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.crearUsuario(usuarioDTO);

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan", resultado.getUsuario());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void debeListarTodosLosUsuarios() {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void debeObtenerUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals("Juan Pérez", resultado.getNombre());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void debeLanzarExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.obtenerUsuarioPorId(999L);
        });
        
        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    void debeBuscarUsuarioPorNombreUsuario() {
        when(usuarioRepository.findByUsuario("juan")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorNombreUsuario("juan");

        assertTrue(resultado.isPresent());
        assertEquals("juan", resultado.get().getUsuario());
        verify(usuarioRepository, times(1)).findByUsuario("juan");
    }

    @Test
    void debeEliminarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).delete(usuario);
    }
}
