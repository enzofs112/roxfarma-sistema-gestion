# 🏥 RoxFarma - Sistema de Gestión Farmacéutica

Sistema integral de gestión para farmacias desarrollado con Spring Boot y React + TypeScript.

## 📋 Descripción

RoxFarma es un sistema completo de gestión farmacéutica que permite:
- Control de inventario de medicamentos
- Gestión de ventas y generación de boletas
- Administración de pedidos a proveedores
- Control de usuarios con roles (Administrador/Trabajador)
- Reportes y auditoría de operaciones
- Alertas automáticas de stock bajo y productos próximos a vencer

## 🏗️ Arquitectura

### Backend
- **Framework**: Spring Boot 3.x
- **Base de datos**: MySQL 8.0
- **Seguridad**: JWT (JSON Web Tokens)
- **Arquitectura**: MVC + DAO (Patrón Repository)
- **Documentación API**: REST endpoints

### Frontend
- **Framework**: React 18 con TypeScript
- **UI Library**: Material-UI (MUI)
- **Estado**: Context API
- **HTTP Client**: Axios
- **Routing**: React Router v6

## 📂 Estructura del Proyecto

```
roxfarma/
├── backend/              # API REST Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/roxfarma/
│   │   │   │   ├── config/       # Configuración (Security, CORS)
│   │   │   │   ├── controller/   # Controladores REST
│   │   │   │   ├── dto/          # Data Transfer Objects
│   │   │   │   ├── exception/    # Excepciones personalizadas
│   │   │   │   ├── model/        # Entidades JPA
│   │   │   │   ├── repository/   # Repositorios DAO
│   │   │   │   └── service/      # Lógica de negocio
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
├── frontend/             # Aplicación React
│   ├── public/
│   ├── src/
│   │   ├── components/   # Componentes React
│   │   ├── context/      # Contextos (Auth, etc.)
│   │   ├── pages/        # Páginas/Vistas
│   │   ├── services/     # Servicios API
│   │   └── types/        # Tipos TypeScript
│   ├── package.json
│   └── tsconfig.json
├── database/             # Scripts SQL
│   ├── schema.sql
└── README.md
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- Java 17 o superior
- Maven 3.8+
- Node.js 16+ y npm
- MySQL 8.0
- XAMPP (opcional, para desarrollo local)

### Backend

1. **Configurar base de datos**:
   ```bash
   # Ejecutar schema.sql en MySQL
   mysql -u root -p < database/schema.sql
   ```

2. **Configurar application.properties**:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/roxfarma
   spring.datasource.username=root
   spring.datasource.password=tu_password
   ```

3. **Compilar y ejecutar**:
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

   El backend estará disponible en: `http://localhost:8080`

### Frontend

1. **Instalar dependencias**:
   ```bash
   cd frontend
   npm install
   ```

2. **Configurar variables de entorno** (crear `.env`):
   ```
   REACT_APP_API_URL=http://localhost:8080/api
   ```

3. **Ejecutar en desarrollo**:
   ```bash
   npm start
   ```

   El frontend estará disponible en: `http://localhost:3000`

## 👥 Usuarios por Defecto

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | admin123 | ADMINISTRADOR |
| trabajador1 | trabajador123 | TRABAJADOR |

## 🔐 Seguridad

- Autenticación mediante JWT
- Contraseñas cifradas con BCrypt
- Autorización basada en roles
- Sesiones stateless
- CORS configurado para desarrollo

## 📊 Funcionalidades Principales

### Módulo de Inventario
- ✅ CRUD de productos
- ✅ Control de stock automático
- ✅ Alertas de stock bajo (< 10 unidades)
- ✅ Alertas de vencimiento (30 días)
- ✅ Gestión de categorías

### Módulo de Ventas
- ✅ Registro de ventas
- ✅ Cálculo automático de IGV (18%)
- ✅ Generación de boletas PDF
- ✅ Actualización automática de inventario
- ✅ Gestión de clientes

### Módulo de Pedidos
- ✅ Gestión de pedidos a proveedores
- ✅ Estados: PENDIENTE → ENVIADO → RECIBIDO
- ✅ Actualización automática de stock al recibir
- ✅ Gestión de proveedores

### Módulo de Administración
- ✅ Gestión de usuarios
- ✅ Roles y permisos
- ✅ Auditoría de operaciones
- ✅ Reportes de ventas
- ✅ Dashboard con estadísticas

## 🧪 Testing

```bash
# Backend
cd backend
mvn test

# Frontend
cd frontend
npm test
```

## 📈 Estado del Proyecto

- ✅ Backend: 100% completado 
- 🚧 Frontend: En desarrollo 
- 📊 Progreso total: ~40%

## 🌳 Flujo de Git

```
main (rama principal estable)
├── backend (desarrollo del backend)
└── frontend (desarrollo del frontend)
```

## 👨‍💻 Equipo de Desarrollo

- Segundo Silva Ochoa
- Enzo Siapo Flores
- Jairo Yarleque Cornejo
- Angel Rivera Galdos 

## 📄 Licencia

Proyecto académico - Todos los derechos reservados.

---

**Desarrollado para el curso Integrador de Sistemas**
