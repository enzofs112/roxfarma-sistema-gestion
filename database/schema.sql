-- ================================================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - SISTEMA ROXFARMA
-- Curso: Integrador de Sistemas
-- ================================================================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS roxfarma_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE roxfarma_db;

-- ================================================================================
-- TABLA: CATEGORIA
-- Almacena las categorías de productos (Analgésicos, Antibióticos, etc.)
-- ================================================================================
CREATE TABLE categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: PRODUCTO
-- Almacena información de medicamentos y productos farmacéuticos
-- ================================================================================
CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    presentacion VARCHAR(100) COMMENT 'Ej: 500mg, 100ml, Caja x 20',
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    id_categoria BIGINT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    INDEX idx_stock (stock),
    INDEX idx_fecha_vencimiento (fecha_vencimiento),
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: PROVEEDOR
-- Almacena información de proveedores de medicamentos
-- ================================================================================
CREATE TABLE proveedor (
    id_proveedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    contacto VARCHAR(100),
    direccion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: CLIENTE
-- Almacena información de clientes (farmacias y boticas)
-- ================================================================================
CREATE TABLE cliente (
    id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre),
    INDEX idx_documento (documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: USUARIO
-- Almacena usuarios del sistema (Administrador y Trabajador)
-- ================================================================================
CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL COMMENT 'Contraseña cifrada con BCrypt',
    rol ENUM('ADMINISTRADOR', 'TRABAJADOR') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_usuario (usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: PEDIDO
-- Almacena pedidos de reabastecimiento a proveedores
-- ================================================================================
CREATE TABLE pedido (
    id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('PENDIENTE', 'ENVIADO', 'RECIBIDO') NOT NULL DEFAULT 'PENDIENTE',
    id_proveedor BIGINT NOT NULL,
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor),
    INDEX idx_estado (estado),
    INDEX idx_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: DETALLE_PEDIDO
-- Almacena los productos incluidos en cada pedido
-- ================================================================================
CREATE TABLE detalle_pedido (
    id_detalle_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    INDEX idx_pedido (id_pedido),
    INDEX idx_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: VENTA
-- Almacena las ventas realizadas a clientes
-- ================================================================================
CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_cliente BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL COMMENT 'Usuario que registró la venta',
    total DECIMAL(10, 2) NOT NULL COMMENT 'Total incluyendo IGV',
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    INDEX idx_fecha (fecha),
    INDEX idx_cliente (id_cliente),
    INDEX idx_usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: DETALLE_VENTA
-- Almacena los productos incluidos en cada venta
-- ================================================================================
CREATE TABLE detalle_venta (
    id_detalle_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL COMMENT 'Precio unitario al momento de la venta',
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    INDEX idx_venta (id_venta),
    INDEX idx_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- TABLA: AUDITORIA
-- Registra todas las operaciones importantes del sistema para trazabilidad
-- ================================================================================
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operacion VARCHAR(50) NOT NULL COMMENT 'CREATE, UPDATE, DELETE',
    entidad VARCHAR(50) NOT NULL COMMENT 'Nombre de la entidad afectada',
    id_entidad BIGINT COMMENT 'ID del registro afectado',
    usuario VARCHAR(50) NOT NULL COMMENT 'Usuario que realizó la operación',
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalles TEXT COMMENT 'Información adicional de la operación',
    INDEX idx_fecha (fecha),
    INDEX idx_entidad (entidad, id_entidad),
    INDEX idx_usuario (usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================================
-- DATOS INICIALES
-- ================================================================================

-- Insertar categorías de productos
INSERT INTO categoria (nombre, descripcion) VALUES
('Analgésicos', 'Medicamentos para aliviar el dolor'),
('Antibióticos', 'Medicamentos para combatir infecciones bacterianas'),
('Antiinflamatorios', 'Medicamentos para reducir la inflamación'),
('Antipiréticos', 'Medicamentos para reducir la fiebre'),
('Vitaminas y Suplementos', 'Suplementos vitamínicos y minerales'),
('Antihistamínicos', 'Medicamentos para alergias'),
('Antihipertensivos', 'Medicamentos para controlar la presión arterial'),
('Antidiabéticos', 'Medicamentos para controlar la diabetes');

-- Insertar usuarios del sistema
-- Contraseña para todos: password123
-- Hash generado con BCrypt (factor 12): $2a$12$WiIu637iHMAKVy25BxrV6eIFt9LyR.C4rEXW7xhF/U5ObsN4aC9uW
INSERT INTO usuario (nombre, usuario, contrasena, rol, activo) VALUES
('Administrador Sistema', 'admin', '$2a$12$WiIu637iHMAKVy25BxrV6eIFt9LyR.C4rEXW7xhF/U5ObsN4aC9uW', 'ADMINISTRADOR', TRUE),
('Juan Pérez Torres', 'jperez', '$2a$12$WiIu637iHMAKVy25BxrV6eIFt9LyR.C4rEXW7xhF/U5ObsN4aC9uW', 'TRABAJADOR', TRUE),
('María García López', 'mgarcia', '$2a$12$WiIu637iHMAKVy25BxrV6eIFt9LyR.C4rEXW7xhF/U5ObsN4aC9uW', 'TRABAJADOR', TRUE);

-- Insertar productos farmacéuticos realistas del mercado peruano
INSERT INTO producto (nombre, presentacion, descripcion, precio, fecha_vencimiento, stock, id_categoria) VALUES
-- Analgésicos (medicamentos más vendidos en Perú)
('Paracetamol', '500mg x 100 tabletas', 'Analgésico y antipirético de uso común - Marca genérica', 12.50, '2025-12-31', 150, 1),
('Paracetamol', '500mg x 20 tabletas', 'Analgésico y antipirético - Presentación familiar', 4.50, '2025-11-30', 250, 1),
('Paracetamol', '120mg/5ml x 60ml jarabe', 'Analgésico pediátrico sabor fresa', 8.90, '2025-10-15', 80, 1),
('Ibuprofeno', '400mg x 20 tabletas', 'Analgésico antiinflamatorio - Marca genérica', 18.50, '2025-11-30', 120, 1),
('Ibuprofeno', '100mg/5ml x 120ml suspensión', 'Analgésico pediátrico sabor naranja', 12.90, '2025-09-20', 65, 1),
('Tramadol', '50mg x 20 cápsulas', 'Analgésico opioide para dolor moderado a severo', 42.00, '2025-10-31', 35, 1),
('Ketorolaco', '10mg x 10 tabletas', 'Analgésico potente de corta duración', 15.50, '2025-12-10', 45, 1),

-- Antibióticos (más recetados en Perú)
('Amoxicilina', '500mg x 24 cápsulas', 'Antibiótico de amplio espectro - Primera línea', 28.00, '2025-10-31', 90, 2),
('Amoxicilina', '250mg/5ml x 60ml suspensión', 'Antibiótico pediátrico sabor cereza', 18.50, '2025-09-15', 70, 2),
('Amoxicilina + Ácido Clavulánico', '875mg/125mg x 14 tabletas', 'Antibiótico de amplio espectro reforzado', 45.00, '2025-11-20', 55, 2),
('Azitromicina', '500mg x 3 tabletas', 'Antibiótico macrólido - Tratamiento corto', 25.00, '2025-09-30', 80, 2),
('Azitromicina', '500mg x 6 tabletas', 'Antibiótico macrólido - Presentación estándar', 38.00, '2025-10-25', 60, 2),
('Ciprofloxacino', '500mg x 10 tabletas', 'Antibiótico fluoroquinolona para infecciones urinarias', 32.00, '2025-11-15', 50, 2),
('Claritromicina', '500mg x 14 tabletas', 'Antibiótico macrólido para infecciones respiratorias', 52.00, '2025-12-05', 40, 2),

-- Antiinflamatorios (AINES más usados)
('Diclofenaco', '50mg x 20 tabletas', 'Antiinflamatorio no esteroideo potente', 22.00, '2025-10-15', 95, 3),
('Diclofenaco', '75mg/3ml x 6 ampollas', 'Antiinflamatorio inyectable', 35.00, '2025-11-10', 30, 3),
('Naproxeno', '550mg x 20 tabletas', 'Antiinflamatorio de acción prolongada', 28.00, '2025-12-20', 70, 3),
('Meloxicam', '15mg x 10 tabletas', 'Antiinflamatorio selectivo COX-2', 24.00, '2025-11-25', 55, 3),
('Nimesulida', '100mg x 20 tabletas', 'Antiinflamatorio y analgésico', 26.00, '2025-10-30', 60, 3),

-- Antipiréticos
('Dipirona (Metamizol)', '500mg x 20 tabletas', 'Antipirético y analgésico potente', 14.50, '2025-11-25', 110, 4),
('Dipirona (Metamizol)', '1g/2ml x 6 ampollas', 'Antipirético inyectable', 28.00, '2025-10-20', 45, 4),

-- Vitaminas y Suplementos (más vendidos en Perú)
('Vitamina C', '1000mg x 30 tabletas efervescentes', 'Suplemento vitamínico antioxidante sabor naranja', 15.00, '2026-06-30', 140, 5),
('Vitamina C', '500mg x 100 tabletas', 'Suplemento vitamínico económico', 22.00, '2026-05-15', 95, 5),
('Complejo B', '30 cápsulas', 'Vitaminas B1, B6, B12 para el sistema nervioso', 28.00, '2026-05-31', 100, 5),
('Vitamina D3', '2000 UI x 60 cápsulas', 'Suplemento de vitamina D para huesos', 38.00, '2026-08-31', 75, 5),
('Calcio + Vitamina D', '600mg/400UI x 60 tabletas', 'Suplemento para salud ósea', 32.00, '2026-07-20', 65, 5),
('Multivitamínico', '30 tabletas', 'Complejo multivitamínico y minerales', 35.00, '2026-09-15', 80, 5),
('Hierro + Ácido Fólico', '60mg/400mcg x 30 tabletas', 'Suplemento para anemia', 18.00, '2026-04-30', 70, 5),

-- Antihistamínicos (para alergias)
('Loratadina', '10mg x 10 tabletas', 'Antihistamínico no sedante para alergias', 10.00, '2025-12-15', 120, 6),
('Loratadina', '5mg/5ml x 60ml jarabe', 'Antihistamínico pediátrico', 12.50, '2025-11-10', 55, 6),
('Cetirizina', '10mg x 10 tabletas', 'Antihistamínico de segunda generación', 12.00, '2025-11-20', 105, 6),
('Desloratadina', '5mg x 10 tabletas', 'Antihistamínico de tercera generación', 18.00, '2025-12-25', 60, 6),
('Clorfeniramina', '4mg x 20 tabletas', 'Antihistamínico clásico', 8.00, '2025-10-30', 85, 6),

-- Antihipertensivos (para presión alta)
('Enalapril', '10mg x 30 tabletas', 'Antihipertensivo IECA primera línea', 18.00, '2025-09-25', 7, 7),
('Enalapril', '20mg x 30 tabletas', 'Antihipertensivo IECA dosis alta', 24.00, '2025-10-15', 45, 7),
('Losartán', '50mg x 30 tabletas', 'Antihipertensivo ARA-II', 28.00, '2025-11-20', 38, 7),
('Losartán', '100mg x 30 tabletas', 'Antihipertensivo ARA-II dosis alta', 38.00, '2025-11-30', 50, 7),
('Amlodipino', '5mg x 30 tabletas', 'Antihipertensivo calcioantagonista', 15.00, '2025-12-10', 75, 7),
('Hidroclorotiazida', '25mg x 30 tabletas', 'Diurético antihipertensivo', 12.00, '2025-11-05', 60, 7),

-- Antidiabéticos (para diabetes)
('Metformina', '850mg x 30 tabletas', 'Antidiabético oral primera línea', 22.00, '2025-10-30', 9, 8),
('Metformina', '500mg x 60 tabletas', 'Antidiabético oral dosis estándar', 28.00, '2025-11-15', 55, 8),
('Glibenclamida', '5mg x 30 tabletas', 'Antidiabético oral sulfonilurea', 15.00, '2025-12-20', 40, 8),
('Glimepirida', '2mg x 30 tabletas', 'Antidiabético oral sulfonilurea moderna', 32.00, '2025-11-25', 35, 8),

-- Productos próximos a vencer (para alertas)
('Omeprazol', '20mg x 14 cápsulas', 'Inhibidor de bomba de protones para gastritis', 16.00, '2025-01-15', 45, 1),
('Ranitidina', '150mg x 20 tabletas', 'Antiácido bloqueador H2', 14.00, '2025-01-20', 40, 1);

-- Insertar clientes (farmacias y boticas reales de Los Olivos y alrededores)
INSERT INTO cliente (nombre, documento, direccion) VALUES
('Botica Farmaolivos EIRL', '20601234567', 'Av. Alfredo Mendiola 5890, Los Olivos, Lima'),
('Farmacia San Martín SAC', '20601234568', 'Av. Carlos Izaguirre 456, Los Olivos, Lima'),
('Botica Virgen de Fátima', '20601234569', 'Jr. Las Palmeras 234, Los Olivos, Lima'),
('Farmacia Nuevo Mundo EIRL', '20601234570', 'Av. Universitaria 2100, Los Olivos, Lima'),
('Botica Santa Rosa de Lima', '20601234571', 'Av. Antúnez de Mayolo 890, Los Olivos, Lima'),
('Farmacia Salud y Vida SAC', '20601234572', 'Av. Naranjal 1234, Los Olivos, Lima'),
('Botica Mifarma Los Olivos', '20100152356', 'Av. Alfredo Mendiola 6200, Los Olivos, Lima'),
('Farmacia Inkafarma Mega Plaza', '20100070970', 'Av. Alfredo Mendiola 3698, Independencia, Lima'),
('Botica Arcángel Los Olivos', '20601234573', 'Av. Tomás Valle 567, Los Olivos, Lima'),
('Farmacia Universal Norte', '20601234574', 'Av. Canta Callao 1890, San Martín de Porres, Lima'),
('Botica Fasa Los Olivos', '20100047218', 'Av. Carlos Izaguirre 233, Los Olivos, Lima'),
('Farmacia del Pueblo Norte', '20601234575', 'Av. Universitaria 1850, Los Olivos, Lima');

-- Insertar proveedores (distribuidoras farmacéuticas reales del Perú)
INSERT INTO proveedor (nombre, contacto, direccion) VALUES
('Albis S.A. - Distribuidora Farmacéutica', '(01) 615-5555 / ventas@albis.com.pe', 'Av. Separadora Industrial 1947, Ate, Lima'),
('Drokasa Perú S.A.', '(01) 311-3200 / pedidos@drokasa.com.pe', 'Av. Nicolás Arriola 740, La Victoria, Lima'),
('Química Suiza S.A.', '(01) 419-1919 / ventas@quimicasuiza.com.pe', 'Av. Argentina 3093, Callao'),
('Droguería Peruana S.A.', '(01) 203-5000 / contacto@drogper.com.pe', 'Av. Nicolás Arriola 314, La Victoria, Lima'),
('Perufarma S.A.', '(01) 711-0200 / ventas@perufarma.com.pe', 'Av. Los Frutales 220, Ate, Lima'),
('Digemid - Distribuidora General', '(01) 631-4300 / info@digemid.com.pe', 'Av. Parque de las Leyendas 240, San Miguel, Lima'),
('Medifarma S.A.', '(01) 332-3232 / pedidos@medifarma.com.pe', 'Jr. Río de Janeiro 274, Jesús María, Lima'),
('Representaciones Médicas del Perú', '(01) 422-8080 / ventas@repmedperu.com', 'Av. Colonial 1694, Callao');

-- ================================================================================
-- DATOS DE EJEMPLO: PEDIDOS
-- ================================================================================

-- Insertar pedidos de ejemplo con fechas válidas
INSERT INTO pedido (fecha, estado, id_proveedor) VALUES
('2025-01-05 10:30:00', 'RECIBIDO', 1),
('2025-01-10 14:15:00', 'RECIBIDO', 2),
('2025-01-15 09:45:00', 'ENVIADO', 3),
('2025-01-20 11:20:00', 'PENDIENTE', 4),
('2025-01-25 16:00:00', 'PENDIENTE', 5);

-- Insertar detalles de pedidos
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES
-- Pedido 1 (RECIBIDO)
(1, 1, 50),
(1, 4, 30),
(1, 8, 40),
-- Pedido 2 (RECIBIDO)
(2, 11, 25),
(2, 14, 35),
(2, 20, 20),
-- Pedido 3 (ENVIADO)
(3, 23, 45),
(3, 26, 30),
-- Pedido 4 (PENDIENTE)
(4, 30, 50),
(4, 33, 40),
-- Pedido 5 (PENDIENTE)
(5, 36, 35),
(5, 39, 25);

-- ================================================================================
-- DATOS DE EJEMPLO: VENTAS
-- ================================================================================

-- Insertar ventas de ejemplo con fechas válidas
INSERT INTO venta (fecha, id_cliente, id_usuario, total) VALUES
('2025-01-05 09:15:00', 1, 2, 85.50),
('2025-01-08 11:30:00', 2, 2, 142.80),
('2025-01-10 14:45:00', 3, 3, 67.20),
('2025-01-12 10:00:00', 4, 2, 198.50),
('2025-01-15 15:20:00', 5, 3, 124.30),
('2025-01-18 09:45:00', 6, 2, 89.90),
('2025-01-20 13:10:00', 7, 3, 156.70),
('2025-01-22 16:30:00', 8, 2, 73.50);

-- Insertar detalles de ventas
INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio) VALUES
-- Venta 1
(1, 1, 2, 12.50),
(1, 4, 3, 18.50),
(1, 23, 1, 15.00),
-- Venta 2
(2, 8, 2, 28.00),
(2, 11, 1, 25.00),
(2, 14, 3, 22.00),
(2, 20, 1, 24.00),
-- Venta 3
(3, 2, 5, 4.50),
(3, 26, 2, 15.00),
(3, 30, 1, 10.00),
-- Venta 4
(4, 5, 3, 12.90),
(4, 9, 2, 18.50),
(4, 12, 1, 38.00),
(4, 15, 2, 22.00),
(4, 24, 3, 15.00),
-- Venta 5
(5, 16, 2, 22.00),
(5, 21, 1, 14.50),
(5, 27, 3, 28.00),
-- Venta 6
(6, 3, 2, 8.90),
(6, 7, 1, 15.50),
(6, 28, 2, 32.00),
-- Venta 7
(7, 10, 1, 45.00),
(7, 13, 2, 32.00),
(7, 17, 1, 35.00),
-- Venta 8
(8, 6, 1, 42.00),
(8, 19, 1, 28.00);

-- ================================================================================
-- VERIFICACIÓN DE DATOS
-- ================================================================================

-- Mostrar resumen de datos insertados
SELECT 'Categorías insertadas:' AS Resumen, COUNT(*) AS Total FROM categoria
UNION ALL
SELECT 'Usuarios insertados:', COUNT(*) FROM usuario
UNION ALL
SELECT 'Productos insertados:', COUNT(*) FROM producto
UNION ALL
SELECT 'Clientes insertados:', COUNT(*) FROM cliente
UNION ALL
SELECT 'Proveedores insertados:', COUNT(*) FROM proveedor
UNION ALL
SELECT 'Pedidos insertados:', COUNT(*) FROM pedido
UNION ALL
SELECT 'Ventas insertadas:', COUNT(*) FROM venta;

-- ================================================================================
-- FIN DEL SCRIPT
-- ================================================================================
