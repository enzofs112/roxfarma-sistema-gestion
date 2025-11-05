# RoxFarma Frontend

Frontend del Sistema de Gestión Farmacéutica RoxFarma desarrollado con React + TypeScript.

## 🚀 Tecnologías

- React 18
- TypeScript
- React Router v6
- Axios
- Material-UI
- Context API

## 📦 Instalación

```bash
# Instalar dependencias
npm install

# Iniciar en desarrollo
npm start

# Compilar para producción
npm run build
```

## 🔧 Configuración

Crear archivo `.env` en la raíz del proyecto:

```
REACT_APP_API_URL=http://localhost:8080/api
```

## 👥 Usuarios de Prueba

- **Administrador**: admin / admin123
- **Trabajador**: trabajador1 / trabajador123

## 📁 Estructura

```
src/
├── components/       # Componentes React
│   ├── auth/        # Login y autenticación
│   ├── common/      # Componentes reutilizables
│   └── dashboard/   # Dashboard principal
├── context/         # Contextos (Auth)
├── services/        # Servicios API
├── types/           # Tipos TypeScript
├── App.tsx          # Componente principal
└── index.tsx        # Punto de entrada
```

## ✨ Funcionalidades Implementadas

- ✅ Autenticación con JWT
- ✅ Dashboard principal
- ✅ Rutas protegidas
- ✅ Manejo de roles (Administrador/Trabajador)
- ✅ Interceptores de Axios
- ✅ Context API para estado global

## 🔜 Próximas Funcionalidades

- Gestión de productos
- Registro de ventas
- Gestión de pedidos
- Alertas de inventario
- Reportes y estadísticas

## 🌐 API Backend

El frontend se comunica con el backend Spring Boot en `http://localhost:8080/api`

## 📝 Notas

- El proxy está configurado en `package.json` para desarrollo
- Los tokens JWT se guardan en localStorage
- Las rutas están protegidas con PrivateRoute
- El sistema redirige automáticamente a login si el token expira
