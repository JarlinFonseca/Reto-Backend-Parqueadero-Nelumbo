package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.services.IExcelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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


    @Override
    @Async("asyncExecutor")
    public CompletableFuture<ExcelResponseDto> generarExcelReporte(List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez, List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos, List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado, List<VehiculoParqueadoResponseDto> vehiculosPorCoincidencia, GananciasResponseDto indicadorGanancias, String nombreParqueadero, String placa) throws IOException {
        if (indicadorVehiculoParqueadoPrimeraVez.isEmpty() &&
                indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos.isEmpty() &&
                indicadorVehiculosMasVecesRegistrado.isEmpty() &&
                indicadorGanancias == null &&
                vehiculosPorCoincidencia.isEmpty()) {
            ExcelResponseDto excelResponseDto = new ExcelResponseDto();
            excelResponseDto.setMensaje("Excel no generado, debido a que no hay indicadores para el parqueadero y coincidencia solicitada");
            excelResponseDto.setIndicadores("No hay indicadores");
            return CompletableFuture.completedFuture(excelResponseDto);
        }

        try (Workbook workbook = new XSSFWorkbook()) {

            // Establecer el estilo para la cabecera principal
            CellStyle mainHeaderStyle = getMainHeaderStyle(workbook);
            // Establecer el estilo para las cabeceras de las columnas
            CellStyle columnHeaderStyle = getColumnHeaderStyle(workbook);
            // Establecer el estilo para los datos
            CellStyle dataStyle = getDataStyle(workbook);

            long numero = (long) (Math.random() * 999999999) + 1;
            String rutaArchivo = "reporte"+numero+".xlsx";


            // Crear las cabeceras de las columnas
            String[] headers = {"ID", "Placa", "Fecha de Ingreso"};
            String[] headers2 = {"ID", "Placa", "Cantidad veces registrado"};
            String[] headers3 = {"Hoy", "Semana", "Mes", "Año"};

            ExcelResponseDto excelResponseDto = new ExcelResponseDto();

            if(!indicadorVehiculoParqueadoPrimeraVez.isEmpty()){
                Sheet sheet = workbook.createSheet("Parqueados Primera Vez");
                createMainHeader(sheet, workbook, "Vehículos Parqueados por Primera Vez", mainHeaderStyle, 3);
                createColumnHeaders(sheet, workbook, headers, columnHeaderStyle);
                agregarDatosAExcel(sheet, indicadorVehiculoParqueadoPrimeraVez, dataStyle,null );
                concatenarIndicador(excelResponseDto, 1);
            }

            if(!indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos.isEmpty()){
                Sheet sheet2 = workbook.createSheet("Vehiculos más registrados (D.P)");
                createMainHeader(sheet2, workbook, "Vehículos más veces registrados en diferentes parqueaderos", mainHeaderStyle, 3);
                createColumnHeaders(sheet2, workbook, headers2, columnHeaderStyle);
                agregarDatosAExcel(sheet2, indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos, dataStyle, null );
                concatenarIndicador(excelResponseDto, 2);
            }

            if(!indicadorVehiculosMasVecesRegistrado.isEmpty()){
                Sheet sheet3 = workbook.createSheet("Vehiculos más registrados en P");
                createMainHeader(sheet3, workbook, "Vehículos más veces registrados en un parqueadero: "+nombreParqueadero, mainHeaderStyle, 3);
                createColumnHeaders(sheet3, workbook, headers2, columnHeaderStyle);
                agregarDatosAExcel(sheet3, indicadorVehiculosMasVecesRegistrado, dataStyle, null );
                concatenarIndicador(excelResponseDto, 3);
            }

            if(indicadorGanancias!=null){
                Sheet sheet4 = workbook.createSheet("Ganancias de un parqueadero");
                createColumnHeaders(sheet4, workbook, headers3, columnHeaderStyle);
                createMainHeader(sheet4, workbook, "Ganancias de un parqueadero: "+nombreParqueadero, mainHeaderStyle, 0);
                agregarDatosAExcel(sheet4, null, dataStyle, indicadorGanancias );
                concatenarIndicador(excelResponseDto, 4);
            }

            if(!vehiculosPorCoincidencia.isEmpty()){
                Sheet sheet5 = workbook.createSheet("Coincidencias de placa");
                createMainHeader(sheet5, workbook, "Coincidencias de la placa: "+placa, mainHeaderStyle, 3);
                createColumnHeaders(sheet5, workbook, headers, columnHeaderStyle);
                agregarDatosAExcel(sheet5, vehiculosPorCoincidencia, dataStyle, null);
                concatenarIndicador(excelResponseDto, 5);
            }

            try (FileOutputStream outputStream = new FileOutputStream(rutaArchivo)) {
                workbook.write(outputStream);
            }


            excelResponseDto.setMensaje("Excel generado correctamente");

            return CompletableFuture.completedFuture(excelResponseDto);
        }
    }

    private void concatenarIndicador(ExcelResponseDto excelResponseDto, int numeroIndicador) {
        String nuevoIndicador = "Indicador " + numeroIndicador;
        if (excelResponseDto.getIndicadores()==null || excelResponseDto.getIndicadores().isEmpty() ) {
            excelResponseDto.setIndicadores(nuevoIndicador);
        } else {
            excelResponseDto.setIndicadores(excelResponseDto.getIndicadores() + ", " + nuevoIndicador);
        }
    }

    private void agregarDatosAExcel(Sheet sheet, List<?> data, CellStyle dataStyle, GananciasResponseDto ganancias ) {
        int rowNum = 2;
        if(ganancias != null){
            String[] valores = {ganancias.getHoy(), ganancias.getSemana(), ganancias.getMes(), ganancias.getAnio()};
            agregarDatosUnaColumna(sheet, dataStyle, valores);
        }
        if(data!=null){
            for (Object obj : data) {
                Row row = sheet.createRow(rowNum++);
                if (obj instanceof VehiculoParqueadoResponseDto) {
                    VehiculoParqueadoResponseDto indicador1 = (VehiculoParqueadoResponseDto) obj;
                    Object[] valoresIndicador1 = {indicador1.getId(), indicador1.getPlaca(),indicador1.getFechaIngreso()};
                    agregarDatosDesdeArreglo(dataStyle, valoresIndicador1, row);
                }else if(obj instanceof IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto){
                    IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto indicador2 = (IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto) obj;
                    Object[] valoresIndicador2 = {indicador2.getVehiculo().getId(), indicador2.getVehiculo().getPlaca(),indicador2.getCantidadVecesRegistrado()};
                    agregarDatosDesdeArreglo(dataStyle, valoresIndicador2, row);
                }else if(obj instanceof IndicadorVehiculosMasVecesRegistradoResponseDto){
                    IndicadorVehiculosMasVecesRegistradoResponseDto indicador3 = (IndicadorVehiculosMasVecesRegistradoResponseDto) obj;
                    Object[] valoresIndicador3 = {indicador3.getVehiculo().getId(), indicador3.getVehiculo().getPlaca(),indicador3.getCantidadVecesRegistrado()};
                    agregarDatosDesdeArreglo(dataStyle, valoresIndicador3, row);
                }

            }
        }

    }

    private void agregarDatosUnaColumna(Sheet sheet, CellStyle dataStyle, String[] valores) {
        for (int i = 0; i < valores.length; i++) {
            Row row = sheet.createRow(i+1);
            Cell cell = row.createCell(0);
            cell.setCellStyle(dataStyle);
            cell.setCellValue(valores[i]);
        }
    }


    private void agregarDatosDesdeArreglo(CellStyle dataStyle, Object[] data,  Row row) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int cellNum = 0;
        for (Object value : data) {
            if (value != null) {
                Cell cell = row.createCell(cellNum++);
                cell.setCellStyle(dataStyle);
                if (value instanceof Date) {
                    cell.setCellValue(formatter.format((Date) value));
                } else if(value instanceof Long) {
                    cell.setCellValue((Long)value);
                }else{
                    cell.setCellValue(value.toString());
                }
            }
        }
    }

    public static void createMainHeader(Sheet sheet, Workbook workbook, String mainHeaderText, CellStyle mainHeaderStyle, int numCellsToMerge) {
        Row mainHeaderRow = sheet.createRow(0);
        Cell mainHeaderCell = mainHeaderRow.createCell(0);
        mainHeaderCell.setCellValue(mainHeaderText);
        mainHeaderCell.setCellStyle(mainHeaderStyle);

        // Fusionar un número variable de celdas, solo si numCellsToMerge es mayor que 0
        if (numCellsToMerge > 0) {
            sheet.addMergedRegion(new CellRangeAddress(
                    0, // De la primera fila
                    0, // Hasta la primera fila
                    0, // De la primera columna
                    numCellsToMerge - 1 // Hasta la última columna a fusionar
            ));
        }
    }

    // Método para crear las cabeceras de las columnas
    private static void createColumnHeaders(Sheet sheet, Workbook workbook, String[] headers, CellStyle columnHeaderStyle) {
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(columnHeaderStyle);
        }
    }

    // Obtener el estilo para la cabecera principal fusionada con las tres primeras columnas
    private static CellStyle getMainHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // Obtener el estilo para las cabeceras de las columnas
    private static CellStyle getColumnHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // Obtener el estilo para los datos
    private static CellStyle getDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

}
