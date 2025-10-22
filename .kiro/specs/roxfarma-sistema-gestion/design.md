# Documento de Diseño - Sistema RoxFarma

## Visión General

El sistema RoxFarma es una aplicación web empresarial desarrollada con arquitectura multicapa que implementa el patrón Modelo-Vista-Controlador (MVC) junto con el patrón DAO (Data Access Object). El sistema está diseñado siguiendo los principios SOLID, utilizando inyección de dependencias de Spring Framework y aplicando desarrollo guiado por pruebas (TDD).

### Objetivos del Diseño

1. **Separación de responsabilidades**: Cada capa tiene una responsabilidad única y bien definida
2. **Mantenibilidad**: Código modular y desacoplado que facilita cambios futuros
3. **Testabilidad**: Componentes diseñados para ser fácilmente probables mediante pruebas unitarias
4. **Seguridad**: Implementación robusta de autenticación y autorización
5. **Escalabilidad**: Arquitectura que permite crecimiento futuro del sistema

### Stack Tecnológico

**Backend:**
- Java 17+
- Spring Boot 3.x
- Spring Security (JWT)
- Spring Data JPA
- MySQL 8.0
- Maven
- Lombok
- LogBack

**Frontend:**
- React 18+ con TypeScript
- Axios para comunicación HTTP
- React Router para navegación
- Material-UI o Bootstrap para componentes

**Herramientas de Desarrollo:**
- JUnit 5 y Mockito para pruebas
- iText o Apache PDFBox para generación de PDFs
- Postman para pruebas de API

## Arquitectura del Sistema

### Arquitectura Multicapa

El sistema implementa una arquitectura de 5 capas claramente definidas:

```
┌─────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN            │
│    (Frontend React / Thymeleaf)         │
└─────────────────────────────────────────┘
                    ↓ HTTP/REST
┌─────────────────────────────────────────┐
│         CAPA DE CONTROLADORES           │
│    (@RestController / @Controller)      │
│  - ProductoController                   │
│  - VentaController                      │
│  - PedidoController                     │
│  - UsuarioController                    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         CAPA DE SERVICIOS               │
│           (@Service)                    │
│  - ProductoService                      │
│  - VentaService                         │
│  - PedidoService                        │
│  - InventarioService                    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         CAPA DE ACCESO A DATOS          │
│    (@Repository - DAO Pattern)          │
│  - ProductoRepository                   │
│  - VentaRepository                      │
│  - PedidoRepository                     │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         CAPA DE PERSISTENCIA            │
│         (MySQL Database)                │
└─────────────────────────────────────────┘
```

### Patrón MVC (Modelo-Vista-Controlador)

**Modelo (Model):**
- Entidades JPA que representan las tablas de la base de datos
- Ubicación: `com.roxfarma.model`
- Ejemplos: `Producto`, `Venta`, `Pedido`, `Usuario`

**Vista (View):**
- Frontend React con componentes reutilizables
- Ubicación: `frontend/src/components`
- Ejemplos: `ProductoList`, `VentaForm`, `Dashboard`

**Controlador (Controller):**
- Controladores REST que manejan peticiones HTTP
- Ubicación: `com.roxfarma.controller`
- Ejemplos: `ProductoController`, `VentaController`

### Patrón DAO (Data Access Object)

El patrón DAO encapsula toda la lógica de acceso a datos, proporcionando una interfaz abstracta para la persistencia.

**Estructura:**
```
Repository Interface (DAO) → Spring Data JPA → Base de Datos
```

**Ventajas:**
- Desacoplamiento entre lógica de negocio y persistencia
- Facilita cambios en la capa de datos sin afectar servicios
- Permite pruebas unitarias con repositorios mock

## Componentes y Interfaces

### 1. Capa de Modelo (Entidades JPA)

#### Producto
```java
@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;
    
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private LocalDate fechaVencimiento;
    private Integer stock;
    
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}
```

#### Venta
```java
@Entity
@Table(name = "venta")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;
    
    private LocalDateTime fecha;
    private BigDecimal total;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;
}
```

#### Pedido
```java
@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;
    
    private LocalDateTime fecha;
    
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado; // PENDIENTE, ENVIADO, RECIBIDO
    
    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;
}
```

#### Usuario
```java
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    
    private String nombre;
    private String usuario;
    private String contrasena; // Cifrada con BCrypt
    
    @Enumerated(EnumType.STRING)
    private Rol rol; // ADMINISTRADOR, TRABAJADOR
}
```

### 2. Capa de Repositorio (DAO)

Los repositorios extienden `JpaRepository` de Spring Data JPA, proporcionando operaciones CRUD automáticas.

```java
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockLessThan(Integer umbral);
    List<Producto> findByFechaVencimientoBefore(LocalDate fecha);
    List<Producto> findByCategoria(Categoria categoria);
}

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByCliente(Cliente cliente);
    List<Venta> findByUsuario(Usuario usuario);
}

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByProveedor(Proveedor proveedor);
}

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);
}
```

### 3. Capa de Servicio (Lógica de Negocio)

Los servicios contienen la lógica de negocio y orquestan las operaciones entre repositorios.

#### ProductoService

**Responsabilidades:**
- CRUD de productos
- Validación de datos de productos
- Gestión de stock
- Generación de alertas de stock bajo

```java
@Service
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    
    // Inyección de dependencias mediante constructor
    @Autowired
    public ProductoService(ProductoRepository productoRepository, 
                          CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }
    
    public Producto crearProducto(ProductoDTO dto);
    public Producto actualizarProducto(Long id, ProductoDTO dto);
    public void eliminarProducto(Long id);
    public Producto obtenerProductoPorId(Long id);
    public List<Producto> listarTodosLosProductos();
    public List<Producto> obtenerProductosConStockBajo(Integer umbral);
    public List<Producto> obtenerProductosProximosAVencer(Integer dias);
}
```

