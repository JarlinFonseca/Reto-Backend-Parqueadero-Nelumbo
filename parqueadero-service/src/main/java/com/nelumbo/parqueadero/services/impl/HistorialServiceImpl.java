package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.entities.Historial;
import com.nelumbo.parqueadero.entities.Vehiculo;
import com.nelumbo.parqueadero.exception.NoExistenVehiculosRegistrados;
import com.nelumbo.parqueadero.exception.NoExistenVehiculosRegistradosPorPrimeraVez;
import com.nelumbo.parqueadero.repositories.HistorialRepository;
import com.nelumbo.parqueadero.repositories.ParqueaderoRepository;
import com.nelumbo.parqueadero.repositories.ParqueaderoVehiculoRepository;
import com.nelumbo.parqueadero.services.IHistorialService;
import com.nelumbo.parqueadero.services.IVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HistorialServiceImpl implements IHistorialService {

    private final HistorialRepository historialRepository;
    private final ParqueaderoVehiculoRepository parqueaderoVehiculoRepository;
    private final IVehiculoService vehiculoService;

    @Override
    public void guardarHistorial(Historial historial) {
        historialRepository.save(historial);
    }

    @Override
    public List<VehiculoParqueadoResponseDto> obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(Long parqueaderoId) {
        List<Object[]> vehiculosPrimeraVez = parqueaderoVehiculoRepository.obtenerVehiculosParqueadosPorPrimeraVezPorParqueaderoId(parqueaderoId).orElseThrow();
        if(vehiculosPrimeraVez.isEmpty()) throw new NoExistenVehiculosRegistradosPorPrimeraVez();

        return vehiculosPrimeraVez.stream().map(vehiculos ->{
            VehiculoParqueadoResponseDto vehiculoParqueadoResponseDto = new VehiculoParqueadoResponseDto();
            Vehiculo vehiculo = vehiculoService.obtenerVehiculoPorId(Long.parseLong(vehiculos[1].toString()));
            vehiculoParqueadoResponseDto.setId(vehiculo.getId());
            vehiculoParqueadoResponseDto.setPlaca(vehiculo.getPlaca());
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                Date fechaIngreso = formato.parse(vehiculos[2].toString());
                vehiculoParqueadoResponseDto.setFechaIngreso(fechaIngreso);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return vehiculoParqueadoResponseDto;
        }).collect(Collectors.toList());
    }
}
