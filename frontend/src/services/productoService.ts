import api from './api';
import { Producto, ProductoDTO } from '../types';

export const productoService = {
  listarProductos: async (): Promise<Producto[]> => {
    const response = await api.get('/api/productos');
    return response.data;
  },

  obtenerProducto: async (id: number): Promise<Producto> => {
    const response = await api.get(`/api/productos/${id}`);
    return response.data;
  },

  crearProducto: async (producto: ProductoDTO): Promise<Producto> => {
    const response = await api.post('/api/productos', producto);
    return response.data;
  },

  actualizarProducto: async (id: number, producto: ProductoDTO): Promise<Producto> => {
    const response = await api.put(`/api/productos/${id}`, producto);
    return response.data;
  },

  eliminarProducto: async (id: number): Promise<void> => {
    await api.delete(`/api/productos/${id}`);
  },

  obtenerStockBajo: async (): Promise<Producto[]> => {
    const response = await api.get('/api/productos/alertas/stock-bajo');
    return response.data;
  },

  obtenerProximosVencer: async (): Promise<Producto[]> => {
    const response = await api.get('/api/productos/alertas/proximos-vencer');
    return response.data;
  }
};
