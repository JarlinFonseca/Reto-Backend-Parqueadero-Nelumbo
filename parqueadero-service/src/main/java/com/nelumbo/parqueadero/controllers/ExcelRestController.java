package com.nelumbo.parqueadero.controllers;

import com.nelumbo.parqueadero.services.IExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ExcelRestController {

    private final IExcelService excelService;

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
