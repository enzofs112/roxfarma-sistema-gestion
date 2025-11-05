import axios, { AxiosInstance, AxiosResponse, AxiosError } from 'axios';

/**
 * Configuración centralizada de Axios para comunicación con el backend.
 * 
 * Características:
 * - Base URL configurable
 * - Interceptores para token JWT automático
 * - Manejo de errores 401 (redirección a login)
 * - Headers por defecto
 */

// Crear instancia de Axios con configuración base
const api: AxiosInstance = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 segundos de timeout
});

/**
 * Interceptor de peticiones (request).
 * Agrega automáticamente el token JWT a todas las peticiones.
 */
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('✅ Token agregado a la petición:', token.substring(0, 20) + '...');
    } else if (!token) {
      console.warn('⚠️ No hay token en localStorage');
    }
    
    // Log de peticiones en desarrollo
    if (process.env.NODE_ENV === 'development') {
      console.log(`🚀 ${config.method?.toUpperCase()} ${config.url}`);
    }
    
    return config;
  },
  (error) => {
    console.error('❌ Error en petición:', error);
    return Promise.reject(error);
  }
);

/**
 * Interceptor de respuestas (response).
 * Maneja errores globales y redirecciones automáticas.
 */
api.interceptors.response.use(
  (response: AxiosResponse) => {
    // Log de respuestas exitosas en desarrollo
    if (process.env.NODE_ENV === 'development') {
      console.log(`✅ ${response.status} ${response.config.url}`);
    }
    return response;
  },
  (error: AxiosError) => {
    console.error('❌ Error en respuesta:', error);
    
    if (error.response?.status === 401) {
      // Token expirado o inválido - redirigir a login
      localStorage.removeItem('token');
      localStorage.removeItem('usuario');
      window.location.href = '/login';
    }
    
    if (error.response?.status === 403) {
      // Sin permisos - mostrar mensaje
      console.warn('⚠️ Sin permisos para acceder a este recurso');
    }
    
    return Promise.reject(error);
  }
);

export default api;
