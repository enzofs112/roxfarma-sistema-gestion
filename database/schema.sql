-- ================================================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - SISTEMA ROXFARMA
-- Universidad Tecnológica del Perú (UTP)
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
-- Contraseña para ambos: password123
-- Hash generado con BCrypt (factor 12): $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.eLb4W6
INSERT INTO usuario (nombre, usuario, contrasena, rol, activo) VALUES
('Administrador Sistema', 'admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.eLb4W6', 'ADMINISTRADOR', TRUE),
('Juan Pérez Torres', 'jperez', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.eLb4W6', 'TRABAJADOR', TRUE),
('María García López', 'mgarcia', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIR.eLb4W6', 'TRABAJADOR', TRUE);

-- Insertar productos de ejemplo
INSERT INTO producto (nombre, descripcion, precio, fecha_vencimiento, stock, id_categoria) VALUES
-- Analgésicos
('Paracetamol 500mg x 100 tabletas', 'Analgésico y antipirético de uso común', 15.50, '2025-12-31', 100, 1),
('Paracetamol 500mg x 20 tabletas', 'Analgésico y antipirético presentación pequeña', 5.00, '2025-11-30', 200, 1),
('Tramadol 50mg x 20 cápsulas', 'Analgésico opioide para dolor moderado a severo', 45.00, '2025-10-31', 30, 1),

-- Antibióticos
('Amoxicilina 500mg x 24 cápsulas', 'Antibiótico de amplio espectro', 35.00, '2025-10-31', 50, 2),
('Azitromicina 500mg x 6 tabletas', 'Antibiótico macrólido', 42.00, '2025-09-30', 40, 2),
('Ciprofloxacino 500mg x 10 tabletas', 'Antibiótico fluoroquinolona', 38.00, '2025-11-15', 35, 2),

-- Antiinflamatorios
('Ibuprofeno 400mg x 20 tabletas', 'Antiinflamatorio no esteroideo', 22.00, '2025-11-30', 80, 3),
('Diclofenaco 50mg x 20 tabletas', 'Antiinflamatorio y analgésico', 28.00, '2025-10-15', 60, 3),
('Naproxeno 500mg x 20 tabletas', 'Antiinflamatorio de acción prolongada', 32.00, '2025-12-20', 45, 3),

-- Antipiréticos
('Dipirona 500mg x 20 tabletas', 'Antipirético y analgésico', 18.00, '2025-11-25', 90, 4),

-- Vitaminas
('Vitamina C 1000mg x 30 tabletas', 'Suplemento vitamínico antioxidante', 18.00, '2026-06-30', 120, 5),
('Complejo B x 30 cápsulas', 'Suplemento de vitaminas del complejo B', 25.00, '2026-05-31', 85, 5),
('Vitamina D3 2000 UI x 60 cápsulas', 'Suplemento de vitamina D', 35.00, '2026-08-31', 70, 5),

-- Antihistamínicos
('Loratadina 10mg x 10 tabletas', 'Antihistamínico para alergias', 12.00, '2025-12-15', 95, 6),
('Cetirizina 10mg x 10 tabletas', 'Antihistamínico de segunda generación', 15.00, '2025-11-20', 88, 6),

-- Productos con stock bajo (para alertas)
('Metformina 850mg x 30 tabletas', 'Antidiabético oral', 28.00, '2025-10-30', 8, 8),
('Enalapril 10mg x 30 tabletas', 'Antihipertensivo', 22.00, '2025-09-25', 5, 7),

-- Productos próximos a vencer (para alertas)
('Omeprazol 20mg x 14 cápsulas', 'Inhibidor de bomba de protones', 18.00, '2024-11-15', 40, 1),
('Losartán 50mg x 30 tabletas', 'Antihipertensivo', 32.00, '2024-11-20', 35, 7);

-- Insertar clientes (farmacias y boticas)
INSERT INTO cliente (nombre, documento, direccion) VALUES
('Farmacia San Juan EIRL', '20123456789', 'Av. Principal 123, San Juan de Lurigancho, Lima'),
('Botica Salud Total SAC', '20987654321', 'Jr. Los Olivos 456, Los Olivos, Lima'),
('Farmacia Central del Perú SA', '20456789123', 'Av. Arequipa 789, Miraflores, Lima'),
('Botica Mifarma - Sucursal Norte', '20789123456', 'Av. Túpac Amaru 1500, Independencia, Lima'),
('Farmacia Universal EIRL', '20321654987', 'Jr. Huallaga 234, Cercado de Lima'),
('Botica Arcángel SAC', '20147258369', 'Av. Universitaria 890, San Miguel, Lima'),
('Farmacia del Pueblo', '20963852741', 'Av. Venezuela 567, Breña, Lima'),
('Botica Inkafarma - Local 45', '20852963741', 'Av. Javier Prado 2300, San Isidro, Lima');

-- Insertar proveedores
INSERT INTO proveedor (nombre, contacto, direccion) VALUES
('Laboratorios Perú SAC', '(01) 234-5678 / ventas@labperu.com', 'Av. Industrial 100, Ate, Lima'),
('Distribuidora Médica del Sur SA', '(01) 345-6789 / contacto@medsur.com', 'Jr. Comercio 200, Villa El Salvador, Lima'),
('Importadora Farma Internacional EIRL', '(01) 456-7890 / importaciones@farmaint.com', 'Av. República 300, La Victoria, Lima'),
('Droguería Nacional SAC', '(01) 567-8901 / ventas@drogueria.com', 'Av. Colonial 450, Callao'),
('Química Suiza del Perú SA', '(01) 678-9012 / pedidos@quimicasuiza.com', 'Av. Argentina 1200, Callao'),
('Representaciones Farmacéuticas ABC', '(01) 789-0123 / info@repfarma.com', 'Jr. Puno 890, Cercado de Lima');

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
SELECT 'Proveedores insertados:', COUNT(*) FROM proveedor;

-- ================================================================================
-- FIN DEL SCRIPT
-- ================================================================================
