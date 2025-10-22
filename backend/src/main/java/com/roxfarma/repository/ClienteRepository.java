package com.roxfarma.repository;

import com.roxfarma.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio (DAO) para la entidad Cliente.
 * 
 * Los clientes son farmacias o boticas que compran productos a RoxFarma.
 * Este repositorio incluye un método personalizado para buscar por documento (RUC).
 * 
 * @author Sistema RoxFarma
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca un cliente por su número de documento (RUC).
     * El documento es único en el sistema.
     * 
     * Convención: findBy + NombreCampo
     * SQL generado: SELECT * FROM cliente WHERE documento = ?
     * 
     * @param documento Número de documento (RUC) del cliente
     * @return Optional con el cliente si existe, vacío si no
     */
    Optional<Cliente> findByDocumento(String documento);
    
    /**
     * Verifica si existe un cliente con el documento especificado.
     * Útil para validar duplicados antes de crear un cliente.
     * 
     * @param documento Número de documento a verificar
     * @return true si existe, false si no
     */
    boolean existsByDocumento(String documento);
}
