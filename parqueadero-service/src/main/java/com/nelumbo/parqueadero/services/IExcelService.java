package com.nelumbo.parqueadero.services;

import java.io.IOException;
import java.util.List;

public interface IExcelService {
    void generarArchivoExcel(List<String> datos, String rutaArchivo) throws IOException;


}
