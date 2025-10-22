# Documento de Requerimientos - Sistema RoxFarma

## Introducción

RoxFarma es un laboratorio farmacéutico y distribuidora peruana fundada en 1986 que actualmente gestiona sus operaciones mediante hojas de Excel, lo que genera ineficiencias operativas. Este proyecto académico tiene como objetivo desarrollar una aplicación web que automatice y centralice la gestión de pedidos, inventario y facturación.

El sistema permitirá a RoxFarma registrar ventas a farmacias (clientes), gestionar pedidos de reabastecimiento a proveedores, controlar el inventario en tiempo real (entradas, salidas, vencimientos), administrar clientes, proveedores y usuarios internos, generar boletas PDF, y mostrar alertas de stock bajo y productos próximos a vencer.

**Contexto del Negocio:**
- RoxFarma vende medicamentos a farmacias/boticas → genera ventas con boletas → disminuye stock
- RoxFarma se reabastece de proveedores → genera pedidos de compra → aumenta stock al recibir
- Usuarios internos (Administrador/Trabajador) registran todas las operaciones

**Stack Tecnológico:**
- Backend: Java con Spring Boot, MySQL, arquitectura MVC + DAO
- Frontend: React/Angular/Thymeleaf (interfaz funcional y minimalista)
- Principios: SOLID, TDD, Inyección de Dependencias
- Seguridad: Spring Security con JWT o sesiones seguras

## Requerimientos

### Requerimiento 1: Autenticación y Autorización de Usuarios

**Historia de Usuario:** Como usuario del sistema (Administrador o Trabajador), quiero iniciar sesión de forma segura con mi usuario y contraseña, para que solo personal autorizado pueda acceder al sistema y realizar operaciones según mi rol.

#### Criterios de Aceptación

1. WHEN un usuario ingresa credenciales válidas THEN el sistema SHALL autenticar al usuario y generar un token JWT o sesión segura
2. WHEN un usuario ingresa credenciales inválidas THEN el sistema SHALL mostrar un mensaje de error y denegar el acceso
3. WHEN un usuario con rol Administrador inicia sesión THEN el sistema SHALL otorgar acceso completo a todos los módulos (usuarios, productos, clientes, proveedores, ventas, pedidos, reportes)
4. WHEN un usuario con rol Trabajador inicia sesión THEN el sistema SHALL otorgar acceso limitado solo a ventas, pedidos e inventario
5. WHEN un usuario intenta acceder a un módulo sin permisos THEN el sistema SHALL denegar el acceso y mostrar mensaje de autorización insuficiente
6. WHEN un token JWT expira THEN el sistema SHALL solicitar nueva autenticación
7. IF las contraseñas se almacenan en la base de datos THEN el sistema SHALL cifrarlas usando BCrypt o algoritmo similar

### Requerimiento 2: Gestión de Productos (CRUD)

**Historia de Usuario:** Como Administrador, quiero crear, leer, actualizar y eliminar productos del catálogo, para mantener actualizada la información de medicamentos disponibles para la venta.

#### Criterios de Aceptación

1. WHEN el Administrador crea un nuevo producto THEN el sistema SHALL validar que todos los campos obligatorios estén completos (nombre, precio, stock, categoría, fecha_vencimiento)
2. WHEN el Administrador guarda un producto válido THEN el sistema SHALL almacenarlo en la tabla Producto con id_producto generado automáticamente
3. WHEN el Administrador consulta productos THEN el sistema SHALL mostrar una lista con nombre, descripción, precio, stock, categoría y fecha de vencimiento
4. WHEN el Administrador actualiza un producto THEN el sistema SHALL modificar solo los campos editados y mantener la integridad referencial
5. WHEN el Administrador intenta eliminar un producto con ventas o pedidos asociados THEN el sistema SHALL mostrar advertencia y solicitar confirmación
6. IF un producto tiene stock igual a 0 THEN el sistema SHALL marcarlo visualmente como "Sin Stock"
7. WHEN se lista productos THEN el sistema SHALL permitir filtrar por categoría, nombre o estado de stock

### Requerimiento 3: Control Automático de Inventario

**Historia de Usuario:** Como usuario del sistema, quiero que el stock de productos se actualice automáticamente al registrar ventas y pedidos, para mantener un control preciso del inventario en tiempo real.

