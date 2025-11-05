import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { dashboardService } from '../../services/dashboardService';
import Loading from '../common/Loading';
import Alert from '../common/Alert';
import './Dashboard.css';

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [alertas, setAlertas] = useState<any>(null);
  const [estadisticas, setEstadisticas] = useState<any>(null);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [alertasData, estadisticasData] = await Promise.all([
        dashboardService.obtenerAlertas(),
        dashboardService.obtenerEstadisticas()
      ]);
      setAlertas(alertasData);
      setEstadisticas(estadisticasData);
    } catch (err) {
      setError('Error al cargar datos del dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="dashboard-content">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <p>Sistema de Gestión Farmacéutica</p>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <div className="dashboard-cards">
        <div className="dashboard-card" onClick={() => navigate('/productos')}>
          <div className="card-icon">📦</div>
          <h3>Productos</h3>
          <p className="card-number">{estadisticas?.totalProductos || 0}</p>
          <p>Total en inventario</p>
        </div>

        <div className="dashboard-card" onClick={() => navigate('/ventas')}>
          <div className="card-icon">💰</div>
          <h3>Ventas</h3>
          <p>Registro de ventas</p>
        </div>

        <div className="dashboard-card" onClick={() => navigate('/pedidos')}>
          <div className="card-icon">📋</div>
          <h3>Pedidos</h3>
          <p>Gestión de pedidos</p>
        </div>

        <div className="dashboard-card warning" onClick={() => navigate('/productos')}>
          <div className="card-icon">⚠️</div>
          <h3>Stock Bajo</h3>
          <p className="card-number">{alertas?.productosStockBajo?.length || 0}</p>
          <p>Productos con stock bajo</p>
        </div>

        <div className="dashboard-card danger" onClick={() => navigate('/productos')}>
          <div className="card-icon">📅</div>
          <h3>Por Vencer</h3>
          <p className="card-number">{alertas?.productosProximosVencer?.length || 0}</p>
          <p>Productos próximos a vencer</p>
        </div>

        <div className="dashboard-card" onClick={() => navigate('/reportes')}>
          <div className="card-icon">📊</div>
          <h3>Reportes</h3>
          <p>Estadísticas y reportes</p>
        </div>
      </div>

      {alertas && (alertas.productosStockBajo.length > 0 || alertas.productosProximosVencer.length > 0) && (
        <div className="dashboard-alerts">
          <h3>⚠️ Alertas Importantes</h3>
          
          {alertas.productosStockBajo.length > 0 && (
            <div className="alert-section">
              <h4>Productos con Stock Bajo</h4>
              <ul>
                {alertas.productosStockBajo.slice(0, 5).map((p: any) => (
                  <li key={p.idProducto}>
                    {p.nombre} - Stock: {p.stock}
                  </li>
                ))}
              </ul>
            </div>
          )}

          {alertas.productosProximosVencer.length > 0 && (
            <div className="alert-section">
              <h4>Productos Próximos a Vencer</h4>
              <ul>
                {alertas.productosProximosVencer.slice(0, 5).map((p: any) => (
                  <li key={p.idProducto}>
                    {p.nombre} - Vence: {new Date(p.fechaVencimiento).toLocaleDateString()}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
