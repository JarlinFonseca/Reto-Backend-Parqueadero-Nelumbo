package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.services.IExcelService;
import com.nelumbo.parqueadero.services.IHistorialService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExcelServiceImpl implements IExcelService {



    @Override
    public void generarArchivoExcel(List<String> datos, String rutaArchivo) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Datos");

            int rowNum = 0;
            for (String dato : datos) {
                Row row = sheet.createRow(rowNum++);
                Cell cell = row.createCell(0);
                cell.setCellValue(dato);
            }

            try (FileOutputStream outputStream = new FileOutputStream(rutaArchivo)) {
                workbook.write(outputStream);
            }
        }
    }
}
