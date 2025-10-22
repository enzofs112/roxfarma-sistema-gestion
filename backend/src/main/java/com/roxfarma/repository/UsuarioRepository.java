package com.roxfarma.repository;

import com.roxfarma.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio (DAO) para la entidad Usuario
 * auth y gestión de usuarios
 * @author grupo2
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuario(String usuario);

    Optional<Usuario> findByUsuarioAndActivo(String usuario, Boolean activo);
}
