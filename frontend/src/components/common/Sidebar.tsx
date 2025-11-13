import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Rol } from '../../types';
import './Sidebar.css';

const Sidebar: React.FC = () => {
  const { hasRole } = useAuth();
  const location = useLocation();
  const [isOpen, setIsOpen] = useState(false);

  const isActive = (path: string) => location.pathname === path;

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };

  const closeSidebar = () => {
    setIsOpen(false);
  };

  // Cerrar sidebar al cambiar de ruta en móviles
  useEffect(() => {
    setIsOpen(false);
  }, [location.pathname]);

  // Cerrar sidebar al hacer clic fuera en móviles
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 768) {
        setIsOpen(false);
      }
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <>
      {/* Botón hamburguesa para móviles */}
      <button className="sidebar-toggle" onClick={toggleSidebar} aria-label="Toggle menu">
        <span className="icon">{isOpen ? '✕' : '☰'}</span>
      </button>

      {/* Overlay para cerrar sidebar en móviles */}
      <div 
        className={`sidebar-overlay ${isOpen ? 'active' : ''}`} 
        onClick={closeSidebar}
      />

      <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
        <nav className="sidebar-nav">
        <Link 
          to="/dashboard" 
          className={`nav-item ${isActive('/dashboard') ? 'active' : ''}`}
        >
          <span className="icon">📊</span>
          Dashboard
        </Link>

        <Link 
          to="/productos" 
          className={`nav-item ${isActive('/productos') ? 'active' : ''}`}
        >
          <span className="icon">💊</span>
          Productos
        </Link>

        <Link 
          to="/ventas" 
          className={`nav-item ${isActive('/ventas') ? 'active' : ''}`}
        >
          <span className="icon">🛒</span>
          Ventas
        </Link>

        <Link 
          to="/pedidos" 
          className={`nav-item ${isActive('/pedidos') ? 'active' : ''}`}
        >
          <span className="icon">📦</span>
          Pedidos
        </Link>

        {hasRole(Rol.ADMINISTRADOR) && (
          <>
            <Link 
              to="/clientes" 
              className={`nav-item ${isActive('/clientes') ? 'active' : ''}`}
            >
              <span className="icon">👥</span>
              Clientes
            </Link>

            <Link 
              to="/proveedores" 
              className={`nav-item ${isActive('/proveedores') ? 'active' : ''}`}
            >
              <span className="icon">🏢</span>
              Proveedores
            </Link>

            <Link 
              to="/usuarios" 
              className={`nav-item ${isActive('/usuarios') ? 'active' : ''}`}
            >
              <span className="icon">👤</span>
              Usuarios
            </Link>

            <Link 
              to="/reportes" 
              className={`nav-item ${isActive('/reportes') ? 'active' : ''}`}
            >
              <span className="icon">📈</span>
              Reportes
            </Link>
          </>
        )}
      </nav>
    </aside>
    </>
  );
};

export default Sidebar;
