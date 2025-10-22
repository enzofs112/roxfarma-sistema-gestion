# Sistema RoxFarma - Backend

Sistema de Gestión para Laboratorio Farmacéutico RoxFarma  
**Proyecto Integrador de Sistemas - Universidad Tecnológica del Perú (UTP)**

## 📋 Descripción

RoxFarma es un sistema web que automatiza y centraliza la gestión de pedidos, inventario y facturación para el laboratorio farmacéutico RoxFarma, reemplazando el sistema manual basado en hojas de Excel.

## 🏗️ Arquitectura

- **Patrón MVC** (Modelo-Vista-Controlador)
- **Patrón DAO** (Data Access Object) con Spring Data JPA
- **Arquitectura Multicapa**: Controller → Service → Repository → Database
- **Principios SOLID** aplicados en todo el código
- **Inyección de Dependencias** con Spring Framework

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** (Hibernate)
- **Spring Security** con JWT
- **MySQL 8.0**
- **Maven** (gestión de dependencias)
- **Lombok** (reducción de código boilerplate)
- **iText PDF** (generación de boletas)
- **JUnit 5 & Mockito** (pruebas unitarias)

## 📦 Estructura del Proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/utp/roxfarma/
│   │   │   ├── model/          # Entidades JPA
│   │   │   ├── repository/     # Repositorios (DAO)
│   │   │   ├── service/        # Lógica de negocio
│   │   │   ├── controller/     # Controladores REST
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── config/         # Configuraciones
│   │   │   ├── exception/      # Excepciones personalizadas
│   │   │   ├── util/           # Utilidades
│   │   │   └── RoxFarmaApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   └── test/
│       └── java/com/utp/roxfarma/  # Pruebas unitarias
├── pom.xml
└── README.md
```

## 🚀 Instalación y Configuración

### Prerrequisitos

1. **Java 17** o superior
2. **Maven 3.8+**
3. **MySQL 8.0**
4. **IDE** (IntelliJ IDEA, Eclipse, VS Code)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <url-repositorio>
   cd roxfarma/backend
   ```

2. **Crear base de datos MySQL**
   ```sql
   CREATE DATABASE roxfarma_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configurar credenciales de base de datos**
   
   Editar `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/roxfarma_db
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```

4. **Instalar dependencias**
   ```bash
   mvn clean install
   ```

5. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

6. **Verificar que el servidor esté corriendo**
   
   Abrir navegador en: `http://localhost:8080`

## 🔐 Seguridad

- **Autenticación**: JWT (JSON Web Tokens)
- **Autorización**: Basada en roles (ADMINISTRADOR, TRABAJADOR)
- **Cifrado de contraseñas**: BCrypt con factor de trabajo 12
- **Protección CSRF**: Deshabilitado (API REST stateless)
- **CORS**: Configurado para desarrollo

## 📊 Roles y Permisos

### Administrador
- Acceso completo a todos los módulos
- Gestión de usuarios, productos, clientes, proveedores
- Acceso a reportes y estadísticas

### Trabajador
- Registro de ventas y pedidos
- Consulta de inventario
- Sin acceso a gestión de usuarios ni reportes

## 🧪 Pruebas

Ejecutar pruebas unitarias:
```bash
mvn test
```

Ejecutar con cobertura:
```bash
mvn test jacoco:report
```

## 📝 API REST Endpoints

### Autenticación
- `POST /api/auth/login` - Iniciar sesión

### Productos
- `GET /api/productos` - Listar productos
- `POST /api/productos` - Crear producto (ADMIN)
- `PUT /api/productos/{id}` - Actualizar producto (ADMIN)
- `DELETE /api/productos/{id}` - Eliminar producto (ADMIN)

### Ventas
- `POST /api/ventas` - Registrar venta
- `GET /api/ventas` - Listar ventas
- `GET /api/ventas/{id}/boleta` - Descargar boleta PDF

### Pedidos
- `POST /api/pedidos` - Crear pedido
- `PUT /api/pedidos/{id}/estado` - Actualizar estado

### Dashboard
- `GET /api/dashboard/alertas` - Obtener alertas de inventario

## 📖 Documentación Adicional

Ver `EXPLICACION_ACADEMICA.txt` en la raíz del proyecto para:
- Explicación detallada de MVC, DAO, SOLID
- Ejemplos de Inyección de Dependencias
- Guía de TDD
- Arquitectura de seguridad

## 👥 Equipo de Desarrollo

Proyecto Integrador de Sistemas - UTP

## 📄 Licencia

Proyecto académico - Universidad Tecnológica del Perú
