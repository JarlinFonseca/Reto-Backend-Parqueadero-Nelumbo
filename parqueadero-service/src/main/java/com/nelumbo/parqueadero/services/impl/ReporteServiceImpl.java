package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.response.GananciasResponseDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoResponseDto;
import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.services.IHistorialService;
import com.nelumbo.parqueadero.services.IParqueaderoService;
import com.nelumbo.parqueadero.services.IParqueaderoVehiculoService;
import com.nelumbo.parqueadero.services.IReporteService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReporteServiceImpl implements IReporteService {

    private final IHistorialService historialService;
    private final IParqueaderoVehiculoService parqueaderoVehiculoService;
    private final IParqueaderoService parqueaderoService;

    @Override
    public void generarReporte(Long parqueaderoId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Establecer el estilo para la cabecera principal
            CellStyle mainHeaderStyle = getMainHeaderStyle2(workbook);
            // Establecer el estilo para las cabeceras de las columnas
            CellStyle columnHeaderStyle = getColumnHeaderStyle2(workbook);
            // Establecer el estilo para los datos
            CellStyle dataStyle = getDataStyle2(workbook);

            Sheet sheet = workbook.createSheet("Parqueados Primera Vez");

            Sheet sheet2 = workbook.createSheet("Mas Registrados Diferentes Parqueaderos");
            Sheet sheet3 = workbook.createSheet("Mas Registrados en un parqueadero");
            Sheet sheet4 = workbook.createSheet("Ganancias de un parqueadero");

            String nombreParqueadero = parqueaderoService.obtenerParqueaderoPorId(parqueaderoId).getNombre();

            // Crear la cabecera principal
            createMainHeader(sheet, workbook, "Vehículos Parqueados por Primera Vez", mainHeaderStyle);
            createMainHeader(sheet2, workbook, "Vehículos más veces registrados en diferentes parqueaderos", mainHeaderStyle);
            createMainHeader(sheet3, workbook, "Vehículos más veces registrados en un parqueadero: "+nombreParqueadero, mainHeaderStyle);
            createMainHeader(sheet4, workbook, "Ganancias de un parqueadero: "+nombreParqueadero, mainHeaderStyle);

            // Crear las cabeceras de las columnas
            String[] headers = {"ID", "Placa", "Fecha de Ingreso"};
            createColumnHeaders(sheet, workbook, headers, columnHeaderStyle);
            String[] headers2 = {"ID", "Placa", "Cantidad veces registrado"};
            createColumnHeaders(sheet2, workbook, headers2, columnHeaderStyle);
            createColumnHeaders(sheet3, workbook, headers2, columnHeaderStyle);

            String[] headers3 = {"Hoy", "Semana", "Mes", "Año"};
            createColumnHeaders(sheet4, workbook, headers3, columnHeaderStyle);
            String rutaArchivo = "reporte.xlsx";

            List<VehiculoParqueadoResponseDto> indicadorVehiculoParqueadoPrimeraVez = historialService.obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(parqueaderoId);
            List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez();
            List<IndicadorVehiculosMasVecesRegistradoResponseDto> indicadorVehiculosMasVecesRegistrado = parqueaderoVehiculoService.obtenerVehiculosMasVecesRegistradosParqueaderoPorId(parqueaderoId);
            GananciasResponseDto gananciasResponseDto = historialService.obtenerGanancias(parqueaderoId);



            // Establecer el estilo de las líneas de la tabla
            //  setTableBorders(sheet, workbook);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Formatear la fecha como una cadena en el formato deseado
           // String formattedDate = formatter.format(date);


             agregarDatosAExcel(sheet, indicadorVehiculoParqueadoPrimeraVez, dataStyle, formatter,null );
            agregarDatosAExcel(sheet2, indicadorVehiculosMasVecesRegistradosDiferentesParqueaderos, dataStyle, formatter, null );
            agregarDatosAExcel(sheet3, indicadorVehiculosMasVecesRegistrado, dataStyle, formatter, null );
            agregarDatosAExcel(sheet4, null, dataStyle, formatter, gananciasResponseDto );
            //setTableBorders(sheet, workbook);



            try (FileOutputStream outputStream = new FileOutputStream(rutaArchivo)) {
                workbook.write(outputStream);
            }
        }


    }

    private void agregarDatosAExcel(Sheet sheet, List<?> data, CellStyle dataStyle, SimpleDateFormat formatter,  GananciasResponseDto ganancias ) {
        int rowNum = 2;
        if(ganancias != null){
            Row row = sheet.createRow(1);
            Cell cell1 = row.createCell(0);
            cell1.setCellStyle(dataStyle);
            cell1.setCellValue(ganancias.getHoy());


            Cell cell2 = row.createCell(1);
            cell2.setCellStyle(dataStyle);
            cell2.setCellValue(ganancias.getSemana());


            Cell cell3 = row.createCell(2);
            cell3.setCellStyle(dataStyle);
            cell3.setCellValue(ganancias.getMes());

            Cell cell4 = row.createCell(3);
            cell4.setCellStyle(dataStyle);
            cell4.setCellValue(ganancias.getAnio());

        }
        if(data!=null){
        for (Object obj : data) {
            Row row = sheet.createRow(rowNum++);
            if (obj instanceof VehiculoParqueadoResponseDto) {
                VehiculoParqueadoResponseDto vehiculo = (VehiculoParqueadoResponseDto) obj;
                Cell cell1 = row.createCell(0);
                cell1.setCellStyle(dataStyle);
                cell1.setCellValue(vehiculo.getId());


                Cell cell2 = row.createCell(1);
                cell2.setCellStyle(dataStyle);
                cell2.setCellValue(vehiculo.getPlaca());


                Cell cell3 = row.createCell(2);
                cell3.setCellStyle(dataStyle);
                cell3.setCellValue( formatter.format(vehiculo.getFechaIngreso()));


                // Agregar más celdas según las propiedades del objeto
            }else if(obj instanceof IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto){
                IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto indicador = (IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto) obj;
                Cell cell1 = row.createCell(0);
                cell1.setCellStyle(dataStyle);
                cell1.setCellValue(indicador.getVehiculo().getId());


                Cell cell2 = row.createCell(1);
                cell2.setCellStyle(dataStyle);
                cell2.setCellValue(indicador.getVehiculo().getPlaca());


                Cell cell3 = row.createCell(2);
                cell3.setCellStyle(dataStyle);
                cell3.setCellValue(indicador.getCantidadVecesRegistrado());

            }else if(obj instanceof IndicadorVehiculosMasVecesRegistradoResponseDto){
                IndicadorVehiculosMasVecesRegistradoResponseDto indicador = (IndicadorVehiculosMasVecesRegistradoResponseDto) obj;
                Cell cell1 = row.createCell(0);
                cell1.setCellStyle(dataStyle);
                cell1.setCellValue(indicador.getVehiculo().getId());


                Cell cell2 = row.createCell(1);
                cell2.setCellStyle(dataStyle);
                cell2.setCellValue(indicador.getVehiculo().getPlaca());


                Cell cell3 = row.createCell(2);
                cell3.setCellStyle(dataStyle);
                cell3.setCellValue(indicador.getCantidadVecesRegistrado());

            }else if(obj instanceof GananciasResponseDto){
                GananciasResponseDto indicador = (GananciasResponseDto) obj;
                Cell cell1 = row.createCell(0);
                cell1.setCellStyle(dataStyle);
                cell1.setCellValue(indicador.getHoy());


                Cell cell2 = row.createCell(1);
                cell2.setCellStyle(dataStyle);
                cell2.setCellValue(indicador.getSemana());


                Cell cell3 = row.createCell(2);
                cell3.setCellStyle(dataStyle);
                cell3.setCellValue(indicador.getMes());

                Cell cell4 = row.createCell(3);
                cell4.setCellStyle(dataStyle);
                cell4.setCellValue(indicador.getAnio());

            }

        }
            }

    }

    // Método para crear la cabecera principal
    private static void createMainHeader(Sheet sheet, Workbook workbook, String mainHeaderText, CellStyle mainHeaderStyle) {
        Row mainHeaderRow = sheet.createRow(0);
        Cell mainHeaderCell = mainHeaderRow.createCell(0);
        mainHeaderCell.setCellValue(mainHeaderText);
        mainHeaderCell.setCellStyle(mainHeaderStyle);

        // Fusionar las tres primeras celdas
        sheet.addMergedRegion(new CellRangeAddress(
                0, // De la primera fila
                0, // Hasta la primera fila
                0, // De la primera columna
                2  // Hasta la tercera columna
        ));
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

    // Estilo para la cabecera principal
    private static CellStyle getMainHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Estilo para las cabeceras de columnas
    private static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // Método para establecer el estilo de las líneas de la tabla
    private static void setTableBorders(Sheet sheet, Workbook workbook) {
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        cell.setCellStyle(getTableBorderStyle(workbook));
                    }
                }
            }
        }
    }

    // Estilo para las líneas de la tabla
    private static CellStyle getTableBorderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // Obtener el estilo para la cabecera principal fusionada con las tres primeras columnas
    private static CellStyle getMainHeaderStyle2(Workbook workbook) {
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
       // style.setFont(getBoldFont(workbook));
        return style;
    }

    // Obtener el estilo para las cabeceras de las columnas
    private static CellStyle getColumnHeaderStyle2(Workbook workbook) {
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
      //  style.setFont(getBoldFont(workbook));
        return style;
    }

    // Obtener el estilo para los datos
    private static CellStyle getDataStyle2(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        //font.setBold(true);
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

    // Obtener una fuente en negrita
    private static Font getBoldFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        return font;
    }

}
