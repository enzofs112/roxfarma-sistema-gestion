export enum Rol {
  ADMINISTRADOR = 'ADMINISTRADOR',
  TRABAJADOR = 'TRABAJADOR'
}

export enum EstadoPedido {
  PENDIENTE = 'PENDIENTE',
  ENVIADO = 'ENVIADO',
  RECIBIDO = 'RECIBIDO'
}

// Interfaces de entidades
export interface Usuario {
  idUsuario: number;
  nombre: string;
  usuario: string;
  rol: Rol;
  activo: boolean;
  fechaCreacion: string;
}

export interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion?: string;
  fechaCreacion: string;
}

export interface Producto {
  idProducto: number;
  nombre: string;
  presentacion?: string;
  descripcion?: string;
  precio: number;
  fechaVencimiento: string;
  stock: number;
  categoria: Categoria;
  fechaCreacion: string;
  fechaActualizacion: string;
}

export interface Cliente {
  idCliente: number;
  nombre: string;
  documento: string;
  direccion?: string;
  fechaCreacion: string;
}

export interface Proveedor {
  idProveedor: number;
  nombre: string;
  contacto?: string;
  direccion?: string;
  fechaCreacion: string;
}

export interface DetalleVenta {
  idDetalleVenta: number;
  producto: Producto;
  cantidad: number;
  precio: number;
}

export interface Venta {
  idVenta: number;
  fecha: string;
  cliente: Cliente;
  usuario: Usuario;
  total: number;
  detalles: DetalleVenta[];
}

export interface DetallePedido {
  idDetallePedido: number;
  producto: Producto;
  cantidad: number;
}

export interface Pedido {
  idPedido: number;
  fecha: string;
  estado: EstadoPedido;
  proveedor: Proveedor;
  detalles: DetallePedido[];
}

// DTOs para formularios
export interface LoginRequest {
  usuario: string;
  contrasena: string;
}

export interface AuthResponse {
  token: string;
  tipo: string;
  idUsuario: number;
  nombre: string;
  rol: Rol;
}

export interface ProductoDTO {
  nombre: string;
  presentacion?: string;
  descripcion?: string;
  precio: number;
  fechaVencimiento: string;
  stock: number;
  idCategoria: number;
}

export interface CategoriaDTO {
  nombre: string;
  descripcion?: string;
}

export interface ClienteDTO {
  nombre: string;
  documento: string;
  direccion?: string;
}

export interface ProveedorDTO {
  nombre: string;
  contacto?: string;
  direccion?: string;
}

export interface UsuarioDTO {
  nombre: string;
  usuario: string;
  contrasena: string;
  rol: Rol;
}

export interface DetalleVentaDTO {
  idProducto: number;
  cantidad: number;
  precio: number;
}

export interface VentaDTO {
  idCliente: number;
  detalles: DetalleVentaDTO[];
}

export interface DetallePedidoDTO {
  idProducto: number;
  cantidad: number;
}

export interface PedidoDTO {
  idProveedor: number;
  detalles: DetallePedidoDTO[];
}

// Tipos para respuestas de error
export interface ErrorResponse {
  status: number;
  mensaje: string;
  detalles?: { [key: string]: string };
  timestamp: string;
}

// Tipos para dashboard
export interface EstadisticasDashboard {
  totalProductos: number;
  stockTotal: number;
  alertasStockBajo: number;
  alertasVencimiento: number;
}

export interface AlertasInventario {
  stockBajo: Producto[];
  proximosVencer: Producto[];
}
