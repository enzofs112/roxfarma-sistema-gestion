import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { pedidoService } from '../../services/pedidoService';
import { Pedido, EstadoPedido } from '../../types';
import Loading from '../common/Loading';
import Alert from '../common/Alert';
import '../productos/ProductoList.css';

const PedidoList: React.FC = () => {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    cargarPedidos();
  }, []);

  const cargarPedidos = async () => {
    try {
      setLoading(true);
      const data = await pedidoService.listarPedidos();
      setPedidos(data);
    } catch (err) {
      setError('Error al cargar pedidos');
    } finally {
      setLoading(false);
    }
  };

  const cambiarEstado = async (id: number, estado: EstadoPedido) => {
    try {
      await pedidoService.actualizarEstado(id, estado);
      cargarPedidos();
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al cambiar estado');
    }
  };

  const getEstadoColor = (estado: EstadoPedido) => {
    switch (estado) {
      case 'PENDIENTE': return '#f39c12';
      case 'ENVIADO': return '#3498db';
      case 'RECIBIDO': return '#27ae60';
      default: return '#95a5a6';
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="producto-list-container">
      <div className="list-header">
        <h2>Gestión de Pedidos</h2>
        <button className="btn btn-primary" onClick={() => navigate('/pedidos/nuevo')}>
          + Nuevo Pedido
        </button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <div className="table-container">
        <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Proveedor</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {pedidos.map(pedido => (
            <tr key={pedido.idPedido}>
              <td>{pedido.idPedido}</td>
              <td>
                {pedido.fecha 
                  ? (() => {
                      const fecha = new Date(pedido.fecha);
                      return isNaN(fecha.getTime()) ? 'N/A' : fecha.toLocaleString('es-PE');
                    })()
                  : 'N/A'}
              </td>
              <td>{pedido.proveedor.nombre}</td>
              <td>
                <span style={{ 
                  padding: '0.25rem 0.75rem', 
                  borderRadius: '4px', 
                  backgroundColor: getEstadoColor(pedido.estado),
                  color: 'white'
                }}>
                  {pedido.estado}
                </span>
              </td>
              <td>
                {pedido.estado === EstadoPedido.PENDIENTE && (
                  <button className="btn btn-sm btn-info" onClick={() => cambiarEstado(pedido.idPedido, EstadoPedido.ENVIADO)}>
                    Marcar Enviado
                  </button>
                )}
                {pedido.estado === EstadoPedido.ENVIADO && (
                  <button className="btn btn-sm btn-primary" onClick={() => cambiarEstado(pedido.idPedido, EstadoPedido.RECIBIDO)}>
                    Marcar Recibido
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>
    </div>
  );
};

export default PedidoList;