#### VentaService

**Responsabilidades:**
- Registro de ventas
- Cálculo de totales (subtotal, IGV, total)
- Actualización automática de stock
- Generación de boletas PDF
- Validación de stock disponible

```java
@Service
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final BoletaService boletaService;
    
    @Autowired
    public VentaService(VentaRepository ventaRepository,
                       ProductoRepository productoRepository,
                       InventarioService inventarioService,
                       BoletaService boletaService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
        this.boletaService = boletaService;
    }
    
    @Transactional
    public Venta registrarVenta(VentaDTO dto);
    public List<Venta> listarVentas();
    public List<Venta> listarVentasPorFecha(LocalDateTime inicio, LocalDateTime fin);
    public byte[] generarBoletaPDF(Long idVenta);
}
```

#### PedidoService

**Responsabilidades:**
- Registro de pedidos a proveedores
- Gestión de estados de pedidos
- Actualización automática de stock al recibir pedidos
- Validación de transiciones de estado

```java
@Service
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final InventarioService inventarioService;
    
    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,
                        InventarioService inventarioService) {
        this.pedidoRepository = pedidoRepository;
        this.inventarioService = inventarioService;
    }
    
    @Transactional
    public Pedido crearPedido(PedidoDTO dto);
    
    @Transactional
    public Pedido actualizarEstadoPedido(Long id, EstadoPedido nuevoEstado);
    
    public List<Pedido> listarPedidos();
    public List<Pedido> listarPedidosPorEstado(EstadoPedido estado);
}
```

#### InventarioService

**Responsabilidades:**
- Control centralizado del stock
- Registro de movimientos de inventario
- Generación de alertas
- Auditoría de cambios

```java
@Service
public class InventarioService {
    
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    
    @Autowired
    public InventarioService(ProductoRepository productoRepository,
                            MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }
    
    @Transactional
    public void disminuirStock(Long idProducto, Integer cantidad, String motivo);
    
    @Transactional
    public void aumentarStock(Long idProducto, Integer cantidad, String motivo);
    
    public List<AlertaInventario> obtenerAlertas();
    public List<MovimientoInventario> obtenerHistorialMovimientos(Long idProducto);
}
```

#### UsuarioService

**Responsabilidades:**
- CRUD de usuarios
- Cifrado de contraseñas
- Validación de credenciales
- Gestión de roles

```java
@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Usuario crearUsuario(UsuarioDTO dto);
    public Usuario actualizarUsuario(Long id, UsuarioDTO dto);
    public void eliminarUsuario(Long id);
    public List<Usuario> listarUsuarios();
    public Optional<Usuario> buscarPorNombreUsuario(String usuario);
}
```

### 4. Capa de Controladores (API REST)

Los controladores exponen endpoints REST y manejan las peticiones HTTP.

#### ProductoController

```java
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos();
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Long id);
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoDTO dto);
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, 
                                                       @Valid @RequestBody ProductoDTO dto);
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id);
    
    @GetMapping("/alertas/stock-bajo")
    public ResponseEntity<List<Producto>> obtenerStockBajo();
    
    @GetMapping("/alertas/proximos-vencer")
    public ResponseEntity<List<Producto>> obtenerProximosVencer();
}
```

#### VentaController

```java
@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    
    private final VentaService ventaService;
    
    @Autowired
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Venta> registrarVenta(@Valid @RequestBody VentaDTO dto);
    
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas();
    
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVenta(@PathVariable Long id);
    
    @GetMapping("/{id}/boleta")
    public ResponseEntity<byte[]> descargarBoleta(@PathVariable Long id);
}
```

#### PedidoController

```java
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    
    private final PedidoService pedidoService;
    
    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody PedidoDTO dto);
    
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long id, 
                                                   @RequestParam EstadoPedido estado);
    
    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos();
}
```

#### AuthController

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationService authService;
    
    @Autowired
    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout();
}
```

## Modelos de Datos

### Diagrama Entidad-Relación

```
┌─────────────┐         ┌─────────────┐
│  Categoria  │────┐    │  Proveedor  │
│─────────────│    │    │─────────────│
│ id_categoria│    │    │id_proveedor │
│ nombre      │    │    │ nombre      │
│ descripcion │    │    │ contacto    │
└─────────────┘    │    │ direccion   │
                   │    └─────────────┘
                   │           │
                   │           │ 1:N
                   │           ▼
┌─────────────┐    │    ┌─────────────┐
│  Producto   │◄───┘    │   Pedido    │
│─────────────│         │─────────────│
│ id_producto │         │ id_pedido   │
│ nombre      │         │ fecha       │
│ descripcion │         │ estado      │
│ precio      │         │id_proveedor │
│fecha_venc   │         └─────────────┘
│ stock       │                │
│id_categoria │                │ 1:N
└─────────────┘                ▼
      │                 ┌──────────────┐
      │                 │DetallePedido │
      │                 │──────────────│
      │                 │id_det_pedido │
      │                 │ id_pedido    │
      │                 │ id_producto  │
      │                 │ cantidad     │
      │                 └──────────────┘
      │
      │ N:M (via DetalleVenta)
      │
      ▼
┌─────────────┐         ┌─────────────┐
│   Venta     │         │   Cliente   │
│─────────────│         │─────────────│
│  id_venta   │         │ id_cliente  │
│  fecha      │◄────────│ nombre      │
│  total      │   N:1   │ documento   │
│ id_cliente  │         │ direccion   │
│ id_usuario  │         └─────────────┘
└─────────────┘
      │                 ┌─────────────┐
      │                 │   Usuario   │
      │                 │─────────────│
      └─────────────────│ id_usuario  │
              N:1       │ nombre      │
                        │ usuario     │
                        │ contrasena  │
                        │ rol         │
                        └─────────────┘
      │
      │ 1:N
      ▼
