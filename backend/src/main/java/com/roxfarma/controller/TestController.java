package com.roxfarma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.roxfarma.repository.UsuarioRepository;
import com.roxfarma.model.Usuario;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador temporal para resetear contraseñas
 * ELIMINAR EN PRODUCCIÓN
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/reset-passwords")
    public Map<String, String> resetPasswords() {
        Map<String, String> result = new HashMap<>();
        
        try {
            // Generar hash para "password123"
            String hashedPassword = passwordEncoder.encode("password123");
            
            // Actualizar todos los usuarios
            Usuario admin = usuarioRepository.findByUsuario("admin").orElse(null);
            Usuario jperez = usuarioRepository.findByUsuario("jperez").orElse(null);
            Usuario mgarcia = usuarioRepository.findByUsuario("mgarcia").orElse(null);
            
            if (admin != null) {
                admin.setContrasena(hashedPassword);
                usuarioRepository.save(admin);
                result.put("admin", "✓ Actualizado");
            } else {
                result.put("admin", "✗ No encontrado");
            }
            
            if (jperez != null) {
                jperez.setContrasena(hashedPassword);
                usuarioRepository.save(jperez);
                result.put("jperez", "✓ Actualizado");
            } else {
                result.put("jperez", "✗ No encontrado");
            }
            
            if (mgarcia != null) {
                mgarcia.setContrasena(hashedPassword);
                usuarioRepository.save(mgarcia);
                result.put("mgarcia", "✓ Actualizado");
            } else {
                result.put("mgarcia", "✗ No encontrado");
            }
            
            result.put("mensaje", "Contraseñas actualizadas. Usa: password123");
            result.put("hash_generado", hashedPassword);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/test-password")
    public Map<String, Object> testPassword(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Usuario usuario = usuarioRepository.findByUsuario(username).orElse(null);
            
            if (usuario == null) {
                result.put("encontrado", false);
                result.put("mensaje", "Usuario no encontrado");
                return result;
            }
            
            boolean matches = passwordEncoder.matches(password, usuario.getContrasena());
            
            result.put("encontrado", true);
            result.put("usuario", username);
            result.put("password_coincide", matches);
            result.put("hash_en_bd", usuario.getContrasena());
            result.put("password_probada", password);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
