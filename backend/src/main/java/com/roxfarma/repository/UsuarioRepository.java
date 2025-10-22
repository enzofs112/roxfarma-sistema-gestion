package com.roxfarma.repository;

import com.roxfarma.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio (DAO) para la entidad Usuario.
 * 
 * Los usuarios son personas que acceden al sistema (Administradores o Trabajadores).
 * Este repositorio incluye métodos para autenticación y gestión de usuarios.
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su nombre de usuario (login).
     * Usado en el proceso de autenticación.
     * 
     * El nombre de usuario es único en el sistema.
     * 
     * @param usuario Nombre de usuario para login
     * @return Optional con el usuario si existe, vacío si no
     */
    Optional<Usuario> findByUsuario(String usuario);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     * Útil para validar duplicados al crear o actualizar usuarios.
     * 
     * @param usuario Nombre de usuario a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsuario(String usuario);
    
    /**
     * Busca un usuario por nombre de usuario y que esté activo.
     * Usado en autenticación para verificar que el usuario puede acceder.
     * 
     * Convención: findBy + Campo1 + And + Campo2
     * SQL: SELECT * FROM usuario WHERE usuario = ? AND activo = ?
     * 
     * @param usuario Nombre de usuario
     * @param activo Estado del usuario (true = activo)
     * @return Optional con el usuario si existe y está activo
     */
    Optional<Usuario> findByUsuarioAndActivo(String usuario, Boolean activo);
}
