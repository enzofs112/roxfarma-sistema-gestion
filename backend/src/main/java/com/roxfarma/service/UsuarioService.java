package com.roxfarma.service;

import com.roxfarma.dto.UsuarioDTO;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.Usuario;
import com.roxfarma.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de gestión de usuarios
 * Funciones:
 * - CRUD de usuarios
 * - Cifrado de contraseñas con BCrypt
 * - Validación de datos y registro en logs
 * Buenas prácticas:
 * - lógica exclusiva de usuarios
 * - depende de interfaces (repositorio, codificador de contraseñas)
 * - Inyección de dependencias por constructor (@RequiredArgsConstructor)
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    
    // Inyección de dependencias mediante constructor (generado por @RequiredArgsConstructor)
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
 * Registra un nuevo usuario.
 * Pasos:
 * 1. Verifica si el usuario ya existe
 * 2. Cifra la contraseña con BCrypt
 * 3. Crea la entidad y la guarda en la BD
 * 4. Registra la operación en logs
 * @throws IllegalArgumentException si el usuario ya está registrado
     */
    @Transactional
    public Usuario crearUsuario(UsuarioDTO dto) {
        log.info("Creando usuario: {}", dto.getUsuario());
        
        if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
            log.warn("Intento de crear usuario duplicado: {}", dto.getUsuario());
            throw new IllegalArgumentException("El usuario '" + dto.getUsuario() + "' ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setUsuario(dto.getUsuario());
        
        String contrasenaCifrada = passwordEncoder.encode(dto.getContrasena());
        usuario.setContrasena(contrasenaCifrada);
        
        usuario.setRol(dto.getRol());
        usuario.setActivo(true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        log.info("Usuario creado exitosamente: {} con ID: {}", 
                usuarioGuardado.getUsuario(), usuarioGuardado.getIdUsuario());
        
        return usuarioGuardado;
    }
    
    /**
     * Actualización de usuario existente
     * 
     * Proceso:
     * 1. Buscar usuario por ID
     * 2. Validar que el nuevo nombre de usuario no esté en uso (si cambió)
     * 3. Actualizar campos
     * 4. Si cambió la contraseña, cifrarla nuevamente
     * 5. Guardar cambios
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, UsuarioDTO dto) {
        log.info("Actualizando usuario ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        if (!usuario.getUsuario().equals(dto.getUsuario())) {
            if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
                throw new IllegalArgumentException("El usuario '" + dto.getUsuario() + "' ya existe");
            }
            usuario.setUsuario(dto.getUsuario());
        }
        
        usuario.setNombre(dto.getNombre());
        usuario.setRol(dto.getRol());
        
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            String contrasenaCifrada = passwordEncoder.encode(dto.getContrasena());
            usuario.setContrasena(contrasenaCifrada);
            log.debug("Contraseña actualizada para usuario: {}", usuario.getUsuario());
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        log.info("Usuario actualizado exitosamente: {}", usuarioActualizado.getUsuario());
        
        return usuarioActualizado;
    }
    
    @Transactional
    public void eliminarUsuario(Long id) {
        log.info("Eliminando usuario ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        usuarioRepository.delete(usuario);
        
        log.info("Usuario eliminado: {}", usuario.getUsuario());
    }
    

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        log.debug("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        log.debug("Buscando usuario ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNombreUsuario(String usuario) {
        log.debug("Buscando usuario: {}", usuario);
        return usuarioRepository.findByUsuario(usuario);
    }
}