┌──────────────┐
│ DetalleVenta │
│──────────────│
│id_det_venta  │
│  id_venta    │
│ id_producto  │
│  cantidad    │
│especie_animal│
└──────────────┘
```

### Descripción de Tablas

**Producto:**
- Almacena información de medicamentos
- Relación N:1 con Categoría
- Campos clave: stock, fecha_vencimiento, precio

**Venta:**
- Registra transacciones de venta a clientes
- Relación N:1 con Cliente y Usuario
- Relación 1:N con DetalleVenta

**Pedido:**
- Registra pedidos de reabastecimiento a proveedores
- Relación N:1 con Proveedor
- Relación 1:N con DetallePedido
- Campo estado: PENDIENTE, ENVIADO, RECIBIDO

**Usuario:**
- Almacena usuarios del sistema
- Campo rol: ADMINISTRADOR, TRABAJADOR
- Contraseña cifrada con BCrypt

## DTOs (Data Transfer Objects)

Los DTOs se utilizan para transferir datos entre capas y validar entrada de usuarios.

```java
public class ProductoDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;
}

public class VentaDTO {
    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;
    
    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<DetalleVentaDTO> detalles;
}

public class DetalleVentaDTO {
    @NotNull
    private Long idProducto;
    
    @NotNull
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    private String especieAnimales;
}

public class PedidoDTO {
    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;
    
    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<DetallePedidoDTO> detalles;
}

public class LoginRequest {
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}

public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private Long idUsuario;
    private String nombre;
    private Rol rol;
}
```

## Seguridad

### Arquitectura de Seguridad

El sistema implementa seguridad mediante Spring Security con autenticación basada en JWT (JSON Web Tokens).

```
┌──────────────┐
│   Cliente    │
└──────────────┘
       │
       │ 1. POST /api/auth/login
       │    {usuario, contraseña}
       ▼
┌──────────────────────┐
│  AuthController      │
└──────────────────────┘
       │
       │ 2. Validar credenciales
       ▼
┌──────────────────────┐
│ AuthenticationService│
└──────────────────────┘
       │
       │ 3. Generar JWT
       ▼
┌──────────────────────┐
│    JwtService        │
└──────────────────────┘
       │
       │ 4. Retornar token
       ▼
┌──────────────┐
│   Cliente    │ (Almacena token)
└──────────────┘
       │
       │ 5. Peticiones subsecuentes
       │    Header: Authorization: Bearer <token>
       ▼
┌──────────────────────┐
│  JwtAuthFilter       │ (Valida token)
└──────────────────────┘
       │
       │ 6. Si válido, permite acceso
       ▼
┌──────────────────────┐
│   Controller         │
└──────────────────────┘
```

### Componentes de Seguridad

#### SecurityConfig

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/productos/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/ventas/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/pedidos/**").hasAnyRole("ADMINISTRADOR", "TRABAJADOR")
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/clientes/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/proveedores/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/reportes/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Factor de trabajo 12
    }
}
```

#### JwtService

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private Long expiration; // 24 horas
    
    public String generarToken(Usuario usuario);
    public String extraerUsuario(String token);
    public boolean validarToken(String token, UserDetails userDetails);
    public boolean tokenExpirado(String token);
}
```

#### JwtAuthFilter

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) {
        // 1. Extraer token del header Authorization
        // 2. Validar token
        // 3. Cargar usuario
        // 4. Establecer autenticación en SecurityContext
        // 5. Continuar con la cadena de filtros
    }
}
```

### Autorización por Roles

**Administrador:**
- Acceso completo a todos los módulos
- CRUD de usuarios, productos, clientes, proveedores
- Acceso a reportes y estadísticas
- Gestión de ventas y pedidos

**Trabajador:**
- Acceso limitado a operaciones diarias
- Registro de ventas
- Registro de pedidos
- Consulta de inventario
- Sin acceso a gestión de usuarios ni reportes

### Cifrado de Contraseñas

Las contraseñas se cifran usando BCrypt con factor de trabajo 12:

```java
// Al crear usuario
String contrasenaCifrada = passwordEncoder.encode(dto.getContrasena());
usuario.setContrasena(contrasenaCifrada);

// Al validar login
boolean coincide = passwordEncoder.matches(contrasenaIngresada, usuario.getContrasena());
```

## Inyección de Dependencias

### Concepto y Ventajas

La Inyección de Dependencias (DI) es un patrón de diseño que implementa el principio de Inversión de Dependencias (D de SOLID). En lugar de que una clase cree sus propias dependencias, estas se "inyectan" desde el exterior.

### Ejemplo: Sin Inyección de Dependencias (Acoplamiento Fuerte)

```java
// ❌ MAL: Acoplamiento fuerte, difícil de probar
public class VentaService {
    
    // La clase crea directamente sus dependencias
    private VentaRepository ventaRepository = new VentaRepositoryImpl();
    private ProductoRepository productoRepository = new ProductoRepositoryImpl();
    private InventarioService inventarioService = new InventarioService();
    
    public Venta registrarVenta(VentaDTO dto) {
        // Lógica de negocio...
    }
}

// Problemas:
// 1. No se puede cambiar la implementación sin modificar el código
// 2. Imposible hacer pruebas unitarias con mocks
// 3. Viola el principio Open/Closed de SOLID
// 4. Dificulta el mantenimiento
```

### Ejemplo: Con Inyección de Dependencias (Desacoplamiento)

