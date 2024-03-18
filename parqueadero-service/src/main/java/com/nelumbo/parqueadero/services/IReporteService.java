package com.nelumbo.parqueadero.services;

import java.io.IOException;

public interface IReporteService {

    void generarReporte(Long parqueaderoId, String placa)throws IOException;
}
