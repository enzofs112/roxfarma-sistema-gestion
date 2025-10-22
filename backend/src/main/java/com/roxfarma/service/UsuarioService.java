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
 * Servicio de lógica de negocio para gestión de Usuarios.
 * 
 * Responsabilidades:
 * - CRUD de usuarios
 * - Cifrado de contraseñas con BCrypt
 * - Validación de datos
 * - Logging de operaciones
 * 
 * Aplicación de SOLID:
 * - SRP: Solo gestiona lógica de negocio de usuarios
 * - OCP: Extensible mediante herencia o interfaces
 * - DIP: Depende de abstracciones (UsuarioRepository, PasswordEncoder)
 * 
 * Inyección de Dependencias:
 * - Las dependencias se inyectan mediante constructor
 * - @RequiredArgsConstructor de Lombok genera el constructor automáticamente
 * - Spring gestiona el ciclo de vida de los beans
 * 
 * Ventajas de DI en este servicio:
 * - Fácil de probar con mocks
 * - Desacoplado de implementaciones concretas
 * - Mantenible y escalable
 * 
 * @author Sistema RoxFarma
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {
    
    // Inyección de dependencias mediante constructor (generado por @RequiredArgsConstructor)
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Crea un nuevo usuario en el sistema.
     * 
     * Proceso:
     * 1. Validar que el nombre de usuario no exista
     * 2. Cifrar la contraseña con BCrypt
     * 3. Crear entidad Usuario
     * 4. Guardar en base de datos
     * 5. Registrar en log
     * 
     * @param dto Datos del usuario a crear
     * @return Usuario creado
     * @throws IllegalArgumentException si el usuario ya existe
     */
    @Transactional
    public Usuario crearUsuario(UsuarioDTO dto) {
        log.info("Creando usuario: {}", dto.getUsuario());
        
        // Validar que el usuario no exista
        if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
            log.warn("Intento de crear usuario duplicado: {}", dto.getUsuario());
            throw new IllegalArgumentException("El usuario '" + dto.getUsuario() + "' ya existe");
        }
        
        // Crear entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setUsuario(dto.getUsuario());
        
        // Cifrar contraseña con BCrypt antes de guardar
        String contrasenaCifrada = passwordEncoder.encode(dto.getContrasena());
        usuario.setContrasena(contrasenaCifrada);
        
        usuario.setRol(dto.getRol());
        usuario.setActivo(true);
        
        // Guardar en base de datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        log.info("Usuario creado exitosamente: {} con ID: {}", 
                usuarioGuardado.getUsuario(), usuarioGuardado.getIdUsuario());
        
        return usuarioGuardado;
    }
    
    /**
     * Actualiza un usuario existente.
     * 
     * Proceso:
     * 1. Buscar usuario por ID
     * 2. Validar que el nuevo nombre de usuario no esté en uso (si cambió)
     * 3. Actualizar campos
     * 4. Si cambió la contraseña, cifrarla nuevamente
     * 5. Guardar cambios
     * 
     * @param id ID del usuario a actualizar
     * @param dto Nuevos datos del usuario
     * @return Usuario actualizado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, UsuarioDTO dto) {
        log.info("Actualizando usuario ID: {}", id);
        
        // Buscar usuario existente
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        // Validar cambio de nombre de usuario
        if (!usuario.getUsuario().equals(dto.getUsuario())) {
            if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
                throw new IllegalArgumentException("El usuario '" + dto.getUsuario() + "' ya existe");
            }
            usuario.setUsuario(dto.getUsuario());
        }
        
        // Actualizar campos
        usuario.setNombre(dto.getNombre());
        usuario.setRol(dto.getRol());
        
        // Si se proporciona nueva contraseña, cifrarla
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            String contrasenaCifrada = passwordEncoder.encode(dto.getContrasena());
            usuario.setContrasena(contrasenaCifrada);
            log.debug("Contraseña actualizada para usuario: {}", usuario.getUsuario());
        }
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        log.info("Usuario actualizado exitosamente: {}", usuarioActualizado.getUsuario());
        
        return usuarioActualizado;
    }
    
    /**
     * Elimina un usuario del sistema.
     * 
     * Nota: En un sistema real, se recomienda desactivar en lugar de eliminar
     * para mantener integridad referencial con ventas registradas.
     * 
     * @param id ID del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        log.info("Eliminando usuario ID: {}", id);
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        // Alternativa: Desactivar en lugar de eliminar
        // usuario.setActivo(false);
        // usuarioRepository.save(usuario);
        
        usuarioRepository.delete(usuario);
        
        log.info("Usuario eliminado: {}", usuario.getUsuario());
    }
    
    /**
     * Lista todos los usuarios del sistema.
     * 
     * @return Lista de usuarios
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        log.debug("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }
    
    /**
     * Busca un usuario por ID.
     * 
     * @param id ID del usuario
     * @return Usuario encontrado
     * @throws ResourceNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {
        log.debug("Buscando usuario ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }
    
    /**
     * Busca un usuario por nombre de usuario.
     * 
     * @param usuario Nombre de usuario
     * @return Optional con el usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNombreUsuario(String usuario) {
        log.debug("Buscando usuario: {}", usuario);
        return usuarioRepository.findByUsuario(usuario);
    }
}
