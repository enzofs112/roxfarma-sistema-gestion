import api from './api';
import { Producto } from '../types';

export interface Alertas {
  stockBajo: Producto[];
  proximosVencer: Producto[];
  cantidadStockBajo: number;
  cantidadProximosVencer: number;
}

export interface Estadisticas {
  totalProductos: number;
  stockTotal: number;
  alertasStockBajo: number;
  alertasVencimiento: number;
}

export const dashboardService = {
  obtenerAlertas: async (): Promise<Alertas> => {
    const response = await api.get('/api/dashboard/alertas');
    return response.data;
  },

  obtenerEstadisticas: async (): Promise<Estadisticas> => {
    const response = await api.get('/api/dashboard/estadisticas');
    return response.data;
  }
};