```java
// ✅ BIEN: Desacoplamiento, fácil de probar y mantener
@Service
public class VentaService {
    
    // Las dependencias se declaran como interfaces
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    
    // Spring inyecta las dependencias mediante el constructor
    @Autowired
    public VentaService(VentaRepository ventaRepository,
                       ProductoRepository productoRepository,
                       InventarioService inventarioService) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }
    
    public Venta registrarVenta(VentaDTO dto) {
        // Lógica de negocio...
    }
}

// Ventajas:
// 1. Fácil cambiar implementaciones sin modificar el código
// 2. Permite pruebas unitarias con mocks
// 3. Cumple con el principio de Inversión de Dependencias
// 4. Facilita el mantenimiento y escalabilidad
// 5. Spring gestiona el ciclo de vida de los objetos
```

### Anotaciones de Spring para DI

```java
@Component  // Marca una clase como componente de Spring
@Service    // Especialización de @Component para capa de servicio
@Repository // Especialización de @Component para capa de datos
@Controller // Especialización de @Component para controladores web
@RestController // Combinación de @Controller + @ResponseBody

@Autowired  // Indica que Spring debe inyectar la dependencia
```

### Ejemplo Completo en RoxFarma

```java
// Repositorio
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Spring Data JPA genera la implementación automáticamente
}

// Servicio
@Service
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    
    // Inyección por constructor (recomendada)
    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    public Producto crearProducto(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        return productoRepository.save(producto);
    }
}

// Controlador
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    
    private final ProductoService productoService;
    
    // Spring inyecta ProductoService automáticamente
    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody ProductoDTO dto) {
        Producto producto = productoService.crearProducto(dto);
        return ResponseEntity.ok(producto);
    }
}
```

## Principios SOLID

### 1. Single Responsibility Principle (SRP)

**Principio:** Cada clase debe tener una única responsabilidad.

**Aplicación en RoxFarma:**

```java
// ✅ BIEN: Cada clase tiene una responsabilidad única

// Responsabilidad: Acceso a datos de productos
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}

// Responsabilidad: Lógica de negocio de productos
@Service
public class ProductoService {
    // Solo maneja operaciones de negocio de productos
}

// Responsabilidad: Manejo de peticiones HTTP de productos
@RestController
public class ProductoController {
    // Solo maneja requests/responses HTTP
}

// Responsabilidad: Generación de PDFs
@Service
public class BoletaService {
    // Solo se encarga de generar boletas PDF
}
```

### 2. Open/Closed Principle (OCP)

**Principio:** Las clases deben estar abiertas para extensión pero cerradas para modificación.

**Aplicación en RoxFarma:**

```java
// Interfaz base para generadores de reportes
public interface GeneradorReporte {
    byte[] generarReporte(List<Venta> ventas);
}

// Implementación para PDF
@Service
public class GeneradorReportePDF implements GeneradorReporte {
    @Override
    public byte[] generarReporte(List<Venta> ventas) {
        // Genera PDF
    }
}

// Implementación para Excel (extensión sin modificar código existente)
@Service
public class GeneradorReporteExcel implements GeneradorReporte {
    @Override
    public byte[] generarReporte(List<Venta> ventas) {
        // Genera Excel
    }
}

// El servicio usa la interfaz, no implementaciones concretas
@Service
public class ReporteService {
    private final GeneradorReporte generador;
    
    public ReporteService(GeneradorReporte generador) {
        this.generador = generador;
    }
}
```

### 3. Liskov Substitution Principle (LSP)

**Principio:** Los objetos de una clase derivada deben poder reemplazar objetos de la clase base sin alterar el comportamiento.

**Aplicación en RoxFarma:**

```java
// Clase base
public abstract class Documento {
    protected Long id;
    protected LocalDateTime fecha;
    
    public abstract BigDecimal calcularTotal();
}

// Subclase que respeta el contrato
public class Venta extends Documento {
    private List<DetalleVenta> detalles;
    
    @Override
    public BigDecimal calcularTotal() {
        return detalles.stream()
            .map(d -> d.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// Subclase que respeta el contrato
public class Pedido extends Documento {
    private List<DetallePedido> detalles;
    
    @Override
    public BigDecimal calcularTotal() {
        return detalles.stream()
            .map(d -> d.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### 4. Interface Segregation Principle (ISP)

**Principio:** Los clientes no deben depender de interfaces que no usan.

**Aplicación en RoxFarma:**

```java
// ❌ MAL: Interfaz grande que obliga a implementar métodos innecesarios
public interface OperacionesCompletas {
    void crear();
    void actualizar();
    void eliminar();
    void exportarPDF();
    void exportarExcel();
    void enviarEmail();
}

// ✅ BIEN: Interfaces segregadas
public interface Persistible {
    void guardar();
}

public interface Actualizable {
    void actualizar();
}

public interface Exportable {
    byte[] exportar();
}

// Las clases implementan solo lo que necesitan
@Service
public class ProductoService implements Persistible, Actualizable {
    @Override
    public void guardar() { }
    
    @Override
    public void actualizar() { }
}

@Service
public class ReporteService implements Exportable {
    @Override
    public byte[] exportar() { }
}
```

### 5. Dependency Inversion Principle (DIP)

**Principio:** Las clases de alto nivel no deben depender de clases de bajo nivel. Ambas deben depender de abstracciones.

**Aplicación en RoxFarma:**

```java
// ✅ BIEN: Dependencia de abstracciones (interfaces)

@Service
public class VentaService {
    
    // Depende de interfaces, no de implementaciones concretas
    private final VentaRepository ventaRepository;
    private final InventarioService inventarioService;
    private final NotificacionService notificacionService;
    
    @Autowired
    public VentaService(VentaRepository ventaRepository,
                       InventarioService inventarioService,
                       NotificacionService notificacionService) {
        this.ventaRepository = ventaRepository;
        this.inventarioService = inventarioService;
        this.notificacionService = notificacionService;
    }
}

