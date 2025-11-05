import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { usuarioService } from '../../services/usuarioService';
import { Usuario } from '../../types';
import './UsuarioList.css';

const UsuarioList: React.FC = () => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    cargarUsuarios();
  }, []);

  const cargarUsuarios = async () => {
    try {
      setLoading(true);
      const data = await usuarioService.listar();
      setUsuarios(data);
    } catch (err) {
      setError('Error al cargar usuarios');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEliminar = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este usuario?')) {
      try {
        await usuarioService.eliminar(id);
        cargarUsuarios();
      } catch (err) {
        alert('Error al eliminar usuario');
      }
    }
  };

  if (loading) return <div className="loading">Cargando...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="usuario-list-container">
      <div className="header">
        <h2>Gestión de Usuarios</h2>
        <button onClick={() => navigate('/usuarios/nuevo')} className="btn-primary">
          + Nuevo Usuario
        </button>
      </div>

      <table className="tabla-usuarios">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Usuario</th>
            <th>Rol</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map((usuario) => (
            <tr key={usuario.idUsuario}>
              <td>{usuario.idUsuario}</td>
              <td>{usuario.nombre}</td>
              <td>{usuario.usuario}</td>
              <td>
                <span className={`badge ${usuario.rol.toLowerCase()}`}>
                  {usuario.rol}
                </span>
              </td>
              <td>
                <span className={`badge ${usuario.activo ? 'activo' : 'inactivo'}`}>
                  {usuario.activo ? 'Activo' : 'Inactivo'}
                </span>
              </td>
              <td>
                <button
                  onClick={() => navigate(`/usuarios/editar/${usuario.idUsuario}`)}
                  className="btn-edit"
                >
                  Editar
                </button>
                <button
                  onClick={() => handleEliminar(usuario.idUsuario)}
                  className="btn-delete"
                >
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {usuarios.length === 0 && (
        <div className="empty-state">No hay usuarios registrados</div>
      )}
    </div>
  );
};

export default UsuarioList;
