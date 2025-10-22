package com.roxfarma.repository;

import com.roxfarma.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio (DAO) para la entidad Proveedor.
 * 
 * Proporciona operaciones CRUD para gestionar proveedores.
 * Los proveedores son laboratorios o distribuidoras que suministran productos.
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
    // Métodos CRUD heredados de JpaRepository:
    // - save(Proveedor): Crear o actualizar proveedor
    // - findById(Long): Buscar proveedor por ID
    // - findAll(): Listar todos los proveedores
    // - deleteById(Long): Eliminar proveedor
    // - count(): Contar proveedores
}
