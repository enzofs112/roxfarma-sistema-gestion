import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { usuarioService } from '../../services/usuarioService';
import { UsuarioDTO, Rol } from '../../types';
import './UsuarioForm.css';

const UsuarioForm: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState<UsuarioDTO>({
    nombre: '',
    usuario: '',
    contrasena: '',
    rol: Rol.TRABAJADOR,
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
        contrasena: '', // No mostramos la contraseña
        rol: usuario.rol,
      });
    } catch (err) {
      setError('Error al cargar usuario');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.nombre || !formData.usuario) {
      setError('Complete todos los campos obligatorios');
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
      <h2>{id ? 'Editar Usuario' : 'Nuevo Usuario'}</h2>

      <form onSubmit={handleSubmit} className="usuario-form">
        <div className="form-group">
          <label>Nombre Completo *</label>
          <input
            type="text"
            value={formData.nombre}
            onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
            placeholder="Ej: Juan Pérez"
          />
        </div>

        <div className="form-group">
          <label>Usuario *</label>
          <input
            type="text"
            value={formData.usuario}
            onChange={(e) => setFormData({ ...formData, usuario: e.target.value })}
            placeholder="Ej: jperez"
          />
        </div>

        <div className="form-group">
          <label>Contraseña {!id && '*'}</label>
          <input
            type="password"
            value={formData.contrasena}
            onChange={(e) => setFormData({ ...formData, contrasena: e.target.value })}
            placeholder={id ? 'Dejar en blanco para no cambiar' : 'Ingrese contraseña'}
          />
        </div>

        <div className="form-group">
          <label>Rol *</label>
          <select
            value={formData.rol}
            onChange={(e) => setFormData({ ...formData, rol: e.target.value as Rol })}
          >
            <option value={Rol.TRABAJADOR}>Trabajador</option>
            <option value={Rol.ADMINISTRADOR}>Administrador</option>
          </select>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="form-actions">
          <button type="button" onClick={() => navigate('/usuarios')} className="btn-cancel">
            Cancelar
          </button>
          <button type="submit" disabled={loading} className="btn-submit">
            {loading ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default UsuarioForm;
