package com.roxfarma.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.roxfarma.exception.ResourceNotFoundException;
import com.roxfarma.model.DetalleVenta;
import com.roxfarma.model.Venta;
import com.roxfarma.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generación de boletas PDF.
 * - Información de RoxFarma, cliente, productos y totales
 * - Calcular subtotal, IGV y total
 * Usa iText7 para generación de PDFs.
 * @author grupo2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BoletaService {
    
    private final VentaRepository ventaRepository;
    
    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    @Transactional(readOnly = true)
    public byte[] generarBoletaPDF(Long idVenta) {
        log.info("Generando boleta PDF para venta ID: {}", idVenta);
        
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + idVenta));
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            agregarEncabezado(document);
            agregarInformacionVenta(document, venta);
            agregarTablaProductos(document, venta);
            agregarTotales(document, venta);
            agregarPiePagina(document);
            
            document.close();
            
            log.info("Boleta PDF generada exitosamente para venta ID: {}", idVenta);
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error al generar boleta PDF para venta ID: {}", idVenta, e);
            throw new RuntimeException("Error al generar boleta PDF: " + e.getMessage(), e);
        }
    }
    
    private void agregarEncabezado(Document document) {
        Paragraph titulo = new Paragraph("LABORATORIO ROXFARMA")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);
        
        Paragraph info = new Paragraph(
                "RUC: 20123456789\n" +
                "Av. Principal 123, Lima, Perú\n" +
                "Teléfono: (01) 234-5678"
        )
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(info);
        
        document.add(new Paragraph("\n"));
        
        Paragraph boletaTitulo = new Paragraph("BOLETA DE VENTA ELECTRÓNICA")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(boletaTitulo);
        
        document.add(new Paragraph("\n"));
    }
    
    private void agregarInformacionVenta(Document document, Venta venta) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        document.add(new Paragraph("Nº Boleta: " + String.format("B001-%08d", venta.getIdVenta())));
        document.add(new Paragraph("Fecha: " + venta.getFecha().format(formatter)));
        document.add(new Paragraph("Cliente: " + venta.getCliente().getNombre()));
        document.add(new Paragraph("Documento: " + venta.getCliente().getDocumento()));
        document.add(new Paragraph("Atendido por: " + venta.getUsuario().getNombre()));
        document.add(new Paragraph("\n"));
    }

    private void agregarTablaProductos(Document document, Venta venta) {
        float[] columnWidths = {4, 1, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        
        table.addHeaderCell("Producto");
        table.addHeaderCell("Cant.");
        table.addHeaderCell("P. Unit.");
        table.addHeaderCell("Subtotal");
        
        for (DetalleVenta detalle : venta.getDetalles()) {
            table.addCell(detalle.getProducto().getNombre());
            table.addCell(String.valueOf(detalle.getCantidad()));
            table.addCell("S/ " + detalle.getPrecio().setScale(2, RoundingMode.HALF_UP));
            
            BigDecimal subtotal = detalle.getPrecio()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            table.addCell("S/ " + subtotal);
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void agregarTotales(Document document, Venta venta) {
        BigDecimal subtotal = venta.getTotal()
                .divide(BigDecimal.ONE.add(IGV_RATE), 2, RoundingMode.HALF_UP);
        
        BigDecimal igv = venta.getTotal().subtract(subtotal);
        
        Paragraph pSubtotal = new Paragraph("Subtotal: S/ " + subtotal)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(pSubtotal);
        
        Paragraph pIgv = new Paragraph("IGV (18%): S/ " + igv.setScale(2, RoundingMode.HALF_UP))
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(pIgv);
        
        Paragraph pTotal = new Paragraph("TOTAL: S/ " + venta.getTotal().setScale(2, RoundingMode.HALF_UP))
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(pTotal);
    }
    
    private void agregarPiePagina(Document document) {
        document.add(new Paragraph("\n\n"));
        
        Paragraph gracias = new Paragraph("Gracias por su compra")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10);
        document.add(gracias);
    }
}
