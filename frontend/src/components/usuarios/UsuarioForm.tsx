import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { usuarioService } from '../../services/usuarioService';
import { UsuarioDTO, Rol } from '../../types';
import './UsuarioForm.css';

const UsuarioForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState<UsuarioDTO>({
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
      const usuario = await usuarioService.obtenerUsuario(Number(id));
      setFormData({
        nombre: usuario.nombre,
        usuario: usuario.usuario,
        contrasena: '', // No mostramos la contraseña
        rol: usuario.rol
      });
    } catch (err) {
      setError('Error al cargar el usuario');
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
        await usuarioService.actualizarUsuario(Number(id), formData);
      } else {
        await usuarioService.crearUsuario(formData);
      }
      navigate('/usuarios');
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al guardar el usuario');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="usuario-form-container">
      <div className="form-header">
        <h2>{id ? '✏️ Editar Usuario' : '➕ Nuevo Usuario'}</h2>
      </div>

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
          />
        </div>

        <div className="form-group">
          <label htmlFor="contrasena">
            Contraseña {id ? '(dejar vacío para no cambiar)' : '*'}
          </label>
          <input
            type="password"
            id="contrasena"
            name="contrasena"
            value={formData.contrasena}
            onChange={handleChange}
            required={!id}
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
          >
            <option value={Rol.TRABAJADOR}>Trabajador</option>
            <option value={Rol.ADMINISTRADOR}>Administrador</option>
          </select>
        </div>

        {error && <div className="error-message">{error}</div>}

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
