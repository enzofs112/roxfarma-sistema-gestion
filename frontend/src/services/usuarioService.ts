import api from './api';
import { Usuario, UsuarioDTO } from '../types';

export const usuarioService = {
  listar: async (): Promise<Usuario[]> => {
    const response = await api.get<Usuario[]>('/api/usuarios');
    return response.data;
  },

  obtenerPorId: async (id: number): Promise<Usuario> => {
    const response = await api.get<Usuario>(`/api/usuarios/${id}`);
    return response.data;
  },

  crear: async (usuario: UsuarioDTO): Promise<Usuario> => {
    const response = await api.post<Usuario>('/api/usuarios', usuario);
    return response.data;
  },

  actualizar: async (id: number, usuario: UsuarioDTO): Promise<Usuario> => {
    const response = await api.put<Usuario>(`/api/usuarios/${id}`, usuario);
    return response.data;
  },

  eliminar: async (id: number): Promise<void> => {
    await api.delete(`/api/usuarios/${id}`);
  }
};
