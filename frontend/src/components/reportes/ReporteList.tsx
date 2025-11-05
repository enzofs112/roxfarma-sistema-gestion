import React, { useState } from 'react';
import './ReporteList.css';

const ReporteList: React.FC = () => {
  const [tipoReporte, setTipoReporte] = useState('ventas');

  const generarReporte = () => {
    alert(`Generando reporte de ${tipoReporte}...`);
    // Aquí iría la lógica para generar el reporte
  };

  return (
    <div className="reporte-container">
      <div className="header">
        <h2>📈 Reportes</h2>
      </div>

      <div className="reporte-content">
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
          <button className="btn-generate" onClick={generarReporte}>
            Generar Reporte
          </button>
        </div>

        <div className="reporte-info">
          <h3>📊 Reportes Disponibles</h3>
          <ul>
            <li>✅ Reporte de Ventas Diarias</li>
            <li>✅ Reporte de Productos Más Vendidos</li>
            <li>✅ Reporte de Inventario Actual</li>
            <li>✅ Reporte de Pedidos Pendientes</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default ReporteList;
