import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { clienteService } from '../../services/clienteService';
import { Cliente } from '../../types';
import Loading from '../common/Loading';
import Alert from '../common/Alert';
import ConfirmDialog from '../common/ConfirmDialog';
import '../productos/ProductoList.css';

const ClienteList: React.FC = () => {
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    cargarClientes();
  }, []);

  const cargarClientes = async () => {
    try {
      setLoading(true);
      const data = await clienteService.listarClientes();
      setClientes(data);
    } catch (err) {
      setError('Error al cargar clientes');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await clienteService.eliminarCliente(deleteId);
      setDeleteId(null);
      cargarClientes();
    } catch (err) {
      setError('Error al eliminar cliente');
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="producto-list-container">
      <div className="list-header">
        <h2>Gestión de Clientes</h2>
        <button className="btn btn-primary" onClick={() => navigate('/clientes/nuevo')}>
          + Nuevo Cliente
        </button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <div className="table-container">
        <table className="data-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Documento</th>
            <th>Dirección</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map(cliente => (
            <tr key={cliente.idCliente}>
              <td>{cliente.nombre}</td>
              <td>{cliente.documento}</td>
              <td>{cliente.direccion}</td>
              <td>
                <button className="btn btn-sm btn-warning" onClick={() => navigate(`/clientes/editar/${cliente.idCliente}`)}>
                  Editar
                </button>
                <button className="btn btn-sm btn-danger" onClick={() => setDeleteId(cliente.idCliente)}>
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>

      {deleteId && (
        <ConfirmDialog
          title="Confirmar eliminación"
          message="¿Está seguro de eliminar este cliente?"
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
        />
      )}
    </div>
  );
};

export default ClienteList;
