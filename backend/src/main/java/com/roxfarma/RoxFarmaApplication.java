package com.roxfarma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación RoxFarma.
 * 
 * Sistema de Gestión para Laboratorio Farmacéutico RoxFarma
 * Proyecto Integrador de Sistemas - Universidad Tecnológica del Perú (UTP)
 * 
 * Esta aplicación implementa:
 * - Arquitectura MVC (Modelo-Vista-Controlador)
 * - Patrón DAO (Data Access Object) con Spring Data JPA
 * - Principios SOLID
 * - Inyección de Dependencias
 * - Seguridad con Spring Security y JWT
 * - Desarrollo Guiado por Pruebas (TDD)
 * 
 * @SpringBootApplication es una anotación de conveniencia que combina:
 * - @Configuration: Marca la clase como fuente de definiciones de beans
 * - @EnableAutoConfiguration: Habilita la configuración automática de Spring Boot
 * - @ComponentScan: Escanea componentes en el paquete actual y subpaquetes
 * 
 * @author Equipo RoxFarma - UTP
 * @version 1.0.0
 */
@SpringBootApplication
public class RoxFarmaApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * 
     * Spring Boot:
     * 1. Escanea todas las clases con anotaciones (@Service, @Repository, @Controller)
     * 2. Crea instancias de estas clases (beans)
     * 3. Inyecta las dependencias automáticamente
     * 4. Configura el servidor web embebido (Tomcat)
     * 5. Inicia la aplicación en el puerto configurado (8080)
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(RoxFarmaApplication.class, args);
        
        System.out.println("\n" +
            "================================================================================\n" +
            "   SISTEMA ROXFARMA - INICIADO CORRECTAMENTE\n" +
            "   Proyecto Integrador de Sistemas - UTP\n" +
            "================================================================================\n" +
            "   Servidor: http://localhost:8080\n" +
            "   API REST: http://localhost:8080/api\n" +
            "   Base de Datos: MySQL (roxfarma_db)\n" +
            "================================================================================\n"
        );
    }
}
