import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ventaService } from '../../services/ventaService';
import { Venta } from '../../types';
import Loading from '../common/Loading';
import Alert from '../common/Alert';
import './VentaList.css';

const VentaList: React.FC = () => {
  const [ventas, setVentas] = useState<Venta[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    cargarVentas();
  }, []);

  const cargarVentas = async () => {
    try {
      setLoading(true);
      const data = await ventaService.listarVentas();
      setVentas(data);
    } catch (err) {
      setError('Error al cargar ventas');
    } finally {
      setLoading(false);
    }
  };

  const descargarBoleta = async (id: number) => {
    try {
      const blob = await ventaService.descargarBoleta(id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `boleta-${id}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      setError('Error al descargar boleta');
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="producto-list-container">
      <div className="list-header">
        <h2>Historial de Ventas</h2>
        <button className="btn btn-primary" onClick={() => navigate('/ventas/nueva')}>
          + Nueva Venta
        </button>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <div className="table-container">
        <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Cliente</th>
            <th>Usuario</th>
            <th>Total</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {ventas.map(venta => (
            <tr key={venta.idVenta}>
              <td>{venta.idVenta}</td>
              <td>
                {venta.fecha 
                  ? (() => {
                      const fecha = new Date(venta.fecha);
                      return isNaN(fecha.getTime()) ? 'N/A' : fecha.toLocaleString('es-PE');
                    })()
                  : 'N/A'}
              </td>
              <td>{venta.cliente.nombre}</td>
              <td>{venta.usuario.nombre}</td>
              <td>S/ {venta.total.toFixed(2)}</td>
              <td>
                <button className="btn btn-sm btn-info" onClick={() => descargarBoleta(venta.idVenta)}>
                  📄 Descargar Boleta
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>
    </div>
  );
};

export default VentaList;
