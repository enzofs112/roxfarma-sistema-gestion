package com.roxfarma.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roxfarma.dto.AuthResponse;
import com.roxfarma.dto.LoginRequest;
import com.roxfarma.model.Rol;
import com.roxfarma.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para AuthController.
 * Valida los endpoints de autenticación.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsuario("admin");
        loginRequest.setContrasena("admin123");

        authResponse = new AuthResponse();
        authResponse.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        authResponse.setTipo("Bearer");
        authResponse.setIdUsuario(1L);
        authResponse.setNombre("Administrador");
        authResponse.setRol(Rol.ADMINISTRADOR);
    }

    @Test
    void deberiaAutenticarUsuarioCorrectamente() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.nombre").value("Administrador"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));
    }

    @Test
    void deberiaRechazarLoginConCredencialesInvalidas() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void deberiaValidarCamposRequeridos() throws Exception {
        // Arrange
        LoginRequest loginInvalido = new LoginRequest();
        loginInvalido.setUsuario("");
        loginInvalido.setContrasena("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornarTokenJWTValido() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString());
    }
}
