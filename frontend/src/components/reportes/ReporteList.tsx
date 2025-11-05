import React, { useState } from 'react';
import './ReporteList.css';

const ReporteList: React.FC = () => {
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');

  const handleGenerarReporte = (tipo: string) => {
    alert(`Generando reporte de ${tipo}...\nEsta funcionalidad se implementará próximamente.`);
  };

  return (
    <div className="reporte-container">
      <h1>Reportes</h1>

      <div className="filtros-card">
        <h3>Filtros de Fecha</h3>
        <div className="filtros">
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
      </div>

      <div className="reportes-grid">
        <div className="reporte-card">
          <div className="reporte-icon">📊</div>
          <h3>Reporte de Ventas</h3>
          <p>Resumen de ventas por período</p>
          <button onClick={() => handleGenerarReporte('Ventas')} className="btn-generar">
            Generar Reporte
          </button>
        </div>

        <div className="reporte-card">
          <div className="reporte-icon">📦</div>
          <h3>Reporte de Inventario</h3>
          <p>Estado actual del inventario</p>
          <button onClick={() => handleGenerarReporte('Inventario')} className="btn-generar">
            Generar Reporte
          </button>
        </div>

        <div className="reporte-card">
          <div className="reporte-icon">🏢</div>
          <h3>Reporte de Pedidos</h3>
          <p>Pedidos realizados a proveedores</p>
          <button onClick={() => handleGenerarReporte('Pedidos')} className="btn-generar">
            Generar Reporte
          </button>
        </div>

        <div className="reporte-card">
          <div className="reporte-icon">👥</div>
          <h3>Reporte de Clientes</h3>
          <p>Listado de clientes y compras</p>
          <button onClick={() => handleGenerarReporte('Clientes')} className="btn-generar">
            Generar Reporte
          </button>
        </div>

        <div className="reporte-card">
          <div className="reporte-icon">💰</div>
          <h3>Reporte Financiero</h3>
          <p>Ingresos y gastos del período</p>
          <button onClick={() => handleGenerarReporte('Financiero')} className="btn-generar">
            Generar Reporte
          </button>
        </div>

        <div className="reporte-card">
          <div className="reporte-icon">⚠️</div>
          <h3>Reporte de Alertas</h3>
          <p>Productos con stock bajo o vencidos</p>
          <button onClick={() => handleGenerarReporte('Alertas')} className="btn-generar">
            Generar Reporte
          </button>
        </div>
      </div>
    </div>
  );
};

export default ReporteList;
