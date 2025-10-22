import React, { createContext, useContext, useState, useEffect } from 'react';
import { Usuario, AuthResponse } from '../types';
import api from '../services/api';

interface AuthContextType {
  usuario: Usuario | null;
  token: string | null;
  login: (usuario: string, contrasena: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedUsuario = localStorage.getItem('usuario');
    
    if (savedToken && savedUsuario) {
      setToken(savedToken);
      setUsuario(JSON.parse(savedUsuario));
    }
  }, []);

  const login = async (usuario: string, contrasena: string) => {
    const response = await api.post<AuthResponse>('/auth/login', { usuario, contrasena });
    const { token, ...userData } = response.data;
    
    setToken(token);
    setUsuario(userData as Usuario);
    
    localStorage.setItem('token', token);
    localStorage.setItem('usuario', JSON.stringify(userData));
  };

  const logout = () => {
    setToken(null);
    setUsuario(null);
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
  };

  const value = {
    usuario,
    token,
    login,
    logout,
    isAuthenticated: !!token,
    isAdmin: usuario?.rol === 'ADMINISTRADOR'
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};