# Base de Datos RoxFarma

Este directorio contiene el script SQL para crear la base de datos del Sistema RoxFarma.

## Requisitos Previos

1. **XAMPP instalado** con MySQL
2. **MySQL corriendo** en el puerto 3306
3. **phpMyAdmin** accesible en http://localhost/phpmyadmin

## Instrucciones de Instalación

### Opción 1: Usando phpMyAdmin (Recomendado)

1. **Abrir phpMyAdmin**
   - Ir a http://localhost/phpmyadmin en tu navegador
   - O hacer clic en "Admin" junto a MySQL en el panel de XAMPP

2. **Crear la base de datos**
   - Clic en "Nueva" en el panel izquierdo
   - Nombre: `roxfarma_db`
   - Cotejamiento: `utf8mb4_unicode_ci`
   - Clic en "Crear"

3. **Ejecutar el script SQL**
   - Seleccionar la base de datos `roxfarma_db` en el panel izquierdo
   - Ir a la pestaña "SQL"
   - Abrir el archivo `schema.sql` con un editor de texto
   - Copiar todo el contenido
   - Pegarlo en el área de texto de phpMyAdmin
   - Clic en "Continuar" para ejecutar

4. **Verificar la instalación**
   - En el panel izquierdo, expandir `roxfarma_db`
   - Deberías ver 10 tablas creadas:
     - auditoria
     - categoria
     - cliente
     - detalle_pedido
     - detalle_venta
     - pedido
     - producto
     - proveedor
     - usuario
     - venta

### Opción 2: Usando MySQL Command Line

1. **Abrir terminal/CMD**

2. **Navegar al directorio de la base de datos**
   ```bash
   cd ruta/al/proyecto/database
   ```

3. **Ejecutar el script**
   ```bash
   mysql -u root -p < schema.sql
   ```
   (Presionar Enter cuando pida contraseña, ya que está vacía)

## Estructura de la Base de Datos

### Tablas Principales

- **categoria**: Categorías de productos (Analgésicos, Antibióticos, etc.)
- **producto**: Medicamentos y productos farmacéuticos
- **cliente**: Farmacias y boticas que compran a RoxFarma
- **proveedor**: Proveedores de medicamentos
- **usuario**: Usuarios del sistema (Administrador y Trabajador)
- **venta**: Ventas realizadas a clientes
- **detalle_venta**: Productos incluidos en cada venta
- **pedido**: Pedidos de reabastecimiento a proveedores
- **detalle_pedido**: Productos incluidos en cada pedido
- **auditoria**: Registro de operaciones para trazabilidad

### Relaciones

```
Categoria (1) ──< (N) Producto
Proveedor (1) ──< (N) Pedido (1) ──< (N) DetallePedido >── (N) Producto
Cliente (1) ──< (N) Venta (1) ──< (N) DetalleVenta >── (N) Producto
Usuario (1) ──< (N) Venta
```

## Datos Iniciales

El script incluye datos de ejemplo para facilitar el desarrollo y pruebas:

### Usuarios del Sistema

| Usuario | Contraseña | Rol | Descripción |
|---------|-----------|-----|-------------|
| admin | password123 | ADMINISTRADOR | Acceso completo al sistema |
| jperez | password123 | TRABAJADOR | Acceso a ventas, pedidos e inventario |
| mgarcia | password123 | TRABAJADOR | Acceso a ventas, pedidos e inventario |

### Datos de Ejemplo

- **8 categorías** de productos
- **19 productos** con diferentes características:
  - Productos con stock normal
  - Productos con stock bajo (para probar alertas)
  - Productos próximos a vencer (para probar alertas)
- **8 clientes** (farmacias y boticas)
- **6 proveedores** de medicamentos

## Verificación

Después de ejecutar el script, puedes verificar que todo se instaló correctamente:

### En phpMyAdmin

1. Seleccionar la base de datos `roxfarma_db`
2. Ir a la pestaña "SQL"
3. Ejecutar:
   ```sql
   SELECT 'Categorías' AS Tabla, COUNT(*) AS Total FROM categoria
   UNION ALL
   SELECT 'Productos', COUNT(*) FROM producto
   UNION ALL
   SELECT 'Clientes', COUNT(*) FROM cliente
   UNION ALL
   SELECT 'Proveedores', COUNT(*) FROM proveedor
   UNION ALL
   SELECT 'Usuarios', COUNT(*) FROM usuario;
   ```

Deberías ver:
- Categorías: 8
- Productos: 19
- Clientes: 8
- Proveedores: 6
- Usuarios: 3

## Solución de Problemas

### Error: "Access denied for user 'root'@'localhost'"

**Solución**: Verificar que MySQL esté corriendo en XAMPP y que el usuario root no tenga contraseña.

### Error: "Table already exists"

**Solución**: La base de datos ya existe. Puedes:
1. Eliminar la base de datos existente en phpMyAdmin
2. O modificar el script para usar `DROP TABLE IF EXISTS` antes de cada `CREATE TABLE`

### Error: "Unknown database 'roxfarma_db'"

**Solución**: Crear manualmente la base de datos en phpMyAdmin antes de ejecutar el script.

## Notas Importantes

1. **Contraseñas cifradas**: Las contraseñas de los usuarios están cifradas con BCrypt (factor 12)
2. **Datos de ejemplo**: Los datos insertados son solo para desarrollo y pruebas
3. **Índices**: Se han creado índices en campos frecuentemente consultados para mejorar el rendimiento
4. **Integridad referencial**: Se usan claves foráneas con `ON DELETE CASCADE` donde corresponde
5. **Charset**: Todas las tablas usan `utf8mb4_unicode_ci` para soportar caracteres especiales

## Próximos Pasos

Una vez creada la base de datos, puedes:

1. Configurar el archivo `application.properties` de Spring Boot
2. Ejecutar la aplicación backend
3. Verificar la conexión en los logs
4. Comenzar a desarrollar las funcionalidades

## Contacto

Para dudas o problemas con la base de datos, consultar la documentación del proyecto o el archivo `EXPLICACION_ACADEMICA.txt`.
