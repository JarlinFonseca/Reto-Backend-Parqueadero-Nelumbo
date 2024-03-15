package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.services.IExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
public class ExcelRestController {

    @Autowired
    private IExcelService excelService;

    @GetMapping("/exportar-excel")
    public ResponseEntity<String> exportarExcel() {
        List<String> datos = Arrays.asList("Dato 1", "Dato 2", "Dato 3"); // Datos a exportar
        String rutaArchivo = "datos.xlsx"; // Ruta donde se guardará el archivo Excel

        try {
            excelService.generarArchivoExcel(datos, rutaArchivo);
            return ResponseEntity.ok("Archivo Excel generado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al generar el archivo Excel: " + e.getMessage());
        }
    }

    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> descargarExcel() throws IOException {
        ByteArrayOutputStream outputStream;
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hoja1");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Hola, mundo!");

            outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            //workbook.close();
        }

        byte[] bytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("filename", "ejemplo.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }
}
