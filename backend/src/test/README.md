# 🧪 Pruebas Unitarias - RoxFarma Backend

Este directorio contiene las pruebas unitarias y de integración del sistema RoxFarma.

## 📋 Cobertura de Pruebas

### ✅ Pruebas Implementadas

#### 1. **VentaServiceTest** - Módulo de Ventas
- ✅ Registro de venta correctamente
- ✅ Validación de cliente existente
- ✅ Validación de producto existente
- ✅ Cálculo correcto de IGV (18%)
- ✅ Obtención de venta por ID

**Casos probados:** 5  
**Cobertura:** Lógica de negocio crítica de ventas

#### 2. **ProductoServiceTest** - Módulo de Productos
- ✅ Creación de producto
- ✅ Validación de categoría existente
- ✅ Obtención de producto por ID
- ✅ Alertas de stock bajo (< 10 unidades)
- ✅ Actualización de producto
- ✅ Eliminación de producto

**Casos probados:** 6  
**Cobertura:** CRUD completo y alertas de inventario

#### 3. **AuthControllerTest** - Módulo de Autenticación
- ✅ Login exitoso con credenciales válidas
- ✅ Rechazo de credenciales inválidas
- ✅ Validación de campos requeridos
- ✅ Generación de token JWT válido

**Casos probados:** 4  
**Cobertura:** Seguridad y autenticación

## 🚀 Ejecutar Pruebas

### Todas las pruebas
```bash
cd backend
mvn test
```

### Pruebas específicas
```bash
# Solo pruebas de servicios
mvn test -Dtest=*ServiceTest

# Solo pruebas de controladores
mvn test -Dtest=*ControllerTest

# Una clase específica
mvn test -Dtest=VentaServiceTest
```

### Con reporte de cobertura
```bash
mvn test jacoco:report
```

## 📊 Resultados Esperados

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

## 🛠️ Tecnologías Utilizadas

- **JUnit 5** - Framework de pruebas
- **Mockito** - Mocking de dependencias
- **Spring Boot Test** - Pruebas de integración
- **MockMvc** - Pruebas de controladores REST

## 📝 Convenciones

### Nomenclatura de métodos
```java
@Test
void deberia[Accion][Condicion]() {
    // Arrange - Preparar datos
    // Act - Ejecutar acción
    // Assert - Verificar resultado
}
```

### Estructura AAA (Arrange-Act-Assert)
Todas las pruebas siguen el patrón AAA para mayor claridad:
1. **Arrange**: Configurar datos y mocks
2. **Act**: Ejecutar el método a probar
3. **Assert**: Verificar el resultado esperado

## 🎯 Próximas Pruebas (Futuras)

- [ ] PedidoServiceTest - Gestión de pedidos
- [ ] InventarioServiceTest - Control de stock
- [ ] UsuarioServiceTest - Gestión de usuarios
- [ ] ReporteServiceTest - Generación de reportes

## 📈 Métricas de Calidad

| Módulo | Pruebas | Cobertura |
|--------|---------|-----------|
| Ventas | 5 | ✅ Alta |
| Productos | 6 | ✅ Alta |
| Autenticación | 4 | ✅ Alta |
| **Total** | **15** | **✅ Crítico cubierto** |

## 💡 Notas para Desarrollo

- Las pruebas usan **mocks** para aislar la lógica de negocio
- No requieren base de datos real para ejecutarse
- Son rápidas y pueden ejecutarse en cualquier momento
- Garantizan que los cambios no rompan funcionalidad existente

---

**Desarrollado para RoxFarma - Sistema de Gestión Farmacéutica**
