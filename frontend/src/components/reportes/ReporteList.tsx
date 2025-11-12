import React, { useState } from 'react';
import { dashboardService } from '../../services/dashboardService';
import './ReporteList.css';

const ReporteList: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [estadisticas, setEstadisticas] = useState<any>(null);
  const [alertas, setAlertas] = useState<any>(null);

  const cargarReportes = async () => {
    try {
      setLoading(true);
      const [stats, alerts] = await Promise.all([
        dashboardService.obtenerEstadisticas(),
        dashboardService.obtenerAlertas()
      ]);
      setEstadisticas(stats);
      setAlertas(alerts);
    } catch (err) {
      console.error('Error al cargar reportes:', err);
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    cargarReportes();
  }, []);

  if (loading) return <div className="loading">Cargando reportes...</div>;

  return (
    <div className="reporte-container">
      <h2>📈 Reportes del Sistema</h2>

      <div className="reporte-section">
        <h3>📊 Estadísticas Generales</h3>
        {estadisticas && (
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-icon">📦</div>
              <div className="stat-info">
                <div className="stat-value">{estadisticas.totalProductos}</div>
                <div className="stat-label">Total Productos</div>
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-icon">📊</div>
              <div className="stat-info">
                <div className="stat-value">{estadisticas.stockTotal}</div>
                <div className="stat-label">Stock Total</div>
              </div>
            </div>

            <div className="stat-card warning">
              <div className="stat-icon">⚠️</div>
              <div className="stat-info">
                <div className="stat-value">{estadisticas.alertasStockBajo}</div>
                <div className="stat-label">Alertas Stock Bajo</div>
              </div>
            </div>

            <div className="stat-card danger">
              <div className="stat-icon">⏰</div>
              <div className="stat-info">
                <div className="stat-value">{estadisticas.alertasVencimiento}</div>
                <div className="stat-label">Próximos a Vencer</div>
              </div>
            </div>
          </div>
        )}
      </div>

      <div className="reporte-section">
        <h3>⚠️ Productos con Stock Bajo</h3>
        {alertas && alertas.stockBajo && alertas.stockBajo.length > 0 ? (
          <table className="reporte-table">
            <thead>
              <tr>
                <th>Producto</th>
                <th>Presentación</th>
                <th>Categoría</th>
                <th>Stock Actual</th>
                <th>Precio</th>
              </tr>
            </thead>
            <tbody>
              {alertas.stockBajo.map((producto: any) => (
                <tr key={producto.idProducto}>
                  <td>{producto.nombre}</td>
                  <td>{producto.presentacion || '-'}</td>
                  <td>{producto.categoria?.nombre || 'N/A'}</td>
                  <td className="stock-bajo">{producto.stock}</td>
                  <td>S/ {producto.precio.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div className="empty-state">✅ No hay productos con stock bajo</div>
        )}
      </div>

      <div className="reporte-section">
        <h3>⏰ Productos Próximos a Vencer</h3>
        {alertas && alertas.proximosVencer && alertas.proximosVencer.length > 0 ? (
          <table className="reporte-table">
            <thead>
              <tr>
                <th>Producto</th>
                <th>Presentación</th>
                <th>Categoría</th>
                <th>Fecha Vencimiento</th>
                <th>Stock</th>
              </tr>
            </thead>
            <tbody>
              {alertas.proximosVencer.map((producto: any) => (
                <tr key={producto.idProducto}>
                  <td>{producto.nombre}</td>
                  <td>{producto.presentacion || '-'}</td>
                  <td>{producto.categoria?.nombre || 'N/A'}</td>
                  <td className="fecha-vencimiento">
                    {producto.fechaVencimiento 
                      ? new Date(producto.fechaVencimiento).toLocaleDateString()
                      : 'N/A'}
                  </td>
                  <td>{producto.stock}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div className="empty-state">✅ No hay productos próximos a vencer</div>
        )}
      </div>
    </div>
  );
};

export default ReporteList;
