package com.roxfarma.security;

import com.roxfarma.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para gestión de tokens JWT (JSON Web Tokens).
 * 
 * Responsabilidades:
 * - Generar tokens JWT para usuarios autenticados
 * - Validar tokens JWT
 * - Extraer información de los tokens (usuario, rol, expiración)
 * 
 * Estructura de un JWT:
 * Header.Payload.Signature
 * 
 * Ejemplo:
 * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
 * eyJzdWIiOiJhZG1pbiIsInJvbCI6IkFETUlOSVNUUkFET1IifQ.
 * SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
 * 
 * @author Sistema RoxFarma
 */
@Service
public class JwtService {
    
    /**
     * Clave secreta para firmar los tokens.
     * Se lee desde application.properties.
     * IMPORTANTE: En producción debe ser una clave larga y segura.
     */
    @Value("${jwt.secret}")
    private String secretKey;
    
    /**
     * Tiempo de expiración del token en milisegundos.
     * Por defecto: 86400000 ms = 24 horas
     */
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * Genera un token JWT para un usuario.
     * 
     * El token contiene:
     * - subject: nombre de usuario
     * - claims: rol y nombre completo
     * - issuedAt: fecha de emisión
     * - expiration: fecha de expiración
     * - signature: firma con clave secreta
     * 
     * @param usuario Usuario para el que se genera el token
     * @return Token JWT como String
     */
    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        claims.put("nombre", usuario.getNombre());
        claims.put("idUsuario", usuario.getIdUsuario());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getUsuario())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extrae el nombre de usuario del token.
     * 
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String extraerUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrae un claim específico del token.
     * 
     * @param token Token JWT
     * @param claimsResolver Función para extraer el claim
     * @return Valor del claim
     */
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Valida si el token es válido para el usuario.
     * 
     * Verifica:
     * 1. El usuario del token coincide con el UserDetails
     * 2. El token no ha expirado
     * 
     * @param token Token JWT
     * @param userDetails Detalles del usuario
     * @return true si el token es válido, false si no
     */
    public boolean validarToken(String token, UserDetails userDetails) {
        final String usuario = extraerUsuario(token);
        return (usuario.equals(userDetails.getUsername()) && !tokenExpirado(token));
    }
    
    /**
     * Verifica si el token ha expirado.
     * 
     * @param token Token JWT
     * @return true si ha expirado, false si aún es válido
     */
    public boolean tokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }
    
    /**
     * Extrae la fecha de expiración del token.
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extrae todos los claims del token.
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extraerTodosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Obtiene la clave de firma a partir de la clave secreta.
     * 
     * @return Clave de firma
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
