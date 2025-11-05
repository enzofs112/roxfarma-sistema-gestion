import React, { useState } from 'react';
import './ReporteList.css';

const ReporteList: React.FC = () => {
  const [tipoReporte, setTipoReporte] = useState('ventas');
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');

  const handleGenerar = () => {
    alert('Funcionalidad de reportes en desarrollo');
  };

  return (
    <div className="reporte-container">
      <h2>Reportes</h2>

      <div className="reporte-card">
        <h3>Generar Reporte</h3>
        
        <div className="form-group">
          <label>Tipo de Reporte</label>
          <select value={tipoReporte} onChange={(e) => setTipoReporte(e.target.value)}>
            <option value="ventas">Reporte de Ventas</option>
            <option value="productos">Reporte de Productos</option>
            <option value="inventario">Reporte de Inventario</option>
            <option value="pedidos">Reporte de Pedidos</option>
          </select>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Fecha Inicio</label>
            <input
              type="date"
              value={fechaInicio}
              onChange={(e) => setFechaInicio(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label>Fecha Fin</label>
            <input
              type="date"
              value={fechaFin}
              onChange={(e) => setFechaFin(e.target.value)}
            />
          </div>
        </div>

        <button onClick={handleGenerar} className="btn-generar">
          📊 Generar Reporte
        </button>
      </div>

      <div className="reportes-disponibles">
        <h3>Reportes Disponibles</h3>
        <div className="reporte-item">
          <span className="icon">📈</span>
          <div>
            <h4>Ventas del Mes</h4>
            <p>Resumen de ventas mensuales</p>
          </div>
          <button className="btn-ver">Ver</button>
        </div>

        <div className="reporte-item">
          <span className="icon">📦</span>
          <div>
            <h4>Inventario Actual</h4>
            <p>Estado actual del inventario</p>
          </div>
          <button className="btn-ver">Ver</button>
        </div>

        <div className="reporte-item">
          <span className="icon">💊</span>
          <div>
            <h4>Productos Más Vendidos</h4>
            <p>Top 10 productos del mes</p>
          </div>
          <button className="btn-ver">Ver</button>
        </div>

        <div className="reporte-item">
          <span className="icon">⚠️</span>
          <div>
            <h4>Alertas de Stock</h4>
            <p>Productos con stock bajo</p>
          </div>
          <button className="btn-ver">Ver</button>
        </div>
      </div>
    </div>
  );
};

export default ReporteList;
