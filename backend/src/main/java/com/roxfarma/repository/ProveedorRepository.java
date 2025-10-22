package com.roxfarma.repository;

import com.roxfarma.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio (DAO) para la entidad Proveedor
 * @author grupo2
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
