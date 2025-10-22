package com.roxfarma.controller;

import com.roxfarma.dto.VentaDTO;
import com.roxfarma.model.Venta;
import com.roxfarma.service.BoletaService;
import com.roxfarma.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Ventas, para los users
 * @author Sistema RoxFarma
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VentaController {
    
    private final VentaService ventaService;
    private final BoletaService boletaService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Venta> registrarVenta(@Valid @RequestBody VentaDTO dto) {
        log.info("POST /api/ventas - Registrando venta");
        Venta venta = ventaService.registrarVenta(dto);
        return ResponseEntity.ok(venta);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<List<Venta>> listarVentas() {
        log.info("GET /api/ventas");
        List<Venta> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<Venta> obtenerVenta(@PathVariable Long id) {
        log.info("GET /api/ventas/{}", id);
        Venta venta = ventaService.obtenerVentaPorId(id);
        return ResponseEntity.ok(venta);
    }
    
    @GetMapping("/{id}/boleta")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'TRABAJADOR')")
    public ResponseEntity<byte[]> descargarBoleta(@PathVariable Long id) {
        log.info("GET /api/ventas/{}/boleta", id);
        
        byte[] pdfBytes = boletaService.generarBoletaPDF(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "boleta-" + id + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