#### Criterios de Aceptación

1. WHEN se registra una venta con productos THEN el sistema SHALL disminuir automáticamente el stock de cada producto vendido según la cantidad en Detalle_Venta
2. WHEN se marca un pedido como "Recibido" THEN el sistema SHALL aumentar automáticamente el stock de cada producto según la cantidad en Detalle_Pedido
3. WHEN el stock de un producto cae por debajo de un umbral mínimo (ej: 10 unidades) THEN el sistema SHALL generar una alerta de "Stock Bajo"
4. WHEN la fecha actual está a 30 días o menos de la fecha_vencimiento de un producto THEN el sistema SHALL generar una alerta de "Próximo a Vencer"
5. IF una venta intenta registrar una cantidad mayor al stock disponible THEN el sistema SHALL rechazar la operación y mostrar mensaje de error
6. WHEN se actualiza el stock THEN el sistema SHALL registrar la operación en un log de auditoría con fecha, usuario y tipo de movimiento
7. WHEN se consulta el inventario THEN el sistema SHALL mostrar stock actual, alertas activas y movimientos recientes

### Requerimiento 4: Registro y Gestión de Ventas

**Historia de Usuario:** Como Trabajador o Administrador, quiero registrar ventas a clientes (farmacias), para documentar las transacciones comerciales y generar boletas automáticamente.

#### Criterios de Aceptación

1. WHEN se inicia una nueva venta THEN el sistema SHALL solicitar selección de cliente y productos con cantidades
2. WHEN se agregan productos a la venta THEN el sistema SHALL calcular automáticamente el subtotal, IGV (18%) y total
3. WHEN se confirma una venta THEN el sistema SHALL crear registros en las tablas Venta y Detalle_Venta con fecha actual, usuario logueado y cliente seleccionado
4. WHEN se confirma una venta THEN el sistema SHALL disminuir automáticamente el stock de los productos vendidos
5. WHEN se completa una venta THEN el sistema SHALL generar automáticamente una boleta en formato PDF con datos de RoxFarma, cliente, productos, cantidades, precios, subtotal, IGV y total
6. WHEN se genera la boleta PDF THEN el sistema SHALL permitir descargarla o imprimirla
7. IF un producto en la venta no tiene stock suficiente THEN el sistema SHALL mostrar error antes de confirmar
8. WHEN se consulta el historial de ventas THEN el sistema SHALL mostrar fecha, cliente, usuario que registró, total y opción de ver/descargar boleta

### Requerimiento 5: Gestión de Pedidos a Proveedores

**Historia de Usuario:** Como Trabajador o Administrador, quiero registrar pedidos de reabastecimiento a proveedores y controlar su estado, para asegurar el suministro continuo de productos.

#### Criterios de Aceptación

1. WHEN se crea un nuevo pedido THEN el sistema SHALL solicitar selección de proveedor y productos con cantidades
2. WHEN se guarda un pedido THEN el sistema SHALL crear registros en las tablas Pedido y Detalle_Pedido con estado inicial "Pendiente"
3. WHEN se actualiza el estado de un pedido THEN el sistema SHALL permitir cambiar entre "Pendiente", "Enviado" y "Recibido"
4. WHEN un pedido cambia a estado "Recibido" THEN el sistema SHALL aumentar automáticamente el stock de los productos según las cantidades del pedido
5. WHEN se consulta el historial de pedidos THEN el sistema SHALL mostrar fecha, proveedor, estado y total de productos
6. WHEN se visualiza un pedido THEN el sistema SHALL mostrar el detalle completo con productos, cantidades y proveedor
7. IF un pedido está en estado "Recibido" THEN el sistema SHALL impedir modificaciones posteriores

### Requerimiento 6: Gestión de Clientes

**Historia de Usuario:** Como Administrador, quiero gestionar la información de clientes (farmacias/boticas), para mantener un registro actualizado de mis compradores.

#### Criterios de Aceptación

1. WHEN el Administrador crea un nuevo cliente THEN el sistema SHALL validar que nombre, documento y dirección sean obligatorios
2. WHEN se guarda un cliente THEN el sistema SHALL almacenarlo en la tabla Cliente con id_cliente generado automáticamente
3. WHEN se consulta clientes THEN el sistema SHALL mostrar lista con nombre, documento y dirección
4. WHEN se actualiza un cliente THEN el sistema SHALL modificar solo los campos editados
5. WHEN se intenta eliminar un cliente con ventas asociadas THEN el sistema SHALL mostrar advertencia y solicitar confirmación
6. WHEN se visualiza un cliente THEN el sistema SHALL mostrar su historial de ventas asociadas

