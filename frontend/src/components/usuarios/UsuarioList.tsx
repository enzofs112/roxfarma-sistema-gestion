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
      setError('');
    } catch (err: any) {
      console.error('Error al cargar usuarios:', err);
      setError('Error al cargar la lista de usuarios');
    } finally {
      setLoading(false);
    }
  };

  const handleEliminar = async (id: number, nombre: string) => {
    if (window.confirm(`¿Está seguro de eliminar al usuario ${nombre}?`)) {
      try {
        await usuarioService.eliminar(id);
        cargarUsuarios();
      } catch (err: any) {
        alert('Error al eliminar usuario');
      }
    }
  };

  if (loading) return <div className="loading">Cargando usuarios...</div>;

  return (
    <div className="usuario-list-container">
      <div className="header">
        <h2>👤 Gestión de Usuarios</h2>
        <button className="btn-primary" onClick={() => navigate('/usuarios/nuevo')}>
          + Nuevo Usuario
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Usuario</th>
              <th>Rol</th>
              <th>Estado</th>
              <th>Fecha Creación</th>
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
                  <span className={`badge badge-${usuario.rol.toLowerCase()}`}>
                    {usuario.rol}
                  </span>
                </td>
                <td>
                  <span className={`badge ${usuario.activo ? 'badge-activo' : 'badge-inactivo'}`}>
                    {usuario.activo ? 'Activo' : 'Inactivo'}
                  </span>
                </td>
                <td>
                  {usuario.fechaCreacion 
                    ? new Date(usuario.fechaCreacion).toLocaleDateString() 
                    : 'N/A'}
                </td>
                <td>
                  <button
                    className="btn-edit"
                    onClick={() => navigate(`/usuarios/editar/${usuario.idUsuario}`)}
                  >
                    ✏️
                  </button>
                  <button
                    className="btn-delete"
                    onClick={() => handleEliminar(usuario.idUsuario, usuario.nombre)}
                  >
                    🗑️
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {usuarios.length === 0 && !loading && (
          <div className="empty-state">No hay usuarios registrados</div>
        )}
      </div>
    </div>
  );
};

export default UsuarioList;
