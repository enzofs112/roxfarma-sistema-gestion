import api from './api';
import { Venta, VentaDTO } from '../types';

export const ventaService = {
  registrarVenta: async (venta: VentaDTO): Promise<Venta> => {
    const response = await api.post('/api/ventas', venta);
    return response.data;
  },

  listarVentas: async (): Promise<Venta[]> => {
    const response = await api.get('/api/ventas');
    return response.data;
  },

  obtenerVenta: async (id: number): Promise<Venta> => {
    const response = await api.get(`/api/ventas/${id}`);
    return response.data;
  },

  descargarBoleta: async (id: number): Promise<Blob> => {
    const response = await api.get(`/api/ventas/${id}/boleta`, {
      responseType: 'blob'
    });
    return response.data;
  }
};
