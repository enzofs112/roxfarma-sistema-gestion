import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { proveedorService } from '../../services/proveedorService';
import { Proveedor } from '../../types';
import Loading from '../common/Loading';
import Alert from '../common/Alert';
import ConfirmDialog from '../common/ConfirmDialog';
import '../productos/ProductoList.css';

const ProveedorList: React.FC = () => {
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    cargarProveedores();
  }, []);

  const cargarProveedores = async () => {
    try {
      setLoading(true);
      const data = await proveedorService.listarProveedores();
      setProveedores(data);
    } catch (err) {
      setError('Error al cargar proveedores');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await proveedorService.eliminarProveedor(deleteId);
      setDeleteId(null);
      cargarProveedores();
    } catch (err) {
      setError('Error al eliminar proveedor');
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="producto-list-container">
      <div className="list-header">
        <h2>Gestión de Proveedores</h2>
        <button className="btn btn-primary" onClick={() => navigate('/proveedores/nuevo')}>
          + Nuevo Proveedor
        </button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <table className="data-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Contacto</th>
            <th>Dirección</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {proveedores.map(proveedor => (
            <tr key={proveedor.idProveedor}>
              <td>{proveedor.nombre}</td>
              <td>{proveedor.contacto}</td>
              <td>{proveedor.direccion}</td>
              <td>
                <button className="btn btn-sm btn-warning" onClick={() => navigate(`/proveedores/editar/${proveedor.idProveedor}`)}>
                  Editar
                </button>
                <button className="btn btn-sm btn-danger" onClick={() => setDeleteId(proveedor.idProveedor)}>
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {deleteId && (
        <ConfirmDialog
          title="Confirmar eliminación"
          message="¿Está seguro de eliminar este proveedor?"
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
        />
      )}
    </div>
  );
};

export default ProveedorList;
