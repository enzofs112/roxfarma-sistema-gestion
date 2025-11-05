import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { pedidoService } from '../../services/pedidoService';
import { proveedorService } from '../../services/proveedorService';
import { productoService } from '../../services/productoService';
import { Proveedor, Producto, DetallePedidoDTO } from '../../types';
import Alert from '../common/Alert';
import '../ventas/VentaForm.css';

const PedidoForm: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [productos, setProductos] = useState<Producto[]>([]);
  const [idProveedor, setIdProveedor] = useState(0);
  const [detalles, setDetalles] = useState<DetallePedidoDTO[]>([]);
  const [productoSeleccionado, setProductoSeleccionado] = useState(0);
  const [cantidad, setCantidad] = useState(1);

  useEffect(() => {
    cargarDatos();
  }, []);

  const cargarDatos = async () => {
    try {
      const [proveedoresData, productosData] = await Promise.all([
        proveedorService.listarProveedores(),
        productoService.listarProductos()
      ]);
      setProveedores(proveedoresData);
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

    const detalleExistente = detalles.find(d => d.idProducto === productoSeleccionado);
    if (detalleExistente) {
      setDetalles(detalles.map(d =>
        d.idProducto === productoSeleccionado
          ? { ...d, cantidad: d.cantidad + cantidad }
          : d
      ));
    } else {
      setDetalles([...detalles, { idProducto: productoSeleccionado, cantidad }]);
    }

    setProductoSeleccionado(0);
    setCantidad(1);
    setError('');
  };

  const eliminarDetalle = (idProducto: number) => {
    setDetalles(detalles.filter(d => d.idProducto !== idProducto));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (idProveedor === 0) {
      setError('Seleccione un proveedor');
      return;
    }
    if (detalles.length === 0) {
      setError('Agregue al menos un producto');
      return;
    }

    try {
      setLoading(true);
      await pedidoService.crearPedido({ idProveedor, detalles });
      setSuccess('Pedido creado correctamente');
      setTimeout(() => navigate('/pedidos'), 1500);
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al crear pedido');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>Nuevo Pedido</h2>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}
      {success && <Alert type="success" message={success} />}

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Proveedor *</label>
          <select value={idProveedor} onChange={(e) => setIdProveedor(Number(e.target.value))} required>
            <option value={0}>Seleccione un proveedor</option>
            {proveedores.map(p => (
              <option key={p.idProveedor} value={p.idProveedor}>
                {p.nombre}
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
                    {p.nombre}
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
          </div>
        )}

        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/pedidos')}>
            Cancelar
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Creando...' : 'Crear Pedido'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default PedidoForm;
