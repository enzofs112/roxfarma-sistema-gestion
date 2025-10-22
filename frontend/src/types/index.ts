export enum Rol {
  ADMINISTRADOR = 'ADMINISTRADOR',
  TRABAJADOR = 'TRABAJADOR'
}

export enum EstadoPedido {
  PENDIENTE = 'PENDIENTE',
  ENVIADO = 'ENVIADO',
  RECIBIDO = 'RECIBIDO'
}

export interface Usuario {
  idUsuario: number;
  nombre: string;
  usuario: string;
  rol: Rol;
  activo: boolean;
  fechaCreacion: string;
}

export interface Producto {
  idProducto: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  fechaVencimiento: string;
  stock: number;
  categoria: Categoria;
  fechaCreacion: string;
}

export interface Categoria {
  idCategoria: number;
  nombre: string;
  descripcion?: string;
  fechaCreacion: string;
}

export interface Cliente {
  idCliente: number;
  nombre: string;
  documento: string;
  direccion?: string;
  fechaCreacion: string;
}

export interface Venta {
  idVenta: number;
  fecha: string;
  cliente: Cliente;
  usuario: Usuario;
  total: number;
  detalles: DetalleVenta[];
}

export interface DetalleVenta {
  idDetalleVenta: number;
  producto: Producto;
  cantidad: number;
  precio: number;
}

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