### Requerimiento 7: Gestión de Proveedores

**Historia de Usuario:** Como Administrador, quiero gestionar la información de proveedores, para mantener un registro actualizado de mis suministradores.

#### Criterios de Aceptación

1. WHEN el Administrador crea un nuevo proveedor THEN el sistema SHALL validar que nombre, contacto y dirección sean obligatorios
2. WHEN se guarda un proveedor THEN el sistema SHALL almacenarlo en la tabla Proveedor con id_proveedor generado automáticamente
3. WHEN se consulta proveedores THEN el sistema SHALL mostrar lista con nombre, contacto y dirección
4. WHEN se actualiza un proveedor THEN el sistema SHALL modificar solo los campos editados
5. WHEN se intenta eliminar un proveedor con pedidos asociados THEN el sistema SHALL mostrar advertencia y solicitar confirmación
6. WHEN se visualiza un proveedor THEN el sistema SHALL mostrar su historial de pedidos asociados

### Requerimiento 8: Gestión de Usuarios Internos

**Historia de Usuario:** Como Administrador, quiero crear, modificar y eliminar usuarios del sistema, para controlar quién tiene acceso y con qué permisos.

#### Criterios de Aceptación

1. WHEN el Administrador crea un nuevo usuario THEN el sistema SHALL solicitar nombre, usuario, contraseña y rol (Administrador o Trabajador)
2. WHEN se guarda un usuario THEN el sistema SHALL cifrar la contraseña antes de almacenarla en la tabla Usuario
3. WHEN se consulta usuarios THEN el sistema SHALL mostrar nombre, usuario y rol (sin mostrar contraseñas)
4. WHEN se actualiza un usuario THEN el sistema SHALL permitir cambiar nombre, usuario, contraseña y rol
5. IF se cambia la contraseña THEN el sistema SHALL cifrarla nuevamente antes de guardar
6. WHEN se intenta eliminar un usuario con ventas registradas THEN el sistema SHALL mostrar advertencia
7. IF un Trabajador intenta acceder al módulo de usuarios THEN el sistema SHALL denegar el acceso

### Requerimiento 9: Dashboard de Inventario y Alertas

**Historia de Usuario:** Como usuario del sistema, quiero visualizar un dashboard con el estado del inventario y alertas automáticas, para tomar decisiones informadas sobre reabastecimiento.

#### Criterios de Aceptación

1. WHEN se accede al dashboard THEN el sistema SHALL mostrar resumen de productos totales, stock total y alertas activas
2. WHEN existen productos con stock bajo (menos de 10 unidades) THEN el sistema SHALL mostrarlos en una sección de "Alertas de Stock Bajo"
3. WHEN existen productos próximos a vencer (30 días o menos) THEN el sistema SHALL mostrarlos en una sección de "Próximos a Vencer"
4. WHEN se visualiza una alerta THEN el sistema SHALL mostrar nombre del producto, stock actual o fecha de vencimiento
5. WHEN se hace clic en una alerta THEN el sistema SHALL permitir navegar al detalle del producto
6. WHEN se actualiza el inventario THEN el sistema SHALL refrescar automáticamente las alertas

### Requerimiento 10: Reportes de Ventas e Inventario

**Historia de Usuario:** Como Administrador, quiero generar reportes de ventas e inventario, para analizar el desempeño del negocio y tomar decisiones estratégicas.

#### Criterios de Aceptación

1. WHEN el Administrador solicita un reporte de ventas THEN el sistema SHALL permitir filtrar por rango de fechas
2. WHEN se genera el reporte de ventas THEN el sistema SHALL mostrar total de ventas, cantidad de transacciones, productos más vendidos y ventas por cliente
3. WHEN el Administrador solicita un reporte de inventario THEN el sistema SHALL mostrar stock actual por producto, productos con stock bajo y productos próximos a vencer
4. WHEN se genera un reporte THEN el sistema SHALL permitir exportarlo en formato PDF o Excel
5. IF un Trabajador intenta acceder a reportes THEN el sistema SHALL denegar el acceso

