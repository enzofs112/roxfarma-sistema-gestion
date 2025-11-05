import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { proveedorService } from '../../services/proveedorService';
import { ProveedorDTO } from '../../types';
import Alert from '../common/Alert';
import Loading from '../common/Loading';
import '../productos/ProductoForm.css';

const ProveedorForm: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const [formData, setFormData] = useState<ProveedorDTO>({
    nombre: '',
    contacto: '',
    direccion: ''
  });

  useEffect(() => {
    if (id) {
      cargarProveedor();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const cargarProveedor = async () => {
    try {
      setLoading(true);
      const proveedor = await proveedorService.obtenerProveedor(Number(id));
      setFormData({
        nombre: proveedor.nombre,
        contacto: proveedor.contacto || '',
        direccion: proveedor.direccion || ''
      });
    } catch (err) {
      setError('Error al cargar proveedor');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      if (id) {
        await proveedorService.actualizarProveedor(Number(id), formData);
        setSuccess('Proveedor actualizado correctamente');
      } else {
        await proveedorService.crearProveedor(formData);
        setSuccess('Proveedor creado correctamente');
      }
      setTimeout(() => navigate('/proveedores'), 1500);
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al guardar proveedor');
    } finally {
      setLoading(false);
    }
  };

  if (loading && id) return <Loading />;

  return (
    <div className="form-container">
      <h2>{id ? 'Editar Proveedor' : 'Nuevo Proveedor'}</h2>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}
      {success && <Alert type="success" message={success} />}

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Nombre *</label>
          <input
            type="text"
            required
            value={formData.nombre}
            onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
          />
        </div>

        <div className="form-group">
          <label>Contacto</label>
          <input
            type="text"
            value={formData.contacto}
            onChange={(e) => setFormData({ ...formData, contacto: e.target.value })}
          />
        </div>

        <div className="form-group">
          <label>Dirección</label>
          <textarea
            value={formData.direccion}
            onChange={(e) => setFormData({ ...formData, direccion: e.target.value })}
            rows={3}
          />
        </div>

        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/proveedores')}>
            Cancelar
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProveedorForm;
