import api from './api';
import { Cliente, ClienteDTO } from '../types';

export const clienteService = {
  listarClientes: async (): Promise<Cliente[]> => {
    const response = await api.get('/api/clientes');
    return response.data;
  },

  obtenerCliente: async (id: number): Promise<Cliente> => {
    const response = await api.get(`/api/clientes/${id}`);
    return response.data;
  },

  crearCliente: async (cliente: ClienteDTO): Promise<Cliente> => {
    const response = await api.post('/api/clientes', cliente);
    return response.data;
  },

  actualizarCliente: async (id: number, cliente: ClienteDTO): Promise<Cliente> => {
    const response = await api.put(`/api/clientes/${id}`, cliente);
    return response.data;
  },

  eliminarCliente: async (id: number): Promise<void> => {
    await api.delete(`/api/clientes/${id}`);
  }
};
