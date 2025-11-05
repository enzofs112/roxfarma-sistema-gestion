import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { clienteService } from '../../services/clienteService';
import { ClienteDTO } from '../../types';
import Alert from '../common/Alert';
import Loading from '../common/Loading';
import '../productos/ProductoForm.css';

const ClienteForm: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const [formData, setFormData] = useState<ClienteDTO>({
    nombre: '',
    documento: '',
    direccion: ''
  });

  useEffect(() => {
    if (id) {
      cargarCliente();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const cargarCliente = async () => {
    try {
      setLoading(true);
      const cliente = await clienteService.obtenerCliente(Number(id));
      setFormData({
        nombre: cliente.nombre,
        documento: cliente.documento,
        direccion: cliente.direccion || ''
      });
    } catch (err) {
      setError('Error al cargar cliente');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      if (id) {
        await clienteService.actualizarCliente(Number(id), formData);
        setSuccess('Cliente actualizado correctamente');
      } else {
        await clienteService.crearCliente(formData);
        setSuccess('Cliente creado correctamente');
      }
      setTimeout(() => navigate('/clientes'), 1500);
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al guardar cliente');
    } finally {
      setLoading(false);
    }
  };

  if (loading && id) return <Loading />;

  return (
    <div className="form-container">
      <h2>{id ? 'Editar Cliente' : 'Nuevo Cliente'}</h2>

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
          <label>Documento (RUC/DNI) *</label>
          <input
            type="text"
            required
            value={formData.documento}
            onChange={(e) => setFormData({ ...formData, documento: e.target.value })}
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
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/clientes')}>
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

export default ClienteForm;
