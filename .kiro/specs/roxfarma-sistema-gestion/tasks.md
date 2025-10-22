# Plan de Implementación - Sistema RoxFarma

Este plan de implementación desglosa el desarrollo del sistema RoxFarma en tareas incrementales y manejables. Cada tarea está diseñada para construir sobre las anteriores, asegurando un progreso constante hacia un sistema funcional completo.

## Tareas de Implementación

- [x] 1. Configurar estructura del proyecto y dependencias


  - Crear proyecto Spring Boot con Maven
  - Configurar dependencias: Spring Web, Spring Data JPA, Spring Security, MySQL Driver, Lombok, iText PDF, JWT
  - Configurar archivo `application.properties` con conexión a MySQL
  - Crear estructura de paquetes: model, repository, service, controller, dto, config, exception, util
  - _Requerimientos: 14.1, 14.2_

- [ ] 2. Crear base de datos y modelo de entidades JPA
- [x] 2.1 Ejecutar script SQL para crear base de datos


  - Crear base de datos `roxfarma_db` en MySQL
  - Ejecutar script de creación de tablas (categoria, producto, proveedor, cliente, usuario, pedido, detalle_pedido, venta, detalle_venta, auditoria)
  - Insertar datos iniciales (categorías y usuario administrador)
  - _Requerimientos: 2.2, 6.2, 7.2, 8.2, 11.2_

- [x] 2.2 Implementar entidades JPA del modelo de datos


  - Crear entidad `Categoria` con anotaciones JPA
  - Crear entidad `Producto` con relación ManyToOne a Categoria
  - Crear entidad `Proveedor`
  - Crear entidad `Cliente`
  - Crear entidad `Usuario` con enum Rol (ADMINISTRADOR, TRABAJADOR)
  - Crear entidad `Pedido` con enum EstadoPedido (PENDIENTE, ENVIADO, RECIBIDO) y relación a Proveedor
  - Crear entidad `DetallePedido` con relaciones a Pedido y Producto
  - Crear entidad `Venta` con relaciones a Cliente y Usuario
  - Crear entidad `DetalleVenta` con relaciones a Venta y Producto
  - Crear entidad `Auditoria` para registro de operaciones
  - _Requerimientos: 2.1, 4.3, 5.2, 6.1, 7.1, 8.1, 11.1_

- [ ] 3. Implementar capa de repositorioúa s (DAO)
- [x] 3.1 Crear interfaces de repositorio con Spring Data JPA



  - Crear `CategoriaRepository` extendiendo JpaRepository
  - Crear `ProductoRepository` con métodos personalizados: findByStockLessThan, findByFechaVencimientoBefore, findByCategoria
  - Crear `ProveedorRepository`
  - Crear `ClienteRepository` con método findByDocumento
  - Crear `UsuarioRepository` con método findByUsuario
  - Crear `PedidoRepository` con métodos: findByEstado, findByProveedor
  - Crear `DetallePedidoRepository`
  - Crear `VentaRepository` con métodos: findByFechaBetween, findByCliente, findByUsuario
  - Crear `DetalleVentaRepository`
  - Crear `AuditoriaRepository`
  - _Requerimientos: 2.7, 3.7, 4.8, 5.5, 6.3, 7.3, 8.3, 10.1, 12.7_

- [ ] 4. Crear DTOs y validaciones
- [x] 4.1 Implementar DTOs para transferencia de datos


  - Crear `CategoriaDTO` con validaciones @NotBlank
  - Crear `ProductoDTO` con validaciones: @NotBlank para nombre, @NotNull y @DecimalMin para precio, @Future para fechaVencimiento, @Min para stock
  - Crear `ProveedorDTO` con validaciones @NotBlank
  - Crear `ClienteDTO` con validaciones @NotBlank
  - Crear `UsuarioDTO` con validaciones y campo rol
  - Crear `DetalleVentaDTO` con validaciones @NotNull y @Min para cantidad
  - Crear `VentaDTO` con lista de DetalleVentaDTO y validación @NotEmpty
  - Crear `DetallePedidoDTO` con validaciones
  - Crear `PedidoDTO` con lista de DetallePedidoDTO
  - Crear `LoginRequest` con validaciones @NotBlank
  - Crear `AuthResponse` con token, tipo, usuario y rol
  - Crear `ErrorResponse` para respuestas de error estandarizadas
  - _Requerimientos: 1.1, 2.1, 4.1, 5.1, 6.1, 7.1, 8.1, 13.5_