// La implementación concreta se inyecta en tiempo de ejecución
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Spring Data JPA proporciona la implementación
}
```

## Manejo de Errores

### Estrategia de Manejo de Excepciones

El sistema implementa un manejo centralizado de excepciones usando `@ControllerAdvice`.

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errores.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Errores de validación",
            errores,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log del error completo
        log.error("Error no controlado", ex);
        
        // Respuesta genérica al cliente (sin exponer detalles internos)
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Ha ocurrido un error interno. Por favor contacte al administrador.",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### Excepciones Personalizadas

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}

public class EstadoPedidoInvalidoException extends RuntimeException {
    public EstadoPedidoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
```

### Validaciones en Servicios

```java
@Service
public class VentaService {
    
    @Transactional
    public Venta registrarVenta(VentaDTO dto) {
        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(dto.getIdCliente())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Cliente no encontrado con ID: " + dto.getIdCliente()
            ));
        
        // Validar stock disponible para cada producto
        for (DetalleVentaDTO detalle : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Producto no encontrado con ID: " + detalle.getIdProducto()
                ));
            
            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException(
                    String.format("Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                        producto.getNombre(), producto.getStock(), detalle.getCantidad())
                );
            }
        }
        
        // Procesar venta...
    }
}
```

## Logging y Auditoría

### Configuración de LogBack

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/roxfarma.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/roxfarma.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <logger name="com.roxfarma" level="DEBUG" />
</configuration>
```

### Uso de Logging en Servicios

```java
@Service
@Slf4j // Lombok genera automáticamente el logger
public class VentaService {
    
    @Transactional
    public Venta registrarVenta(VentaDTO dto) {
        log.info("Iniciando registro de venta para cliente ID: {}", dto.getIdCliente());
        
        try {
            // Lógica de negocio
            Venta venta = procesarVenta(dto);
            
            log.info("Venta registrada exitosamente. ID: {}, Total: {}", 
                venta.getIdVenta(), venta.getTotal());
            
            return venta;
        } catch (Exception e) {
            log.error("Error al registrar venta para cliente ID: {}", dto.getIdCliente(), e);
            throw e;
        }
    }
}
```

### Auditoría de Operaciones

```java
@Entity
@Table(name = "auditoria")
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String operacion; // CREATE, UPDATE, DELETE
    private String entidad;   // Producto, Venta, etc.
    private Long idEntidad;
    private String usuario;
    private LocalDateTime fecha;
    private String detalles;
}

@Service
public class AuditoriaService {
    
    private final AuditoriaRepository auditoriaRepository;
    
    public void registrarOperacion(String operacion, String entidad, 
                                   Long idEntidad, String usuario, String detalles) {
        Auditoria auditoria = new Auditoria();
        auditoria.setOperacion(operacion);
        auditoria.setEntidad(entidad);
        auditoria.setIdEntidad(idEntidad);
        auditoria.setUsuario(usuario);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setDetalles(detalles);
        
        auditoriaRepository.save(auditoria);
        log.info("Auditoría registrada: {} {} por {}", operacion, entidad, usuario);
    }
}
```

## Estrategia de Pruebas (TDD)

### Desarrollo Guiado por Pruebas

TDD (Test-Driven Development) sigue el ciclo: **Red → Green → Refactor**

1. **Red**: Escribir una prueba que falle
2. **Green**: Escribir el código mínimo para que pase
3. **Refactor**: Mejorar el código manteniendo las pruebas verdes

### Estructura de Pruebas

```
src/
├── main/
│   └── java/
│       └── com/roxfarma/
└── test/
    └── java/
        └── com/roxfarma/
            ├── service/
            │   ├── ProductoServiceTest.java
            │   ├── VentaServiceTest.java
            │   └── PedidoServiceTest.java
            ├── controller/
            │   ├── ProductoControllerTest.java
            │   └── VentaControllerTest.java
            └── repository/
                └── ProductoRepositoryTest.java
```

### Ejemplo: Pruebas Unitarias de Servicio

```java
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    
    @Mock
    private ProductoRepository productoRepository;
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    @InjectMocks
    private ProductoService productoService;
    
    @Test
    @DisplayName("Debe crear un producto correctamente")
    void debeCrearProductoCorrectamente() {
        // Arrange (Preparar)
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Paracetamol");
        dto.setPrecio(new BigDecimal("15.50"));
        dto.setStock(100);
        dto.setIdCategoria(1L);
        
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Analgésicos");
        
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act (Actuar)
        Producto resultado = productoService.crearProducto(dto);
        
        // Assert (Verificar)
        assertNotNull(resultado);
        assertEquals("Paracetamol", resultado.getNombre());
        assertEquals(new BigDecimal("15.50"), resultado.getPrecio());
        assertEquals(100, resultado.getStock());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando la categoría no existe")
    void debeLanzarExcepcionCuandoCategoriaNoExiste() {
        // Arrange
        ProductoDTO dto = new ProductoDTO();
        dto.setIdCategoria(999L);
        
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.crearProducto(dto);
        });
        
        verify(productoRepository, never()).save(any(Producto.class));
    }
    
    @Test
    @DisplayName("Debe obtener productos con stock bajo")
    void debeObtenerProductosConStockBajo() {
        // Arrange
        List<Producto> productosStockBajo = Arrays.asList(
            crearProducto(1L, "Producto A", 5),
            crearProducto(2L, "Producto B", 3)
        );
        
        when(productoRepository.findByStockLessThan(10)).thenReturn(productosStockBajo);
        
        // Act
        List<Producto> resultado = productoService.obtenerProductosConStockBajo(10);
        
        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> p.getStock() < 10));
    }
    
    private Producto crearProducto(Long id, String nombre, Integer stock) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        producto.setStock(stock);
        return producto;
    }
}
```

### Ejemplo: Pruebas de Integración de Repositorio

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductoRepositoryTest {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Test
    @DisplayName("Debe encontrar productos con stock menor al umbral")
    void debeEncontrarProductosConStockBajo() {
        // Arrange
        Categoria categoria = new Categoria();
        categoria.setNombre("Test");
        categoria = categoriaRepository.save(categoria);
        
        Producto p1 = crearYGuardarProducto("Producto 1", 5, categoria);
        Producto p2 = crearYGuardarProducto("Producto 2", 15, categoria);
        Producto p3 = crearYGuardarProducto("Producto 3", 8, categoria);
        
        // Act
        List<Producto> resultado = productoRepository.findByStockLessThan(10);
        
        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(p -> p.getNombre().equals("Producto 1")));
        assertTrue(resultado.stream().anyMatch(p -> p.getNombre().equals("Producto 3")));
    }
    
    private Producto crearYGuardarProducto(String nombre, Integer stock, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setStock(stock);
        producto.setPrecio(new BigDecimal("10.00"));
        producto.setCategoria(categoria);
        producto.setFechaVencimiento(LocalDate.now().plusMonths(6));
        return productoRepository.save(producto);
    }
}
```

