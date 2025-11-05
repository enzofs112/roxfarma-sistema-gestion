import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { productoService } from '../../services/productoService';
import { categoriaService } from '../../services/categoriaService';
import { ProductoDTO, Categoria } from '../../types';
import Alert from '../common/Alert';
import Loading from '../common/Loading';
import './ProductoForm.css';

const ProductoForm: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  
  const [formData, setFormData] = useState<ProductoDTO>({
    nombre: '',
    descripcion: '',
    precio: 0,
    stock: 0,
    fechaVencimiento: '',
    idCategoria: 0
  });

  useEffect(() => {
    cargarCategorias();
    if (id) {
      cargarProducto();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const cargarCategorias = async () => {
    try {
      const data = await categoriaService.listarCategorias();
      setCategorias(data);
    } catch (err) {
      setError('Error al cargar categorías');
    }
  };

  const cargarProducto = async () => {
    try {
      setLoading(true);
      const producto = await productoService.obtenerProducto(Number(id));
      setFormData({
        nombre: producto.nombre,
        descripcion: producto.descripcion || '',
        precio: producto.precio,
        stock: producto.stock,
        fechaVencimiento: producto.fechaVencimiento,
        idCategoria: producto.categoria.idCategoria
      });
    } catch (err) {
      setError('Error al cargar producto');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      setLoading(true);
      if (id) {
        await productoService.actualizarProducto(Number(id), formData);
        setSuccess('Producto actualizado correctamente');
      } else {
        await productoService.crearProducto(formData);
        setSuccess('Producto creado correctamente');
      }
      setTimeout(() => navigate('/productos'), 1500);
    } catch (err: any) {
      setError(err.response?.data?.mensaje || 'Error al guardar producto');
    } finally {
      setLoading(false);
    }
  };

  if (loading && id) return <Loading />;

  return (
    <div className="form-container">
      <h2>{id ? 'Editar Producto' : 'Nuevo Producto'}</h2>

      {error && <Alert type="error" message={error} onClose={() => setError('')} />}
      {success && <Alert type="success" message={success} />}

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label>Nombre *</label>
          <input
            type="text"
            required
            value={formData.nombre}
            onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
          />
        </div>

        <div className="form-group">
          <label>Descripción</label>
          <textarea
            value={formData.descripcion}
            onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
            rows={3}
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Precio *</label>
            <input
              type="number"
              step="0.01"
              required
              min="0"
              value={formData.precio}
              onChange={(e) => setFormData({ ...formData, precio: Number(e.target.value) })}
            />
          </div>

          <div className="form-group">
            <label>Stock *</label>
            <input
              type="number"
              required
              min="0"
              value={formData.stock}
              onChange={(e) => setFormData({ ...formData, stock: Number(e.target.value) })}
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Categoría *</label>
            <select
              required
              value={formData.idCategoria}
              onChange={(e) => setFormData({ ...formData, idCategoria: Number(e.target.value) })}
            >
              <option value={0}>Seleccione una categoría</option>
              {categorias.map(cat => (
                <option key={cat.idCategoria} value={cat.idCategoria}>
                  {cat.nombre}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Fecha de Vencimiento *</label>
            <input
              type="date"
              required
              value={formData.fechaVencimiento}
              onChange={(e) => setFormData({ ...formData, fechaVencimiento: e.target.value })}
            />
          </div>
        </div>

        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={() => navigate('/productos')}>
            Cancelar
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProductoForm;
