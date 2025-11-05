import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { usuarioService } from '../../services/usuarioService';
import { Rol } from '../../types';
import './UsuarioForm.css';

const UsuarioForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    nombre: '',
    usuario: '',
    contrasena: '',
    rol: Rol.TRABAJADOR
  });

  useEffect(() => {
    if (id) {
      cargarUsuario();
    }
  }, [id]);

  const cargarUsuario = async () => {
    try {
      const usuario = await usuarioService.obtenerPorId(Number(id));
      setFormData({
        nombre: usuario.nombre,
        usuario: usuario.usuario,
        contrasena: '',
        rol: usuario.rol
      });
    } catch (err) {
      setError('Error al cargar usuario');
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.nombre || !formData.usuario) {
      setError('Por favor complete todos los campos obligatorios');
      return;
    }

    if (!id && !formData.contrasena) {
      setError('La contraseña es obligatoria para nuevos usuarios');
      return;
    }

    try {
      setLoading(true);
      if (id) {
        await usuarioService.actualizar(Number(id), formData);
      } else {
        await usuarioService.crear(formData);
      }
      navigate('/usuarios');
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al guardar usuario');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="usuario-form-container">
      <div className="form-header">
        <h2>{id ? '✏️ Editar Usuario' : '➕ Nuevo Usuario'}</h2>
        <button className="btn-back" onClick={() => navigate('/usuarios')}>
          ← Volver
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="usuario-form">
        <div className="form-group">
          <label htmlFor="nombre">Nombre Completo *</label>
          <input
            type="text"
            id="nombre"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="usuario">Usuario *</label>
          <input
            type="text"
            id="usuario"
            name="usuario"
            value={formData.usuario}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="contrasena">
            Contraseña {id ? '(dejar en blanco para no cambiar)' : '*'}
          </label>
          <input
            type="password"
            id="contrasena"
            name="contrasena"
            value={formData.contrasena}
            onChange={handleChange}
            required={!id}
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="rol">Rol *</label>
          <select
            id="rol"
            name="rol"
            value={formData.rol}
            onChange={handleChange}
            required
            disabled={loading}
          >
            <option value={Rol.TRABAJADOR}>Trabajador</option>
            <option value={Rol.ADMINISTRADOR}>Administrador</option>
          </select>
        </div>

        <div className="form-actions">
          <button type="button" className="btn-cancel" onClick={() => navigate('/usuarios')}>
            Cancelar
          </button>
          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default UsuarioForm;
