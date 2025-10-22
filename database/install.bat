@echo off
REM ================================================================================
REM Script de instalación de base de datos RoxFarma para Windows
REM ================================================================================

echo.
echo ================================================================================
echo   INSTALACION DE BASE DE DATOS - SISTEMA ROXFARMA
echo ================================================================================
echo.

REM Verificar que MySQL esté corriendo
echo [1/4] Verificando que MySQL este corriendo...
netstat -an | find "3306" >nul
if errorlevel 1 (
    echo.
    echo [ERROR] MySQL no esta corriendo en el puerto 3306
    echo Por favor, inicia MySQL desde el panel de control de XAMPP
    echo.
    pause
    exit /b 1
)
echo [OK] MySQL esta corriendo en el puerto 3306
echo.

REM Buscar la ruta de mysql.exe en XAMPP
echo [2/4] Buscando instalacion de MySQL...
set MYSQL_PATH=
if exist "C:\xampp\mysql\bin\mysql.exe" set MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe
if exist "C:\XAMPP\mysql\bin\mysql.exe" set MYSQL_PATH=C:\XAMPP\mysql\bin\mysql.exe
if exist "%ProgramFiles%\xampp\mysql\bin\mysql.exe" set MYSQL_PATH=%ProgramFiles%\xampp\mysql\bin\mysql.exe

if "%MYSQL_PATH%"=="" (
    echo.
    echo [ERROR] No se encontro mysql.exe en las rutas comunes de XAMPP
    echo Por favor, ejecuta el script schema.sql manualmente desde phpMyAdmin
    echo.
    pause
    exit /b 1
)
echo [OK] MySQL encontrado en: %MYSQL_PATH%
echo.

REM Ejecutar el script SQL
echo [3/4] Ejecutando script SQL...
echo Por favor espera, esto puede tomar unos segundos...
echo.

"%MYSQL_PATH%" -u root --password= < schema.sql 2>error.log

if errorlevel 1 (
    echo.
    echo [ERROR] Hubo un problema al ejecutar el script SQL
    echo Revisa el archivo error.log para mas detalles
    echo.
    echo Alternativa: Ejecuta el script manualmente desde phpMyAdmin
    echo 1. Abre http://localhost/phpmyadmin
    echo 2. Crea la base de datos 'roxfarma_db'
    echo 3. Selecciona la base de datos
    echo 4. Ve a la pestana SQL
    echo 5. Copia y pega el contenido de schema.sql
    echo 6. Haz clic en Continuar
    echo.
    pause
    exit /b 1
)
echo [OK] Script SQL ejecutado exitosamente
echo.

REM Verificar la instalación
echo [4/4] Verificando instalacion...
"%MYSQL_PATH%" -u root --password= -e "USE roxfarma_db; SELECT COUNT(*) FROM producto;" >nul 2>&1

if errorlevel 1 (
    echo.
    echo [ADVERTENCIA] No se pudo verificar la instalacion
    echo Por favor, verifica manualmente en phpMyAdmin
    echo.
) else (
    echo [OK] Base de datos instalada correctamente
    echo.
)

REM Mostrar resumen
echo ================================================================================
echo   INSTALACION COMPLETADA
echo ================================================================================
echo.
echo Base de datos: roxfarma_db
echo Tablas creadas: 10
echo Datos de ejemplo: SI
echo.
echo CREDENCIALES DE ACCESO:
echo   Usuario: admin
echo   Contrasena: password123
echo   Rol: ADMINISTRADOR
echo.
echo   Usuario: jperez
echo   Contrasena: password123
echo   Rol: TRABAJADOR
echo.
echo Puedes verificar la instalacion en:
echo   http://localhost/phpmyadmin
echo.
echo Proximos pasos:
echo   1. Configurar application.properties en Spring Boot
echo   2. Ejecutar la aplicacion backend
echo   3. Probar la conexion a la base de datos
echo.
echo ================================================================================
echo.

pause
