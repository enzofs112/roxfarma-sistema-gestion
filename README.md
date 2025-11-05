# 🏥 Sistema de Gestión Farmacéutica RoxFarma

Sistema completo de gestión farmacéutica desarrollado con Spring Boot (Backend) y React + TypeScript (Frontend).

## 📋 Características

- ✅ Gestión de productos e inventario
- ✅ Control de ventas con generación de boletas PDF
- ✅ Gestión de pedidos a proveedores
- ✅ Administración de clientes y proveedores
- ✅ Alertas de stock bajo y productos próximos a vencer
- ✅ Dashboard con estadísticas en tiempo real
- ✅ Autenticación y autorización con JWT
- ✅ Roles de usuario (Administrador y Trabajador)
- ✅ Auditoría de operaciones

## 🛠️ Tecnologías

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL 8
- iText PDF
- Maven

### Frontend
- React 18
- TypeScript
- React Router
- Axios
- CSS3

## 📦 Requisitos Previos

- Java JDK 17 o superior
- Node.js 16 o superior
- MySQL 8 o superior
- Maven 3.6 o superior

## 🚀 Instalación y Configuración

### 1. Configurar Base de Datos

```bash
# Iniciar MySQL
mysql -u root -p

# Ejecutar el script de base de datos
mysql -u root -p < database/schema.sql
```

### 2. Configurar Backend

```bash
# Navegar a la carpeta backend
cd backend

# Editar application.properties si es necesario
# Cambiar la contraseña de MySQL en:
# spring.datasource.password=tu_password

# Compilar el proyecto
mvn clean install

# Ejecutar el backend
mvn spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

### 3. Configurar Frontend

```bash
# Navegar a la carpeta frontend
cd frontend

# Instalar dependencias
npm install

# Ejecutar el frontend
npm start
```

El frontend estará disponible en: `http://localhost:3000`

## 👤 Usuarios por Defecto

### Administrador
- **Usuario:** `admin`
- **Contraseña:** `password123`
- **Rol:** ADMINISTRADOR
- **Acceso:** Todos los módulos

### Trabajadores
- **Usuario:** `jperez` (Juan Pérez Torres)
- **Contraseña:** `password123`
- **Rol:** TRABAJADOR

- **Usuario:** `mgarcia` (María García López)
- **Contraseña:** `password123`
- **Rol:** TRABAJADOR

**Nota:** Todos los trabajadores tienen acceso a: Dashboard, Productos (lectura), Ventas, Pedidos

## 📁 Estructura del Proyecto

```
roxfarma-sistema-gestion/
├── backend/                    # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/roxfarma/
│   │   │   │   ├── config/    # Configuración de seguridad
│   │   │   │   ├── controller/# Controladores REST
│   │   │   │   ├── dto/       # Data Transfer Objects
│   │   │   │   ├── exception/ # Excepciones personalizadas
│   │   │   │   ├── model/     # Entidades JPA
│   │   │   │   ├── repository/# Repositorios
│   │   │   │   ├── security/  # JWT y seguridad
│   │   │   │   ├── service/   # Servicios de negocio
│   │   │   │   └── util/      # Utilidades
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── logback-spring.xml
│   │   └── test/              # Pruebas unitarias
│   └── pom.xml
│
├── frontend/                   # Frontend React
│   ├── src/
│   │   ├── components/
│   │   │   ├── auth/          # Login
│   │   │   ├── common/        # Componentes comunes
│   │   │   ├── dashboard/     # Dashboard
│   │   │   ├── productos/     # Gestión de productos
│   │   │   ├── ventas/        # Gestión de ventas
│   │   │   ├── pedidos/       # Gestión de pedidos
│   │   │   ├── clientes/      # Gestión de clientes
│   │   │   ├── proveedores/   # Gestión de proveedores
│   │   │   ├── reportes/      # Gestión de reportes
│   │   │   └── usuarios/      # Gestión de usuarios
│   │   ├── context/           # Context API
│   │   ├── services/          # Servicios API
│   │   ├── types/             # TypeScript types
│   │   ├── App.tsx
│   │   └── index.tsx
│   └── package.json
│
├── database/                   # Scripts SQL
│   └── schema.sql
│
└── README.md
```

## 🔑 Endpoints Principales del API

### Autenticación
- `POST /api/auth/login` - Iniciar sesión

### Productos
- `GET /api/productos` - Listar productos
- `POST /api/productos` - Crear producto (Admin)
- `PUT /api/productos/{id}` - Actualizar producto (Admin)
- `DELETE /api/productos/{id}` - Eliminar producto (Admin)
- `GET /api/productos/alertas/stock-bajo` - Productos con stock bajo
- `GET /api/productos/alertas/proximos-vencer` - Productos próximos a vencer

### Ventas
- `POST /api/ventas` - Registrar venta
- `GET /api/ventas` - Listar ventas
- `GET /api/ventas/{id}/boleta` - Descargar boleta PDF

### Pedidos
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos` - Listar pedidos
- `PUT /api/pedidos/{id}/estado` - Actualizar estado

### Dashboard
- `GET /api/dashboard/alertas` - Obtener alertas
- `GET /api/dashboard/estadisticas` - Obtener estadísticas

## 🎯 Funcionalidades por Rol

### Administrador
- Todas las funcionalidades del sistema
- Gestión de usuarios
- Gestión de productos (CRUD completo)
- Gestión de clientes y proveedores
- Acceso a reportes

### Trabajador
- Visualización de productos
- Registro de ventas
- Gestión de pedidos
- Visualización de dashboard

## 📊 Características Técnicas

### Backend
- Arquitectura en capas (Controller, Service, Repository)
- Validación de datos con Bean Validation
- Manejo global de excepciones
- Logging con Logback
- Auditoría de operaciones
- Generación de PDFs con iText
- Seguridad con JWT
- CORS configurado

### Frontend
- Componentes reutilizables
- Rutas protegidas
- Manejo de estado con Context API
- Interceptores HTTP para JWT
- Validación de formularios
- Diseño responsive
- Alertas y notificaciones

## 🧪 Pruebas

```bash
# Ejecutar pruebas del backend
cd backend
mvn test

# Ejecutar pruebas del frontend
cd frontend
npm test
```

## 📝 Notas Importantes

1. **Seguridad**: En producción, cambiar la clave JWT en `application.properties`
2. **Base de Datos**: Asegurarse de que MySQL esté corriendo antes de iniciar el backend
3. **CORS**: El backend está configurado para aceptar peticiones desde `http://localhost:3000`
4. **Logs**: Los logs se guardan en `backend/logs/roxfarma.log`

## 👥 Equipo de Desarrollo

- Enzo Siapo Flores

## 📄 Licencia

Universidad Tecnológica del Perú (UTP) - Proyecto Integrador de Sistemas

---

**Versión:** 1.5.0
