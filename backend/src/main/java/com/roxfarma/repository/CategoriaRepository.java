package com.roxfarma.repository;

import com.roxfarma.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Patrón DAO (Data Access Object):
 * - Encapsula toda la lógica de acceso a datos
 * - Proporciona una interfaz abstracta para la persistencia
 * - Separa la lógica de negocio de la lógica de acceso a datos
 * 
 * Spring Data JPA genera automáticamente la implementación con métodos CRUD:
 * - save(Categoria): Guardar o actualizar
 * - findById(Long): Buscar por ID
 * - findAll(): Listar todas
 * - deleteById(Long): Eliminar por ID
 * - count(): Contar registros
 * - existsById(Long): Verificar existencia
 * 
 * @author grupo2
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Spring Data JPA proporciona automáticamente todos los métodos CRUD
    // no se digitará implementación
}