- [ ] 5. Implementar configuración de seguridad
- [x] 5.1 Configurar Spring Security con JWT


  - Crear clase `SecurityConfig` con @EnableWebSecurity y @EnableMethodSecurity
  - Configurar SecurityFilterChain con reglas de autorización por rol
  - Configurar endpoints públicos (/api/auth/**) y protegidos
  - Configurar política de sesión STATELESS
  - Crear bean PasswordEncoder con BCryptPasswordEncoder (factor 12)
  - _Requerimientos: 1.1, 1.3, 1.4, 1.5, 1.7, 12.2, 12.3, 12.5_

- [x] 5.2 Implementar servicio JWT


  - Crear `JwtService` con métodos: generarToken, extraerUsuario, validarToken, tokenExpirado
  - Configurar clave secreta y tiempo de expiración desde application.properties
  - Implementar lógica de generación de token con claims del usuario
  - Implementar validación de token y extracción de información
  - _Requerimientos: 1.1, 1.6_

- [x] 5.3 Crear filtro de autenticación JWT


  - Crear `JwtAuthFilter` extendiendo OncePerRequestFilter
  - Implementar doFilterInternal para extraer token del header Authorization
  - Validar token y cargar usuario en SecurityContext
  - Manejar excepciones de token inválido o expirado
  - _Requerimientos: 1.1, 1.6, 12.2_

- [x] 5.4 Implementar UserDetailsService personalizado


  - Crear `CustomUserDetailsService` implementando UserDetailsService
  - Implementar loadUserByUsername consultando UsuarioRepository
  - Mapear Usuario a UserDetails con roles y permisos
  - _Requerimientos: 1.1, 1.3, 1.4_

- [ ] 6. Implementar servicios de negocio - Gestión de Usuarios
- [x] 6.1 Crear UsuarioService con inyección de dependencias


  - Crear clase `UsuarioService` con anotación @Service
  - Inyectar UsuarioRepository y PasswordEncoder mediante constructor con @Autowired
  - Implementar método crearUsuario que cifre la contraseña con BCrypt antes de guardar
  - Implementar método actualizarUsuario que valide y actualice datos
  - Implementar método eliminarUsuario con validación de ventas asociadas
  - Implementar método listarUsuarios
  - Implementar método buscarPorNombreUsuario
  - Agregar logging con @Slf4j para todas las operaciones
  - _Requerimientos: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6, 12.1_

- [ ]* 6.2 Escribir pruebas unitarias para UsuarioService
  - Crear `UsuarioServiceTest` con @ExtendWith(MockitoExtension.class)
  - Mockear UsuarioRepository y PasswordEncoder con @Mock
  - Inyectar mocks en UsuarioService con @InjectMocks
  - Escribir test: debeCrearUsuarioConContrasenaCifrada
  - Escribir test: debeActualizarUsuarioCorrectamente
  - Escribir test: debeLanzarExcepcionCuandoUsuarioNoExiste
  - Escribir test: debeBuscarUsuarioPorNombreUsuario
  - _Requerimientos: 8.1, 8.2, 8.4_

- [ ] 7. Implementar servicios de negocio - Gestión de Categorías y Productos
- [x] 7.1 Crear CategoriaService


  - Crear clase `CategoriaService` con @Service
  - Inyectar CategoriaRepository mediante constructor
  - Implementar CRUD completo: crear, actualizar, eliminar, listar, buscarPorId
  - Validar que no se elimine categoría con productos asociados
  - Agregar logging de operaciones
  - _Requerimientos: 11.1, 11.2, 11.3, 11.4, 11.5_

- [x] 7.2 Crear ProductoService con lógica de negocio


  - Crear clase `ProductoService` con @Service
  - Inyectar ProductoRepository y CategoriaRepository mediante constructor
  - Implementar crearProducto validando que la categoría exista
  - Implementar actualizarProducto con validación de integridad referencial
  - Implementar eliminarProducto con advertencia si tiene ventas/pedidos asociados
  - Implementar listarTodosLosProductos
  - Implementar obtenerProductoPorId con manejo de ResourceNotFoundException
  - Implementar obtenerProductosConStockBajo(umbral) usando repositorio
  - Implementar obtenerProductosProximosAVencer(dias) calculando fecha límite
  - Agregar logging detallado
  - _Requerimientos: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 3.3, 3.4, 11.6_

- [ ]* 7.3 Escribir pruebas unitarias para ProductoService
  - Crear `ProductoServiceTest` con mocks de repositorios
  - Test: debeCrearProductoCorrectamente
  - Test: debeLanzarExcepcionCuandoCategoriaNoExiste
  - Test: debeObtenerProductosConStockBajo
  - Test: debeObtenerProductosProximosAVencer
  - Test: debeActualizarProductoCorrectamente
  - _Requerimientos: 2.1, 2.2, 2.4, 3.3, 3.4_

- [ ] 8. Implementar servicios de negocio - Gestión de Clientes y Proveedores
- [x] 8.1 Crear ClienteService


  - Crear clase `ClienteService` con @Service
  - Inyectar ClienteRepository mediante constructor
  - Implementar CRUD completo con validaciones
  - Validar unicidad de documento
  - Implementar método obtenerHistorialVentas(idCliente)
  - Agregar logging
  - _Requerimientos: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6_



- [ ] 8.2 Crear ProveedorService
  - Crear clase `ProveedorService` con @Service
  - Inyectar ProveedorRepository mediante constructor
  - Implementar CRUD completo con validaciones
  - Implementar método obtenerHistorialPedidos(idProveedor)
  - Agregar logging
  - _Requerimientos: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6_



- [ ] 9. Implementar servicio de Inventario
- [ ] 9.1 Crear InventarioService con control de stock
  - Crear clase `InventarioService` con @Service
  - Inyectar ProductoRepository y AuditoriaRepository mediante constructor
  - Implementar método disminuirStock(idProducto, cantidad, motivo) con @Transactional
  - Validar que el stock no sea negativo antes de disminuir
  - Implementar método aumentarStock(idProducto, cantidad, motivo) con @Transactional
  - Implementar método obtenerAlertas() que retorne productos con stock bajo y próximos a vencer
  - Implementar método registrarMovimiento para auditoría
  - Agregar logging detallado de todos los movimientos
  - _Requerimientos: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 9.2, 9.3, 12.1_

- [ ]* 9.2 Escribir pruebas unitarias para InventarioService
  - Test: debeDisminuirStockCorrectamente
  - Test: debeLanzarExcepcionCuandoStockInsuficiente
  - Test: debeAumentarStockCorrectamente
  - Test: debeGenerarAlertasDeStockBajo


  - Test: debeRegistrarMovimientoEnAuditoria
  - _Requerimientos: 3.1, 3.2, 3.3, 3.5, 12.1_

- [ ] 10. Implementar servicio de Ventas
- [ ] 10.1 Crear VentaService con lógica de negocio compleja
  - Crear clase `VentaService` con @Service
  - Inyectar VentaRepository, ProductoRepository, ClienteRepository, InventarioService mediante constructor
  - Implementar método registrarVenta(VentaDTO) con @Transactional
  - Validar que el cliente exista
  - Validar que todos los productos existan
  - Validar que haya stock suficiente para cada producto (lanzar StockInsuficienteException si no)
  - Calcular subtotal sumando precio * cantidad de cada detalle
  - Calcular IGV (18% del subtotal)
  - Calcular total (subtotal + IGV)
  - Crear entidad Venta con detalles
  - Llamar a InventarioService.disminuirStock para cada producto vendido
  - Guardar venta en repositorio
  - Agregar logging detallado
  - Implementar método listarVentas()
  - Implementar método listarVentasPorFecha(inicio, fin)
  - Implementar método obtenerVentaPorId(id)
  - _Requerimientos: 4.1, 4.2, 4.3, 4.4, 4.7, 4.8, 3.1, 12.1_

- [ ]* 10.2 Escribir pruebas unitarias para VentaService
  - Test: debeRegistrarVentaYActualizarStock
  - Test: debeLanzarExcepcionCuandoStockInsuficiente

  - Test: debeCalcularTotalConIGVCorrectamente
  - Test: debeLanzarExcepcionCuandoClienteNoExiste
  - Test: debeLanzarExcepcionCuandoProductoNoExiste
  - _Requerimientos: 4.1, 4.2, 4.3, 4.4, 4.7_

- [ ] 11. Implementar servicio de Pedidos
- [x] 11.1 Crear PedidoService con gestión de estados

  - Crear clase `PedidoService` con @Service
  - Inyectar PedidoRepository, ProveedorRepository, ProductoRepository, InventarioService mediante constructor
  - Implementar método crearPedido(PedidoDTO) con @Transactional
  - Validar que el proveedor exista
  - Validar que todos los productos existan
  - Crear entidad Pedido con estado PENDIENTE y detalles
  - Guardar pedido en repositorio
  - Implementar método actualizarEstadoPedido(id, nuevoEstado) con @Transactional
  - Validar transiciones de estado válidas (PENDIENTE → ENVIADO → RECIBIDO)
  - Si el nuevo estado es RECIBIDO, llamar a InventarioService.aumentarStock para cada producto
  - Lanzar EstadoPedidoInvalidoException si la transición no es válida
  - Implementar método listarPedidos()
  - Implementar método listarPedidosPorEstado(estado)
  - Agregar logging detallado
  - _Requerimientos: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 3.2, 12.1_

- [ ]* 11.2 Escribir pruebas unitarias para PedidoService
  - Test: debeCrearPedidoConEstadoPendiente
  - Test: debeActualizarEstadoCorrectamente
  - Test: debeAumentarStockCuandoEstadoEsRecibido
  - Test: debeLanzarExcepcionCuandoTransicionInvalida
  - Test: debeImpedirModificacionesCuandoEstadoRecibido
  - _Requerimientos: 5.1, 5.2, 5.3, 5.4, 5.7_

- [-] 12. Implementar servicio de generación de Boletas PDF

- [x] 12.1 Crear BoletaService para generar PDFs

  - Crear clase `BoletaService` con @Service
  - Inyectar VentaRepository mediante constructor
  - Implementar método generarBoletaPDF(idVenta) que retorne byte[]
  - Usar iText para crear documento PDF
  - Agregar encabezado con datos de RoxFarma (nombre, RUC, dirección, teléfono)
  - Agregar título "BOLETA DE VENTA ELECTRÓNICA"
  - Agregar información de la venta: número de boleta, fecha, cliente, documento, usuario que atendió
  - Crear tabla con columnas: Producto, Cantidad, Precio Unitario, Subtotal
  - Agregar filas con los detalles de la venta
  - Calcular y mostrar: Subtotal, IGV (18%), Total
  - Agregar pie de página "Gracias por su compra"
  - Manejar excepciones y lanzar RuntimeException si falla
  - Agregar logging
  - _Requerimientos: 4.5, 4.6_

- [ ]* 12.2 Escribir pruebas unitarias para BoletaService
  - Test: debeGenerarBoletaPDFCorrectamente
  - Test: debeLanzarExcepcionCuandoVentaNoExiste
  - Test: debeIncluirTodosLosDatosEnBoleta
  - _Requerimientos: 4.5, 4.6_

- [-] 13. Implementar servicio de Autenticación

- [x] 13.1 Crear AuthenticationService

  - Crear clase `AuthenticationService` con @Service
  - Inyectar AuthenticationManager, UsuarioRepository, JwtService, PasswordEncoder mediante constructor
  - Implementar método login(LoginRequest) que valide credenciales
  - Usar AuthenticationManager para autenticar
  - Si las credenciales son válidas, generar token JWT
  - Retornar AuthResponse con token, usuario, nombre y rol
  - Si las credenciales son inválidas, lanzar BadCredentialsException
  - Agregar logging de intentos de login
  - _Requerimientos: 1.1, 1.2_

- [ ]* 13.2 Escribir pruebas unitarias para AuthenticationService
  - Test: debeAutenticarUsuarioConCredencialesValidas
  - Test: debeLanzarExcepcionConCredencialesInvalidas
  - Test: debeGenerarTokenJWTCorrectamente
  - _Requerimientos: 1.1, 1.2_

- [ ] 14. Implementar servicio de Reportes
- [x] 14.1 Crear ReporteService


  - Crear clase `ReporteService` con @Service
  - Inyectar VentaRepository y ProductoRepository mediante constructor
  - Implementar método generarReporteVentas(fechaInicio, fechaFin) que retorne estadísticas
  - Calcular: total de ventas, cantidad de transacciones, productos más vendidos, ventas por cliente
  - Implementar método generarReporteInventario() que retorne estado del inventario
  - Incluir: stock actual por producto, productos con stock bajo, productos próximos a vencer
  - Agregar logging
  - _Requerimientos: 10.1, 10.2, 10.3_

- [ ] 15. Implementar servicio de Auditoría
- [x] 15.1 Crear AuditoriaService


  - Crear clase `AuditoriaService` con @Service
  - Inyectar AuditoriaRepository mediante constructor
  - Implementar método registrarOperacion(operacion, entidad, idEntidad, usuario, detalles)
  - Crear entidad Auditoria con fecha actual
  - Guardar en repositorio
  - Implementar método consultarAuditoria(fechaInicio, fechaFin, entidad, usuario)
  - Agregar logging
  - _Requerimientos: 12.1, 12.7_

- [ ] 16. Implementar controladores REST - Autenticación
- [x] 16.1 Crear AuthController


  - Crear clase `AuthController` con @RestController y @RequestMapping("/api/auth")
  - Inyectar AuthenticationService mediante constructor
  - Implementar endpoint POST /login que reciba LoginRequest
  - Validar request con @Valid
  - Llamar a authService.login y retornar AuthResponse
  - Manejar excepciones y retornar ResponseEntity apropiado
  - Agregar logging de intentos de login
  - _Requerimientos: 1.1, 1.2, 13.1_

- [ ] 17. Implementar controladores REST - Gestión de Usuarios
- [x] 17.1 Crear UsuarioController


  - Crear clase `UsuarioController` con @RestController y @RequestMapping("/api/usuarios")
  - Inyectar UsuarioService mediante constructor
  - Implementar GET /api/usuarios con @PreAuthorize("hasRole('ADMINISTRADOR')")
  - Implementar GET /api/usuarios/{id} con autorización
  - Implementar POST /api/usuarios con @Valid y autorización ADMINISTRADOR
  - Implementar PUT /api/usuarios/{id} con @Valid y autorización ADMINISTRADOR
  - Implementar DELETE /api/usuarios/{id} con autorización ADMINISTRADOR
  - Retornar ResponseEntity con códigos HTTP apropiados
  - Agregar logging
  - _Requerimientos: 8.1, 8.2, 8.3, 8.4, 8.7, 1.3, 1.4_

- [ ]* 17.2 Escribir pruebas de integración para UsuarioController
  - Usar @WebMvcTest(UsuarioController.class)
  - Test: GET /api/usuarios debe retornar lista
  - Test: POST /api/usuarios debe crear usuario
  - Test: PUT /api/usuarios/{id} debe actualizar
  - Test: DELETE /api/usuarios/{id} debe eliminar
  - Test: debe denegar acceso a TRABAJADOR
  - _Requerimientos: 8.1, 8.2, 8.3, 8.4, 8.7_

- [ ] 18. Implementar controladores REST - Gestión de Productos
- [x] 18.1 Crear ProductoController


  - Crear clase `ProductoController` con @RestController y @RequestMapping("/api/productos")
  - Inyectar ProductoService mediante constructor
  - Implementar GET /api/productos (accesible para ADMINISTRADOR y TRABAJADOR)
  - Implementar GET /api/productos/{id}
  - Implementar POST /api/productos con @Valid y @PreAuthorize("hasRole('ADMINISTRADOR')")
  - Implementar PUT /api/productos/{id} con @Valid y autorización ADMINISTRADOR
  - Implementar DELETE /api/productos/{id} con autorización ADMINISTRADOR
  - Implementar GET /api/productos/alertas/stock-bajo
  - Implementar GET /api/productos/alertas/proximos-vencer
  - Retornar ResponseEntity apropiados
  - Agregar logging
  - _Requerimientos: 2.1, 2.2, 2.3, 2.4, 2.5, 2.7, 3.3, 3.4, 1.3, 1.4_

- [ ] 19. Implementar controladores REST - Gestión de Categorías
- [x] 19.1 Crear CategoriaController


  - Crear clase `CategoriaController` con @RestController y @RequestMapping("/api/categorias")
  - Inyectar CategoriaService mediante constructor
  - Implementar CRUD completo con autorización ADMINISTRADOR
  - Agregar logging
  - _Requerimientos: 11.1, 11.2, 11.3, 11.4, 1.3_

- [ ] 20. Implementar controladores REST - Gestión de Clientes y Proveedores
- [x] 20.1 Crear ClienteController


  - Crear clase `ClienteController` con @RestController y @RequestMapping("/api/clientes")
  - Inyectar ClienteService mediante constructor
  - Implementar CRUD completo con autorización ADMINISTRADOR
  - Implementar GET /api/clientes/{id}/ventas para historial
  - Agregar logging
  - _Requerimientos: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 1.3_

- [x] 20.2 Crear ProveedorController


  - Crear clase `ProveedorController` con @RestController y @RequestMapping("/api/proveedores")
  - Inyectar ProveedorService mediante constructor
  - Implementar CRUD completo con autorización ADMINISTRADOR
  - Implementar GET /api/proveedores/{id}/pedidos para historial
  - Agregar logging
  - _Requerimientos: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 1.3_

- [ ] 21. Implementar controladores REST - Gestión de Ventas
- [x] 21.1 Crear VentaController


  - Crear clase `VentaController` con @RestController y @RequestMapping("/api/ventas")
  - Inyectar VentaService y BoletaService mediante constructor
  - Implementar POST /api/ventas con @Valid y @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
  - Implementar GET /api/ventas (accesible para ambos roles)
  - Implementar GET /api/ventas/{id}
  - Implementar GET /api/ventas/{id}/boleta que retorne byte[] con Content-Type application/pdf
  - Configurar headers para descarga de PDF: Content-Disposition attachment
  - Manejar excepciones (StockInsuficienteException, ResourceNotFoundException)
  - Agregar logging detallado
  - _Requerimientos: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 1.3, 1.4_

- [ ]* 21.2 Escribir pruebas de integración para VentaController
  - Test: POST /api/ventas debe registrar venta
  - Test: GET /api/ventas debe retornar lista
  - Test: GET /api/ventas/{id}/boleta debe retornar PDF
  - Test: debe rechazar venta con stock insuficiente
  - _Requerimientos: 4.1, 4.2, 4.5, 4.7_

- [ ] 22. Implementar controladores REST - Gestión de Pedidos
- [x] 22.1 Crear PedidoController


  - Crear clase `PedidoController` con @RestController y @RequestMapping("/api/pedidos")
  - Inyectar PedidoService mediante constructor
  - Implementar POST /api/pedidos con @Valid y @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
  - Implementar GET /api/pedidos (accesible para ambos roles)
  - Implementar GET /api/pedidos/{id}
  - Implementar PUT /api/pedidos/{id}/estado con @RequestParam EstadoPedido y autorización
  - Manejar excepciones (EstadoPedidoInvalidoException, ResourceNotFoundException)
  - Agregar logging
  - _Requerimientos: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 1.3, 1.4_

- [ ] 23. Implementar controladores REST - Dashboard y Reportes
- [x] 23.1 Crear DashboardController


  - Crear clase `DashboardController` con @RestController y @RequestMapping("/api/dashboard")
  - Inyectar InventarioService mediante constructor
  - Implementar GET /api/dashboard/alertas que retorne alertas de stock bajo y vencimientos
  - Implementar GET /api/dashboard/estadisticas que retorne resumen del inventario
  - Accesible para ADMINISTRADOR y TRABAJADOR
  - Agregar logging
  - _Requerimientos: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_

- [x] 23.2 Crear ReporteController


  - Crear clase `ReporteController` con @RestController y @RequestMapping("/api/reportes")
  - Inyectar ReporteService mediante constructor
  - Implementar GET /api/reportes/ventas con @RequestParam fechaInicio y fechaFin
  - Implementar GET /api/reportes/inventario
  - Aplicar @PreAuthorize("hasRole('ADMINISTRADOR')")
  - Agregar logging
  - _Requerimientos: 10.1, 10.2, 10.3, 10.5, 1.4_

- [ ] 24. Implementar manejo global de excepciones
- [x] 24.1 Crear GlobalExceptionHandler



  - Crear clase `GlobalExceptionHandler` con @ControllerAdvice
  - Implementar @ExceptionHandler para ResourceNotFoundException (retornar 404)
  - Implementar @ExceptionHandler para StockInsuficienteException (retornar 400)
  - Implementar @ExceptionHandler para EstadoPedidoInvalidoException (retornar 400)
  - Implementar @ExceptionHandler para MethodArgumentNotValidException (retornar 400 con detalles de validación)
  - Implementar @ExceptionHandler para BadCredentialsException (retornar 401)
  - Implementar @ExceptionHandler para AccessDeniedException (retornar 403)
  - Implementar @ExceptionHandler genérico para Exception (retornar 500 sin exponer detalles internos)
  - Usar ErrorResponse para respuestas estandarizadas
  - Agregar logging de errores
  - _Requerimientos: 1.2, 1.5, 3.5, 12.2, 12.3, 12.4, 13.5, 13.6_

- [ ] 25. Crear excepciones personalizadas
- [ ] 25.1 Implementar clases de excepción
  - Crear `ResourceNotFoundException` extendiendo RuntimeException
  - Crear `StockInsuficienteException` extendiendo RuntimeException
  - Crear `EstadoPedidoInvalidoException` extendiendo RuntimeException
  - Cada excepción debe tener constructor que reciba mensaje
  - _Requerimientos: 3.5, 5.7, 13.5_

- [ ] 26. Configurar logging con LogBack
- [ ] 26.1 Crear configuración de LogBack
  - Crear archivo `logback-spring.xml` en src/main/resources
  - Configurar appender CONSOLE con patrón de fecha y mensaje
  - Configurar appender FILE con RollingFileAppender
  - Configurar rotación diaria de logs (logs/roxfarma.%d{yyyy-MM-dd}.log)
  - Configurar retención de 30 días
  - Configurar nivel INFO para root logger
  - Configurar nivel DEBUG para paquete com.roxfarma
  - _Requerimientos: 12.1, 14.4_

- [ ] 27. Implementar frontend React - Configuración inicial
- [ ] 27.1 Crear proyecto React con TypeScript
  - Ejecutar `npx create-react-app roxfarma-frontend --template typescript`
  - Instalar dependencias: axios, react-router-dom, @types/react-router-dom
  - Instalar Material-UI o Bootstrap para componentes UI
  - Crear estructura de carpetas: components, services, context, hooks, utils
  - Configurar proxy para desarrollo apuntando a http://localhost:8080
  - _Requerimientos: 13.1, 13.2_

- [ ] 27.2 Crear servicio de API con Axios
  - Crear archivo `services/api.ts`
  - Configurar instancia de axios con baseURL
  - Implementar interceptor de request para agregar token JWT del localStorage
  - Implementar interceptor de response para manejar errores 401 (redirigir a login)
  - _Requerimientos: 1.1, 1.6, 12.2_

- [ ] 28. Implementar frontend React - Autenticación
- [ ] 28.1 Crear contexto de autenticación
  - Crear `context/AuthContext.tsx`
  - Definir interfaz AuthContextType con: usuario, login, logout, isAuthenticated, hasRole
  - Implementar AuthProvider que maneje estado del usuario
  - Implementar función login que llame a API y guarde token en localStorage
  - Implementar función logout que limpie token y estado
  - Implementar función hasRole para verificar permisos
  - Crear hook personalizado useAuth
  - _Requerimientos: 1.1, 1.3, 1.4_

- [ ] 28.2 Crear componente de Login
  - Crear `components/auth/Login.tsx`
  - Implementar formulario con campos usuario y contraseña
  - Implementar validación de campos requeridos
  - Llamar a useAuth().login al enviar formulario
  - Mostrar mensajes de error si las credenciales son inválidas
  - Redirigir a /dashboard después de login exitoso
  - Aplicar estilos básicos
  - _Requerimientos: 1.1, 1.2, 13.1, 13.2_

- [ ] 29. Implementar frontend React - Componentes comunes
- [ ] 29.1 Crear componentes de navegación
  - Crear `components/common/Navbar.tsx` con logo y nombre de usuario
  - Crear `components/common/Sidebar.tsx` con menú de navegación
  - Mostrar opciones según rol del usuario (usar hasRole)
  - Implementar botón de logout
  - Aplicar estilos consistentes
  - _Requerimientos: 13.2, 13.3, 1.3, 1.4_

- [ ] 29.2 Crear componentes de utilidad
  - Crear `components/common/Alert.tsx` para mostrar mensajes
  - Crear `components/common/Loading.tsx` para estados de carga
  - Crear `components/common/ConfirmDialog.tsx` para confirmaciones
  - _Requerimientos: 13.5_

- [ ] 30. Implementar frontend React - Gestión de Productos
- [ ] 30.1 Crear servicio de productos
  - Crear `services/productoService.ts`
  - Implementar funciones: listarProductos, obtenerProducto, crearProducto, actualizarProducto, eliminarProducto
  - Implementar funciones: obtenerStockBajo, obtenerProximosVencer
  - Todas las funciones deben usar la instancia de axios configurada
  - _Requerimientos: 2.1, 2.2, 2.3, 2.4, 2.5, 2.7, 3.3, 3.4_

- [ ] 30.2 Crear componente ProductoList
  - Crear `components/productos/ProductoList.tsx`
  - Cargar y mostrar lista de productos en tabla
  - Implementar búsqueda y filtrado por nombre o categoría
  - Mostrar botones de editar y eliminar (solo para ADMINISTRADOR)
  - Mostrar indicador visual para productos con stock bajo
  - Implementar paginación
  - _Requerimientos: 2.3, 2.6, 2.7, 13.3, 13.4_

- [ ] 30.3 Crear componente ProductoForm
  - Crear `components/productos/ProductoForm.tsx`
  - Implementar formulario con campos: nombre, descripción, precio, stock, categoría, fecha_vencimiento
  - Implementar validaciones del lado del cliente
  - Soportar modo creación y edición
  - Llamar a productoService para guardar
  - Mostrar mensajes de éxito o error
  - _Requerimientos: 2.1, 2.2, 2.4, 13.5_

- [ ] 31. Implementar frontend React - Dashboard de Inventario
- [ ] 31.1 Crear componente Dashboard
  - Crear `components/inventario/Dashboard.tsx`
  - Mostrar tarjetas con estadísticas: total productos, stock total, alertas stock bajo, alertas vencimiento
  - Cargar datos desde API
  - Implementar auto-refresh cada 30 segundos
  - _Requerimientos: 9.1, 9.2, 9.3_

- [ ] 31.2 Crear componentes de alertas
  - Crear `components/inventario/AlertasStock.tsx`
  - Mostrar lista de productos con stock bajo en tabla
  - Incluir nombre, stock actual y botón para ver detalle
  - Crear `components/inventario/AlertasVencimiento.tsx`
  - Mostrar lista de productos próximos a vencer
  - Incluir nombre, fecha de vencimiento y días restantes
  - _Requerimientos: 9.2, 9.3, 9.4, 9.5_

- [ ] 32. Implementar frontend React - Gestión de Ventas
- [ ] 32.1 Crear servicio de ventas
  - Crear `services/ventaService.ts`
  - Implementar funciones: registrarVenta, listarVentas, obtenerVenta, descargarBoleta
  - _Requerimientos: 4.1, 4.2, 4.5, 4.8_

- [ ] 32.2 Crear componente VentaForm
  - Crear `components/ventas/VentaForm.tsx`
  - Implementar selector de cliente (dropdown o autocomplete)
  - Implementar tabla dinámica para agregar productos con cantidades
  - Calcular y mostrar subtotal, IGV y total en tiempo real
  - Validar stock disponible antes de agregar producto
  - Implementar botón "Registrar Venta"
  - Mostrar mensaje de éxito y opción de descargar boleta
  - Manejar errores (stock insuficiente, cliente no seleccionado)
  - _Requerimientos: 4.1, 4.2, 4.3, 4.5, 4.7, 13.5_

- [ ] 32.3 Crear componente VentaList
  - Crear `components/ventas/VentaList.tsx`
  - Mostrar lista de ventas con: fecha, cliente, total, usuario
  - Implementar filtro por rango de fechas
  - Incluir botón para descargar boleta PDF
  - _Requerimientos: 4.8, 4.6_

- [ ] 33. Implementar frontend React - Gestión de Pedidos
- [ ] 33.1 Crear servicio de pedidos
  - Crear `services/pedidoService.ts`
  - Implementar funciones: crearPedido, listarPedidos, obtenerPedido, actualizarEstado
  - _Requerimientos: 5.1, 5.2, 5.3, 5.5_

- [ ] 33.2 Crear componente PedidoForm
  - Crear `components/pedidos/PedidoForm.tsx`
  - Implementar selector de proveedor
  - Implementar tabla dinámica para agregar productos con cantidades
  - Implementar botón "Crear Pedido"
  - Mostrar mensaje de éxito
  - _Requerimientos: 5.1, 5.2, 13.5_

- [ ] 33.3 Crear componente PedidoList
  - Crear `components/pedidos/PedidoList.tsx`
  - Mostrar lista de pedidos con: fecha, proveedor, estado
  - Implementar filtro por estado
  - Incluir botones para cambiar estado (PENDIENTE → ENVIADO → RECIBIDO)
  - Mostrar confirmación antes de cambiar a RECIBIDO (actualiza stock)
  - Deshabilitar cambios si el estado es RECIBIDO
  - _Requerimientos: 5.3, 5.4, 5.5, 5.6, 5.7_

- [ ] 34. Implementar frontend React - Gestión de Usuarios, Clientes y Proveedores
- [ ] 34.1 Crear módulo de gestión de usuarios
  - Crear `services/usuarioService.ts`
  - Crear `components/usuarios/UsuarioList.tsx` con tabla de usuarios
  - Crear `components/usuarios/UsuarioForm.tsx` con formulario
  - Implementar CRUD completo
  - Restringir acceso solo a ADMINISTRADOR
  - _Requerimientos: 8.1, 8.2, 8.3, 8.4, 8.7, 1.4_

- [ ] 34.2 Crear módulo de gestión de clientes
  - Crear `services/clienteService.ts`
  - Crear `components/clientes/ClienteList.tsx`
  - Crear `components/clientes/ClienteForm.tsx`
  - Implementar CRUD completo
  - Restringir acceso solo a ADMINISTRADOR
  - _Requerimientos: 6.1, 6.2, 6.3, 6.4, 6.5, 1.4_

- [ ] 34.3 Crear módulo de gestión de proveedores
  - Crear `services/proveedorService.ts`
  - Crear `components/proveedores/ProveedorList.tsx`
  - Crear `components/proveedores/ProveedorForm.tsx`
  - Implementar CRUD completo
  - Restringir acceso solo a ADMINISTRADOR
  - _Requerimientos: 7.1, 7.2, 7.3, 7.4, 7.5, 1.4_

- [ ] 35. Implementar frontend React - Reportes
- [ ] 35.1 Crear componente de reportes de ventas
  - Crear `services/reporteService.ts`
  - Crear `components/reportes/ReporteVentas.tsx`
  - Implementar selector de rango de fechas
  - Mostrar estadísticas: total ventas, cantidad transacciones, productos más vendidos
  - Mostrar gráficos básicos (opcional: usar Chart.js o Recharts)
  - Restringir acceso solo a ADMINISTRADOR
  - _Requerimientos: 10.1, 10.2, 1.4_

- [ ] 36. Implementar enrutamiento y protección de rutas
- [ ] 36.1 Configurar React Router
  - Crear `App.tsx` con configuración de rutas
  - Implementar PrivateRoute para rutas protegidas
  - Configurar rutas: /login, /dashboard, /productos, /ventas, /pedidos, /clientes, /proveedores, /usuarios, /reportes
  - Redirigir a /login si no está autenticado
  - Redirigir a /dashboard después de login
  - _Requerimientos: 13.2, 1.3, 1.4_

- [ ] 37. Crear documento de explicación para exposición
- [ ] 37.1 Crear archivo de explicación académica
  - Crear archivo `EXPLICACION_ACADEMICA.txt` en la raíz del proyecto
  - Incluir explicación detallada de MVC con ejemplos del proyecto
  - Incluir explicación de DAO con ejemplos
  - Incluir explicación de cada principio SOLID con ejemplos específicos de RoxFarma
  - Incluir explicación de TDD con ejemplos de pruebas
  - Incluir explicación de Inyección de Dependencias con ejemplos CON y SIN DI
  - Incluir explicación de Seguridad: autenticación, autorización, JWT, cifrado
  - Incluir diagrama de flujo de autenticación
  - Incluir ejemplos de código comentados en español
  - Organizar en secciones claras para facilitar el estudio
  - _Requerimientos: Todos (documento de apoyo académico)_

- [ ] 38. Pruebas de integración y validación final
- [ ] 38.1 Realizar pruebas de integración del sistema completo
  - Verificar flujo completo de autenticación (login, logout, expiración de token)
  - Verificar flujo completo de venta (seleccionar cliente, agregar productos, registrar, generar boleta, actualizar stock)
  - Verificar flujo completo de pedido (crear pedido, cambiar estados, actualizar stock al recibir)
  - Verificar alertas de stock bajo y vencimientos
  - Verificar autorización por roles (ADMINISTRADOR vs TRABAJADOR)
  - Verificar manejo de errores (stock insuficiente, credenciales inválidas, recursos no encontrados)
  - Verificar generación de boletas PDF
  - Verificar auditoría de operaciones
  - _Requerimientos: Todos_

- [ ] 38.2 Validar cumplimiento de requerimientos no funcionales
  - Verificar que las contraseñas estén cifradas en la base de datos
  - Verificar que los endpoints estén protegidos correctamente
  - Verificar que los logs se generen correctamente
  - Verificar tiempos de respuesta aceptables
  - Verificar que la interfaz sea intuitiva y consistente
  - _Requerimientos: 12.5, 12.2, 12.3, 14.1, 14.2, 13.3, 13.4_

## Notas Importantes

### Sobre las Pruebas Unitarias (Tareas marcadas con *)

Las tareas marcadas con asterisco (*) son pruebas unitarias opcionales. Estas tareas son importantes para garantizar la calidad del código, pero se consideran opcionales para priorizar la implementación de funcionalidades core del sistema.

Si decides implementar las pruebas unitarias, asegúrate de:
- Usar JUnit 5 y Mockito
- Mockear todas las dependencias
- Seguir el patrón Arrange-Act-Assert
- Nombrar los tests de forma descriptiva
- Alcanzar al menos 70% de cobertura de código

### Orden de Implementación Recomendado

1. **Fase 1 - Fundamentos (Tareas 1-5)**: Configuración, base de datos, entidades, repositorios, seguridad
2. **Fase 2 - Lógica de Negocio (Tareas 6-15)**: Servicios con toda la lógica de negocio
3. **Fase 3 - API REST (Tareas 16-24)**: Controladores y manejo de excepciones
4. **Fase 4 - Frontend (Tareas 27-36)**: Interfaz de usuario React
5. **Fase 5 - Documentación y Validación (Tareas 37-38)**: Documento académico y pruebas finales

### Principios a Seguir Durante la Implementación

1. **SOLID**: Aplicar consistentemente en todas las clases
2. **Inyección de Dependencias**: Usar siempre constructor injection con @Autowired
3. **Nombres en Español**: Mantener consistencia en nombres de clases, métodos y variables
4. **Comentarios**: Agregar comentarios explicativos en español para facilitar la comprensión académica
5. **Logging**: Usar @Slf4j y registrar operaciones importantes
6. **Transacciones**: Usar @Transactional en operaciones que modifiquen múltiples entidades
7. **Validaciones**: Validar datos en DTOs y en servicios
8. **Manejo de Errores**: Usar excepciones personalizadas y GlobalExceptionHandler

### Tecnologías y Versiones

- Java 17+
- Spring Boot 3.x
- MySQL 8.0
- React 18+
- TypeScript 4.9+
- Maven 3.8+
- Node.js 18+

### Estructura Final del Proyecto

```
roxfarma/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/roxfarma/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── config/
│   │   │   │   ├── exception/
│   │   │   │   └── RoxFarmaApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── logback-spring.xml
│   │   └── test/
│   │       └── java/com/roxfarma/
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── services/
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── utils/
│   │   └── App.tsx
│   └── package.json
├── database/
│   └── schema.sql
└── EXPLICACION_ACADEMICA.txt
```

Este plan de implementación está diseñado para ser ejecutado de forma incremental, permitiendo validar cada componente antes de avanzar al siguiente. Cada tarea construye sobre las anteriores, asegurando un desarrollo ordenado y sistemático del sistema RoxFarma.
