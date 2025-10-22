package com.roxfarma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación
 * Sistema de Gestión para Laboratorio Farmacéutico RoxFarma
 * Proyecto Integrador de Sistemas - Universidad Tecnológica del Perú (UTP)
 * @author grupo2
 * @version 1.0.0
 */
@SpringBootApplication
public class RoxFarmaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoxFarmaApplication.class, args);
        
        System.out.println("\n" +
            "================================================================================\n" +
            "   SISTEMA ROXFARMA - INICIADO CORRECTAMENTE\n" +
            "   Proyecto Integrador de Sistemas\n" +
            "================================================================================\n" +
            "   Servidor: http://localhost:8080\n" +
            "   API REST: http://localhost:8080/api\n" +
            "   Base de Datos: MySQL (roxfarma_db)\n" +
            "================================================================================\n"
        );
    }
}