### Ejemplo: Pruebas de Controlador

```java
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductoService productoService;
    
    @MockBean
    private JwtService jwtService;
    
    @Test
    @DisplayName("GET /api/productos debe retornar lista de productos")
    @WithMockUser(roles = "ADMINISTRADOR")
    void debeRetornarListaDeProductos() throws Exception {
        // Arrange
        List<Producto> productos = Arrays.asList(
            crearProducto(1L, "Producto 1"),
            crearProducto(2L, "Producto 2")
        );
        
        when(productoService.listarTodosLosProductos()).thenReturn(productos);
        
        // Act & Assert
        mockMvc.perform(get("/api/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nombre").value("Producto 1"))
            .andExpect(jsonPath("$[1].nombre").value("Producto 2"));
    }
    
    @Test
    @DisplayName("POST /api/productos debe crear producto")
    @WithMockUser(roles = "ADMINISTRADOR")
    void debeCrearProducto() throws Exception {
        // Arrange
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Nuevo Producto");
        dto.setPrecio(new BigDecimal("25.00"));
        
        Producto productoCreado = crearProducto(1L, "Nuevo Producto");
        when(productoService.crearProducto(any(ProductoDTO.class))).thenReturn(productoCreado);
        
        // Act & Assert
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Nuevo Producto"));
    }
    
    private Producto crearProducto(Long id, String nombre) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        return producto;
    }
    
    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
```

### Ejemplo: Prueba de Lógica de Negocio Compleja (Venta)

```java
@ExtendWith(MockitoExtension.class)
class VentaServiceTest {
    
    @Mock
    private VentaRepository ventaRepository;
    
    @Mock
    private ProductoRepository productoRepository;
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private InventarioService inventarioService;
    
    @InjectMocks
    private VentaService ventaService;
    
    @Test
    @DisplayName("Debe registrar venta y actualizar stock correctamente")
    void debeRegistrarVentaYActualizarStock() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Paracetamol");
        producto.setPrecio(new BigDecimal("15.00"));
        producto.setStock(50);
        
        DetalleVentaDTO detalleDTO = new DetalleVentaDTO();
        detalleDTO.setIdProducto(1L);
        detalleDTO.setCantidad(5);
        
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setIdCliente(1L);
        ventaDTO.setDetalles(Arrays.asList(detalleDTO));
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Venta resultado = ventaService.registrarVenta(ventaDTO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getDetalles().size());
        assertEquals(new BigDecimal("75.00"), resultado.getTotal()); // 15 * 5
        
        // Verificar que se llamó al servicio de inventario para disminuir stock
        verify(inventarioService, times(1)).disminuirStock(1L, 5, "VENTA");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando el stock es insuficiente")
    void debeLanzarExcepcionCuandoStockInsuficiente() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Paracetamol");
        producto.setStock(3); // Stock insuficiente
        
        DetalleVentaDTO detalleDTO = new DetalleVentaDTO();
        detalleDTO.setIdProducto(1L);
        detalleDTO.setCantidad(5); // Solicita más de lo disponible
        
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setIdCliente(1L);
        ventaDTO.setDetalles(Arrays.asList(detalleDTO));
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        // Act & Assert
        StockInsuficienteException exception = assertThrows(
            StockInsuficienteException.class,
            () -> ventaService.registrarVenta(ventaDTO)
        );
        
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(ventaRepository, never()).save(any(Venta.class));
        verify(inventarioService, never()).disminuirStock(anyLong(), anyInt(), anyString());
    }
    
    @Test
    @DisplayName("Debe calcular correctamente el total con IGV")
    void debeCalcularTotalConIGV() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        
        Producto p1 = crearProducto(1L, "Producto 1", new BigDecimal("100.00"), 50);
        Producto p2 = crearProducto(2L, "Producto 2", new BigDecimal("50.00"), 30);
        
        DetalleVentaDTO detalle1 = crearDetalleDTO(1L, 2); // 100 * 2 = 200
        DetalleVentaDTO detalle2 = crearDetalleDTO(2L, 3); // 50 * 3 = 150
        // Subtotal: 350
        // IGV (18%): 63
        // Total: 413
        
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setIdCliente(1L);
        ventaDTO.setDetalles(Arrays.asList(detalle1, detalle2));
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Venta resultado = ventaService.registrarVenta(ventaDTO);
        
        // Assert
        assertEquals(new BigDecimal("413.00"), resultado.getTotal());
    }
    
    private Producto crearProducto(Long id, String nombre, BigDecimal precio, Integer stock) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        return producto;
    }
    
    private DetalleVentaDTO crearDetalleDTO(Long idProducto, Integer cantidad) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setIdProducto(idProducto);
        dto.setCantidad(cantidad);
        return dto;
    }
}
```

