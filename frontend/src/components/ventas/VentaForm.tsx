import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ventaService } from '../../services/ventaService';
import { clienteService } from '../../services/clienteService';
import { productoService } from '../../services/productoService';
import { Cliente, Producto, DetalleVentaDTO } from '../../types';
import Alert from '../common/Alert';
import './VentaForm.css';

const VentaForm: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [idCliente, setIdCliente] = useState(0);
  const [detalles, setDetalles] = useState<DetalleVentaDTO[]>([]);
  const [productoSeleccionado, setProductoSeleccionado] = useState(0);
  const [cantidad, setCantidad] = useState(1);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [clientesData, productosData] = await Promise.all([
        clienteService.listarClientes(),
        productoService.listarProductos()
      ]);
      setClientes(clientesData);
      setProductos(productosData);
    } catch (err) {
      setError('Error al cargar datos');
    }
  };

  const agregarProducto = () => {
    if (productoSeleccionado === 0 || cantidad <= 0) {
      setError('Seleccione un producto y cantidad válida');
      return;
    }

    const producto = productos.find(p => p.idProducto === productoSeleccionado);
    if (!producto) return;

    if (cantidad > producto.stock) {
      setError(`Stock insuficiente. Disponible: ${producto.stock}`);
      return;
    }

    const detalleExistente = detalles.find(d => d.idProducto === productoSeleccionado);
    if (detalleExistente) {
      setDetalles(detalles.map(d =>
        d.idProducto === productoSeleccionado
          ? { ...d, cantidad: d.cantidad + cantidad }
          : d
      ));
    } else {
      setDetalles([...detalles, {
        idProducto: productoSeleccionado,
        cantidad,
        precio: producto.precio
      }]);
    }

    setProductoSeleccionado(0);
    setCantidad(1);
    setError('');
  };

  const eliminarDetalle = (idProducto: number) => {
    setDetalles(detalles.filter(d => d.idProducto !== idProducto));
  };

  const calcularSubtotal = () => {
    return detalles.reduce((sum, d) => sum + (d.precio * d.cantidad), 0);
  };

  const calcularIGV = () => {
    return calcularSubtotal() * 0.18;
  };

  const calcularTotal = () => {
    return calcularSubtotal() + calcularIGV();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (idCliente === 0) {
      setError('Seleccione un cliente');
      return;
    }
    if (detalles.length === 0) {
      setError('Agregue al menos un producto');
      return;
    }

    try {
      setLoading(true);
      await ventaService.registrarVenta({ idCliente, detalles });
      setSuccess('Venta registrada correctamente');
      setTimeout(() => navigate('/ventas'), 1500);
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al registrar venta');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>Nueva Venta</h2>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}
      {success && <Alert type="success" message={success} />}

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Cliente *</label>
          <select value={idCliente} onChange={(e) => setIdCliente(Number(e.target.value))} required>
            <option value={0}>Seleccione un cliente</option>
            {clientes.map(c => (
              <option key={c.idCliente} value={c.idCliente}>
                {c.nombre} - {c.documento}
              </option>
            ))}
          </select>
        </div>

        <div className="productos-section">
          <h3>Agregar Productos</h3>
          <div className="form-row">
            <div className="form-group">
              <label>Producto</label>
              <select value={productoSeleccionado} onChange={(e) => setProductoSeleccionado(Number(e.target.value))}>
                <option value={0}>Seleccione un producto</option>
                {productos.map(p => (
                  <option key={p.idProducto} value={p.idProducto}>
                    {p.nombre} - S/ {p.precio.toFixed(2)} (Stock: {p.stock})
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Cantidad</label>
              <input
                type="number"
                min="1"
                value={cantidad}
                onChange={(e) => setCantidad(Number(e.target.value))}
              />
            </div>
            <button type="button" className="btn btn-primary" onClick={agregarProducto}>
              Agregar
            </button>
          </div>
        </div>

        {detalles.length > 0 && (
          <div className="detalles-section">
            <h3>Productos Agregados</h3>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Cantidad</th>
                  <th>Precio Unit.</th>
                  <th>Subtotal</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {detalles.map(d => {
                  const producto = productos.find(p => p.idProducto === d.idProducto);
                  return (
                    <tr key={d.idProducto}>
                      <td>{producto?.nombre}</td>
                      <td>{d.cantidad}</td>
                      <td>S/ {d.precio.toFixed(2)}</td>
                      <td>S/ {(d.precio * d.cantidad).toFixed(2)}</td>
                      <td>
                        <button type="button" className="btn btn-sm btn-danger" onClick={() => eliminarDetalle(d.idProducto)}>
                          Eliminar
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>

            <div className="totales">
              <div className="total-row">
                <span>Subtotal:</span>
                <span>S/ {calcularSubtotal().toFixed(2)}</span>
              </div>
              <div className="total-row">
                <span>IGV (18%):</span>
                <span>S/ {calcularIGV().toFixed(2)}</span>
              </div>
              <div className="total-row total-final">
                <span>Total:</span>
                <span>S/ {calcularTotal().toFixed(2)}</span>
              </div>
            </div>
          </div>
        )}

        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/ventas')}>
            Cancelar
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Registrando...' : 'Registrar Venta'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default VentaForm;
