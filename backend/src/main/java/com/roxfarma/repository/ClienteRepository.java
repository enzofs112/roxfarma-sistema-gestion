package com.roxfarma.repository;

import com.roxfarma.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio (DAO) para la entidad Cliente
 * Este repositorio incluye un método personalizado para buscar por documento RUC
 * @author grupo2
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca un cliente por su número de documento (RUC).
     * Convención: findBy + NombreCampo
     * SQL generado: SELECT * FROM cliente WHERE documento = ???
     * @param documento Número de documento (RUC) del cliente
     * @return Optional con el cliente si existe, vacío si no
     */
    Optional<Cliente> findByDocumento(String documento);
    
    /**
     * Verifica si existe un cliente con el documento especificado
     * para para validar duplicados antes de crear un cliente
     */
    boolean existsByDocumento(String documento);
}
