import api from './api';
import { Proveedor, ProveedorDTO } from '../types';

export const proveedorService = {
  listarProveedores: async (): Promise<Proveedor[]> => {
    const response = await api.get('/api/proveedores');
    return response.data;
  },

  obtenerProveedor: async (id: number): Promise<Proveedor> => {
    const response = await api.get(`/api/proveedores/${id}`);
    return response.data;
  },

  crearProveedor: async (proveedor: ProveedorDTO): Promise<Proveedor> => {
    const response = await api.post('/api/proveedores', proveedor);
    return response.data;
  },

  actualizarProveedor: async (id: number, proveedor: ProveedorDTO): Promise<Proveedor> => {
    const response = await api.put(`/api/proveedores/${id}`, proveedor);
    return response.data;
  },

  eliminarProveedor: async (id: number): Promise<void> => {
    await api.delete(`/api/proveedores/${id}`);
  }
};
