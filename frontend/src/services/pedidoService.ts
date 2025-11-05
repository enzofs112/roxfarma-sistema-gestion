import api from './api';
import { Pedido, PedidoDTO, EstadoPedido } from '../types';

export const pedidoService = {
  crearPedido: async (pedido: PedidoDTO): Promise<Pedido> => {
    const response = await api.post('/api/pedidos', pedido);
    return response.data;
  },

  listarPedidos: async (): Promise<Pedido[]> => {
    const response = await api.get('/api/pedidos');
    return response.data;
  },

  obtenerPedido: async (id: number): Promise<Pedido> => {
    const response = await api.get(`/api/pedidos/${id}`);
    return response.data;
  },

  actualizarEstado: async (id: number, estado: EstadoPedido): Promise<Pedido> => {
    const response = await api.put(`/api/pedidos/${id}/estado`, null, {
      params: { estado }
    });
    return response.data;
  }
};
