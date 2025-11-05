import api from './api';
import { Categoria, CategoriaDTO } from '../types';

export const categoriaService = {
  listarCategorias: async (): Promise<Categoria[]> => {
    const response = await api.get('/api/categorias');
    return response.data;
  },

  obtenerCategoria: async (id: number): Promise<Categoria> => {
    const response = await api.get(`/api/categorias/${id}`);
    return response.data;
  },

  crearCategoria: async (categoria: CategoriaDTO): Promise<Categoria> => {
    const response = await api.post('/api/categorias', categoria);
    return response.data;
  },

  actualizarCategoria: async (id: number, categoria: CategoriaDTO): Promise<Categoria> => {
    const response = await api.put(`/api/categorias/${id}`, categoria);
    return response.data;
  },

  eliminarCategoria: async (id: number): Promise<void> => {
    await api.delete(`/api/categorias/${id}`);
  }
};
