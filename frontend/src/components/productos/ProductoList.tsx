import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { productoService } from '../../services/productoService';
import { Producto, Rol } from '../../types';
import { useAuth } from '../../context/AuthContext';
import Loading from '../common/Loading';
import Alert from '../common/Alert';
import ConfirmDialog from '../common/ConfirmDialog';
import './ProductoList.css';

const ProductoList: React.FC = () => {
  const [productos, setProductos] = useState<Producto[]>([]);
  const [filteredProductos, setFilteredProductos] = useState<Producto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const { hasRole } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    cargarProductos();
  }, []);

  useEffect(() => {
    const filtered = productos.filter(p =>
      p.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.categoria.nombre.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredProductos(filtered);
  }, [searchTerm, productos]);

  const cargarProductos = async () => {
    try {
      setLoading(true);
      const data = await productoService.listarProductos();
      setProductos(data);
      setFilteredProductos(data);
    } catch (err) {
      setError('Error al cargar productos');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      await productoService.eliminarProducto(deleteId);
      setDeleteId(null);
      cargarProductos();
    } catch (err) {
      setError('Error al eliminar producto');
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="producto-list-container">
      <div className="list-header">
        <h2>Gestión de Productos</h2>
        {hasRole(Rol.ADMINISTRADOR) && (
          <button className="btn btn-primary" onClick={() => navigate('/productos/nuevo')}>
            + Nuevo Producto
          </button>
        )}
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}

      <div className="search-box">
        <input
          type="text"
          placeholder="Buscar por nombre o categoría..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <div className="table-container">
        <table className="data-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Presentación</th>
            <th>Categoría</th>
            <th>Precio</th>
            <th>Stock</th>
            <th>Vencimiento</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {filteredProductos.map(producto => (
            <tr key={producto.idProducto} className={producto.stock < 10 ? 'stock-bajo' : ''}>
              <td>{producto.nombre}</td>
              <td>{producto.presentacion || '-'}</td>
              <td>{producto.categoria.nombre}</td>
              <td>S/ {producto.precio.toFixed(2)}</td>
              <td>
                {producto.stock}
                {producto.stock < 10 && <span className="badge badge-warning">⚠️ Bajo</span>}
              </td>
              <td>
                {producto.fechaVencimiento 
                  ? new Date(producto.fechaVencimiento).toLocaleDateString()
                  : 'N/A'}
              </td>
              <td>
                <button className="btn btn-sm btn-info" onClick={() => navigate(`/productos/${producto.idProducto}`)}>
                  Ver
                </button>
                {hasRole(Rol.ADMINISTRADOR) && (
                  <>
                    <button className="btn btn-sm btn-warning" onClick={() => navigate(`/productos/editar/${producto.idProducto}`)}>
                      Editar
                    </button>
                    <button className="btn btn-sm btn-danger" onClick={() => setDeleteId(producto.idProducto)}>
                      Eliminar
                    </button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>

      {deleteId && (
        <ConfirmDialog
          title="Confirmar eliminación"
          message="¿Está seguro de eliminar este producto?"
          onConfirm={handleDelete}
          onCancel={() => setDeleteId(null)}
        />
      )}
    </div>
  );
};

export default ProductoList;