## Generación de Boletas PDF

### Servicio de Generación de PDFs

```java
@Service
public class BoletaService {
    
    private static final String LOGO_PATH = "classpath:static/images/logo-roxfarma.png";
    
    public byte[] generarBoletaPDF(Venta venta) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            
            // Encabezado con logo y datos de RoxFarma
            agregarEncabezado(document);
            
            // Información de la venta
            agregarInformacionVenta(document, venta);
            
            // Tabla de productos
            agregarTablaProductos(document, venta);
            
            // Totales
            agregarTotales(document, venta);
            
            // Pie de página
            agregarPiePagina(document);
            
            document.close();
            return baos.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Error al generar boleta PDF", e);
        }
    }
    
    private void agregarEncabezado(Document document) {
        Paragraph titulo = new Paragraph("LABORATORIO ROXFARMA")
            .setFontSize(18)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);
        
        Paragraph subtitulo = new Paragraph("RUC: 20123456789\nAv. Principal 123, Lima, Perú\nTelefono: (01) 234-5678")
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(subtitulo);
        
        document.add(new Paragraph("\n"));
        
        Paragraph boletaTitulo = new Paragraph("BOLETA DE VENTA ELECTRÓNICA")
            .setFontSize(14)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER);
        document.add(boletaTitulo);
        
        document.add(new Paragraph("\n"));
    }
    
    private void agregarInformacionVenta(Document document, Venta venta) {
        document.add(new Paragraph("Nº Boleta: " + String.format("B001-%08d", venta.getIdVenta())));
        document.add(new Paragraph("Fecha: " + venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        document.add(new Paragraph("Cliente: " + venta.getCliente().getNombre()));
        document.add(new Paragraph("Documento: " + venta.getCliente().getDocumento()));
        document.add(new Paragraph("Atendido por: " + venta.getUsuario().getNombre()));
        document.add(new Paragraph("\n"));
    }
    
    private void agregarTablaProductos(Document document, Venta venta) {
        Table table = new Table(new float[]{4, 1, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("P. Unit.").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Subtotal").setBold()));
        
        // Detalles
        for (DetalleVenta detalle : venta.getDetalles()) {
            table.addCell(detalle.getProducto().getNombre());
            table.addCell(String.valueOf(detalle.getCantidad()));
            table.addCell("S/ " + detalle.getPrecio().toString());
            
            BigDecimal subtotal = detalle.getPrecio()
                .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            table.addCell("S/ " + subtotal.toString());
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private void agregarTotales(Document document, Venta venta) {
        BigDecimal subtotal = venta.getTotal().divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
        BigDecimal igv = venta.getTotal().subtract(subtotal);
        
        document.add(new Paragraph("Subtotal: S/ " + subtotal.toString())
            .setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("IGV (18%): S/ " + igv.toString())
            .setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("TOTAL: S/ " + venta.getTotal().toString())
            .setFontSize(14)
            .setBold()
            .setTextAlignment(TextAlignment.RIGHT));
    }
    
    private void agregarPiePagina(Document document) {
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Gracias por su compra")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10));
    }
}
```

## Diseño del Frontend

### Arquitectura de Componentes React

```
src/
├── components/
│   ├── common/
│   │   ├── Navbar.tsx
│   │   ├── Sidebar.tsx
│   │   ├── Alert.tsx
│   │   └── Loading.tsx
│   ├── auth/
│   │   └── Login.tsx
│   ├── productos/
│   │   ├── ProductoList.tsx
│   │   ├── ProductoForm.tsx
│   │   └── ProductoCard.tsx
│   ├── ventas/
│   │   ├── VentaForm.tsx
│   │   ├── VentaList.tsx
│   │   └── BoletaViewer.tsx
│   ├── pedidos/
│   │   ├── PedidoForm.tsx
│   │   ├── PedidoList.tsx
│   │   └── PedidoEstado.tsx
│   ├── inventario/
│   │   ├── Dashboard.tsx
│   │   ├── AlertasStock.tsx
│   │   └── AlertasVencimiento.tsx
│   └── reportes/
│       └── ReporteVentas.tsx
├── services/
│   ├── api.ts
│   ├── authService.ts
│   ├── productoService.ts
│   ├── ventaService.ts
│   └── pedidoService.ts
├── context/
│   └── AuthContext.tsx
├── hooks/
│   ├── useAuth.ts
│   └── useApi.ts
└── utils/
    ├── formatters.ts
    └── validators.ts
```

### Servicio de API (Axios)

```typescript
// services/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Contexto de Autenticación

```typescript
// context/AuthContext.tsx
import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';