### Requerimiento 11: Gestión de Categorías de Productos

**Historia de Usuario:** Como Administrador, quiero gestionar categorías de productos, para organizar el catálogo de medicamentos de forma lógica.

#### Criterios de Aceptación

1. WHEN el Administrador crea una nueva categoría THEN el sistema SHALL validar que nombre y descripción sean obligatorios
2. WHEN se guarda una categoría THEN el sistema SHALL almacenarla en la tabla Categoría con id_categoria generado automáticamente
3. WHEN se consulta categorías THEN el sistema SHALL mostrar lista con nombre y descripción
4. WHEN se actualiza una categoría THEN el sistema SHALL modificar solo los campos editados
5. WHEN se intenta eliminar una categoría con productos asociados THEN el sistema SHALL mostrar advertencia y solicitar confirmación
6. WHEN se asigna una categoría a un producto THEN el sistema SHALL validar que la categoría exista

### Requerimiento 12: Seguridad y Auditoría

**Historia de Usuario:** Como Administrador, quiero que el sistema registre todas las operaciones críticas y garantice la seguridad de los datos, para cumplir con estándares de trazabilidad y protección de información.

#### Criterios de Aceptación

1. WHEN se realiza cualquier operación de creación, actualización o eliminación THEN el sistema SHALL registrar en un log la fecha, hora, usuario y tipo de operación
2. WHEN se intenta acceder a un endpoint sin autenticación THEN el sistema SHALL retornar error 401 (No Autorizado)
3. WHEN se intenta acceder a un recurso sin permisos THEN el sistema SHALL retornar error 403 (Prohibido)
4. WHEN se transmiten datos sensibles THEN el sistema SHALL usar HTTPS para cifrar la comunicación
5. WHEN se almacenan contraseñas THEN el sistema SHALL usar BCrypt con factor de trabajo mínimo de 10
6. WHEN se detecta un intento de inyección SQL THEN el sistema SHALL usar consultas preparadas (PreparedStatement) para prevenirlo
7. WHEN se consultan logs de auditoría THEN el sistema SHALL mostrar historial de operaciones con filtros por fecha, usuario y tipo

### Requerimiento 13: Interfaz de Usuario Funcional

**Historia de Usuario:** Como usuario del sistema, quiero una interfaz web intuitiva y funcional, para realizar mis tareas de forma eficiente sin necesidad de capacitación extensa.

#### Criterios de Aceptación

1. WHEN se accede al sistema THEN el sistema SHALL mostrar una pantalla de login clara con campos de usuario y contraseña
2. WHEN un usuario inicia sesión THEN el sistema SHALL mostrar un menú de navegación con las opciones disponibles según su rol
3. WHEN se navega entre módulos THEN el sistema SHALL mantener consistencia visual en botones, formularios y tablas
4. WHEN se muestran listados THEN el sistema SHALL incluir opciones de búsqueda, filtrado y paginación
5. WHEN se realizan operaciones THEN el sistema SHALL mostrar mensajes de confirmación o error claros
6. WHEN se completa una acción exitosa THEN el sistema SHALL mostrar notificación de éxito
7. IF ocurre un error THEN el sistema SHALL mostrar mensaje descriptivo sin exponer detalles técnicos sensibles

### Requerimiento 14: Rendimiento y Disponibilidad

**Historia de Usuario:** Como usuario del sistema, quiero que la aplicación responda rápidamente y esté disponible durante el horario laboral, para no interrumpir las operaciones del negocio.

#### Criterios de Aceptación

1. WHEN se realiza una consulta a la base de datos THEN el sistema SHALL responder en menos de 2 segundos para operaciones simples
2. WHEN múltiples usuarios acceden simultáneamente THEN el sistema SHALL soportar al menos 20 usuarios concurrentes sin degradación significativa
3. WHEN se genera una boleta PDF THEN el sistema SHALL completar la operación en menos de 5 segundos
4. WHEN ocurre un error en el servidor THEN el sistema SHALL registrarlo en LogBack y mostrar mensaje genérico al usuario
5. IF la base de datos no está disponible THEN el sistema SHALL mostrar mensaje de error de conexión
6. WHEN se realizan operaciones de escritura THEN el sistema SHALL usar transacciones para garantizar consistencia de datos
