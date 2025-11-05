import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Login from './components/auth/Login';
import Dashboard from './components/dashboard/Dashboard';
import ProductoList from './components/productos/ProductoList';
import ProductoForm from './components/productos/ProductoForm';
import VentaList from './components/ventas/VentaList';
import VentaForm from './components/ventas/VentaForm';
import PedidoList from './components/pedidos/PedidoList';
import PedidoForm from './components/pedidos/PedidoForm';
import ClienteList from './components/clientes/ClienteList';
import ClienteForm from './components/clientes/ClienteForm';
import ProveedorList from './components/proveedores/ProveedorList';
import ProveedorForm from './components/proveedores/ProveedorForm';
import UsuarioList from './components/usuarios/UsuarioList';
import UsuarioForm from './components/usuarios/UsuarioForm';
import ReporteList from './components/reportes/ReporteList';
import PrivateRoute from './components/common/PrivateRoute';
import Navbar from './components/common/Navbar';
import Sidebar from './components/common/Sidebar';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/*"
            element={
              <PrivateRoute>
                <div className="app-layout">
                  <Navbar />
                  <div className="app-container">
                    <Sidebar />
                    <main className="app-main">
                      <Routes>
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/productos" element={<ProductoList />} />
                        <Route path="/productos/nuevo" element={<ProductoForm />} />
                        <Route path="/productos/editar/:id" element={<ProductoForm />} />
                        <Route path="/productos/:id" element={<ProductoForm />} />
                        <Route path="/ventas" element={<VentaList />} />
                        <Route path="/ventas/nueva" element={<VentaForm />} />
                        <Route path="/pedidos" element={<PedidoList />} />
                        <Route path="/pedidos/nuevo" element={<PedidoForm />} />
                        <Route path="/clientes" element={<ClienteList />} />
                        <Route path="/clientes/nuevo" element={<ClienteForm />} />
                        <Route path="/clientes/editar/:id" element={<ClienteForm />} />
                        <Route path="/proveedores" element={<ProveedorList />} />
                        <Route path="/proveedores/nuevo" element={<ProveedorForm />} />
                        <Route path="/proveedores/editar/:id" element={<ProveedorForm />} />
                        <Route path="/usuarios" element={<UsuarioList />} />
                        <Route path="/usuarios/nuevo" element={<UsuarioForm />} />
                        <Route path="/usuarios/editar/:id" element={<UsuarioForm />} />
                        <Route path="/reportes" element={<ReporteList />} />
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                      </Routes>
                    </main>
                  </div>
                </div>
              </PrivateRoute>
            }
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
