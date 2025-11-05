import api from './api';
import { Usuario, UsuarioDTO } from '../types';

export const usuarioService = {
  // Listar todos los usuarios
  listarUsuarios: async (): Promise<Usuario[]> => {
    const response = await api.get<Usuario[]>('/api/usuarios');
    return response.data;
  },

  // Obtener un usuario por ID
  obtenerUsuario: async (id: number): Promise<Usuario> => {
    const response = await api.get<Usuario>(`/api/usuarios/${id}`);
    return response.data;
  },

  // Crear un nuevo usuario
  crearUsuario: async (usuario: UsuarioDTO): Promise<Usuario> => {
    const response = await api.post<Usuario>('/api/usuarios', usuario);
    return response.data;
  },

  // Actualizar un usuario
  actualizarUsuario: async (id: number, usuario: UsuarioDTO): Promise<Usuario> => {
    const response = await api.put<Usuario>(`/api/usuarios/${id}`, usuario);
    return response.data;
  },

  // Eliminar un usuario
  eliminarUsuario: async (id: number): Promise<void> => {
    await api.delete(`/api/usuarios/${id}`);
  },

  // Activar/desactivar usuario
  cambiarEstado: async (id: number, activo: boolean): Promise<Usuario> => {
    const response = await api.patch<Usuario>(`/api/usuarios/${id}/estado`, { activo });
    return response.data;
  }
};