interface AuthContextType {
  usuario: Usuario | null;
  login: (usuario: string, contrasena: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  hasRole: (rol: string) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC = ({ children }) => {
  const [usuario, setUsuario] = useState<Usuario | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // Validar token y cargar usuario
      authService.validarToken().then(setUsuario).catch(() => logout());
    }
  }, []);

  const login = async (usuario: string, contrasena: string) => {
    const response = await authService.login(usuario, contrasena);
    localStorage.setItem('token', response.token);
    setUsuario(response.usuario);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUsuario(null);
  };

  const hasRole = (rol: string) => {
    return usuario?.rol === rol;
  };

  return (
    <AuthContext.Provider value={{
      usuario,
      login,
      logout,
      isAuthenticated: !!usuario,
      hasRole
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};
```

### Componente de Login

```typescript
// components/auth/Login.tsx
import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export const Login: React.FC = () => {
  const [usuario, setUsuario] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    try {
      await login(usuario, contrasena);
      navigate('/dashboard');
    } catch (err) {
      setError('Usuario o contraseña incorrectos');
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit}>
        <h2>RoxFarma - Iniciar Sesión</h2>
        
        {error && <div className="alert alert-danger">{error}</div>}
        
        <div className="form-group">
          <label>Usuario</label>
          <input
            type="text"
            value={usuario}
            onChange={(e) => setUsuario(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group">
          <label>Contraseña</label>
          <input
            type="password"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            required
          />
        </div>
        
        <button type="submit">Ingresar</button>
      </form>
    </div>
  );
};
```

### Dashboard de Inventario

```typescript
// components/inventario/Dashboard.tsx
import React, { useEffect, useState } from 'react';
import { productoService } from '../../services/productoService';
import { AlertasStock } from './AlertasStock';
import { AlertasVencimiento } from './AlertasVencimiento';

export const Dashboard: React.FC = () => {
  const [estadisticas, setEstadisticas] = useState({
    totalProductos: 0,
    stockTotal: 0,
    alertasStockBajo: 0,
    alertasVencimiento: 0
  });

  useEffect(() => {
    cargarEstadisticas();
  }, []);

  const cargarEstadisticas = async () => {
    const stats = await productoService.obtenerEstadisticas();
    setEstadisticas(stats);
  };

  return (
    <div className="dashboard">
      <h1>Dashboard de Inventario</h1>
      
      <div className="estadisticas-grid">
        <div className="card">
          <h3>Total Productos</h3>
          <p className="numero">{estadisticas.totalProductos}</p>
        </div>
        
        <div className="card">
          <h3>Stock Total</h3>
          <p className="numero">{estadisticas.stockTotal}</p>
        </div>
        
        <div className="card alerta">
          <h3>Alertas Stock Bajo</h3>
          <p className="numero">{estadisticas.alertasStockBajo}</p>
        </div>
        
        <div className="card alerta">
          <h3>Próximos a Vencer</h3>
          <p className="numero">{estadisticas.alertasVencimiento}</p>
        </div>
      </div>
      
      <div className="alertas-container">
        <AlertasStock />
        <AlertasVencimiento />
      </div>
    </div>
  );
};
```

## Configuración de Base de Datos

### Script SQL de Creación

```sql
-- Crear base de datos
CREATE DATABASE IF NOT EXISTS roxfarma_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE roxfarma_db;

-- Tabla Categoria
CREATE TABLE categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Producto
CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    id_categoria BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    INDEX idx_stock (stock),
    INDEX idx_fecha_vencimiento (fecha_vencimiento)
);

-- Tabla Proveedor
CREATE TABLE proveedor (
    id_proveedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    contacto VARCHAR(100),
    direccion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Cliente
CREATE TABLE cliente (
    id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Usuario
CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'TRABAJADOR') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla Pedido
CREATE TABLE pedido (
    id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('PENDIENTE', 'ENVIADO', 'RECIBIDO') NOT NULL DEFAULT 'PENDIENTE',
    id_proveedor BIGINT NOT NULL,
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor),
    INDEX idx_estado (estado)
);

-- Tabla Detalle_Pedido
CREATE TABLE detalle_pedido (
    id_detalle_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- Tabla Venta
CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_cliente BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    INDEX idx_fecha (fecha)
);

-- Tabla Detalle_Venta
CREATE TABLE detalle_venta (
    id_detalle_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- Tabla Auditoria
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operacion VARCHAR(50) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    id_entidad BIGINT,
    usuario VARCHAR(50) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalles TEXT,
    INDEX idx_fecha (fecha),
    INDEX idx_entidad (entidad, id_entidad)
);

-- Insertar datos iniciales
INSERT INTO categoria (nombre, descripcion) VALUES
('Analgésicos', 'Medicamentos para aliviar el dolor'),
('Antibióticos', 'Medicamentos para combatir infecciones bacterianas'),
('Antiinflamatorios', 'Medicamentos para reducir la inflamación');

INSERT INTO usuario (nombre, usuario, contrasena, rol) VALUES
('Administrador Sistema', 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.eLb4W6', 'ADMINISTRADOR'),
('Juan Pérez', 'jperez', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.eLb4W6', 'TRABAJADOR');
-- Contraseña: password123
```

### Configuración application.properties

```properties
# Configuración del servidor
server.port=8080

# Configuración de base de datos MySQL (XAMPP)
spring.datasource.url=jdbc:mysql://localhost:3306/roxfarma_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Configuración JWT
jwt.secret=roxfarma_secret_key_2024_muy_segura_y_larga
jwt.expiration=86400000

# Configuración de logging
logging.level.com.roxfarma=DEBUG
logging.level.org.springframework.security=DEBUG
logging.file.name=logs/roxfarma.log

# Configuración de archivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Resumen del Diseño

Este diseño técnico proporciona una arquitectura sólida y escalable para el sistema RoxFarma, implementando:

1. **Arquitectura Multicapa**: Separación clara de responsabilidades entre presentación, lógica de negocio y acceso a datos
2. **Patrones de Diseño**: MVC, DAO, DTO, Repository
3. **Principios SOLID**: Aplicados consistentemente en toda la arquitectura
4. **Inyección de Dependencias**: Uso extensivo de Spring DI para desacoplamiento
5. **Seguridad Robusta**: Autenticación JWT, autorización por roles, cifrado de contraseñas
6. **TDD**: Estrategia completa de pruebas unitarias e integración
7. **Manejo de Errores**: Excepciones personalizadas y manejo centralizado
8. **Auditoría y Logging**: Trazabilidad completa de operaciones
9. **Frontend Moderno**: React con TypeScript, componentes reutilizables
10. **Base de Datos Normalizada**: Modelo relacional optimizado con índices

El diseño está preparado para ser implementado siguiendo las mejores prácticas de desarrollo de software académico y profesional.
