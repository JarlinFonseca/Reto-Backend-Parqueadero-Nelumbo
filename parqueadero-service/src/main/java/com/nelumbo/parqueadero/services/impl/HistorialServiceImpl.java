package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.response.GananciasResponseDto;
import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.entities.Historial;
import com.nelumbo.parqueadero.entities.Vehiculo;
import com.nelumbo.parqueadero.exception.NoEsSocioDelParqueaderoException;
import com.nelumbo.parqueadero.exception.NoExistenVehiculosRegistradosPorPrimeraVez;
import com.nelumbo.parqueadero.exception.UsuarioSocioNoAutenticadoException;
import com.nelumbo.parqueadero.repositories.HistorialRepository;
import com.nelumbo.parqueadero.repositories.ParqueaderoVehiculoRepository;
import com.nelumbo.parqueadero.services.IHistorialService;
import com.nelumbo.parqueadero.services.IParqueaderoService;
import com.nelumbo.parqueadero.services.IToken;
import com.nelumbo.parqueadero.services.IVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HistorialServiceImpl implements IHistorialService {

    private final HistorialRepository historialRepository;
    private final ParqueaderoVehiculoRepository parqueaderoVehiculoRepository;
    private final IVehiculoService vehiculoService;
    private final IParqueaderoService parqueaderoService;
    private final IToken token;

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

    @Override
    public GananciasResponseDto obtenerGanancias(Long parqueaderoId) {
        String tokenBearer = token.getBearerToken();
        if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        Long idSocioAuth = token.getUsuarioAutenticadoId(tokenBearer);
        Long idSocioParqueadero=  parqueaderoService.obtenerParqueaderoPorId(parqueaderoId).getUsuario().getId();
        if(!idSocioAuth.equals(idSocioParqueadero)) throw new NoEsSocioDelParqueaderoException();

        LocalDate fechaActual = LocalDate.now();
        WeekFields weekFields = WeekFields.ISO;
        Long semanaActual = (long) fechaActual.get(weekFields.weekOfWeekBasedYear());

        Long mesActual = (long) fechaActual.getMonthValue();
        Long anioActual = (long) fechaActual.getYear();
        String fechaHoy= fechaActual.toString();

        Long gananciasAnioActual =  historialRepository.obtenerGananciasAnio(parqueaderoId, anioActual);
        if(gananciasAnioActual==null) gananciasAnioActual= 0L;
        Long gananciasMesActual = historialRepository.obtenerGananciasMes(parqueaderoId, mesActual, anioActual);
        if(gananciasMesActual==null) gananciasMesActual= 0L;
        Long gananciasSemanaActual = historialRepository.obtenerGananciasSemana(parqueaderoId, semanaActual, mesActual, anioActual);
        if(gananciasSemanaActual==null) gananciasSemanaActual= 0L;
        Long gananciasHoy = historialRepository.obtenerGananciasHoy(parqueaderoId, fechaHoy);
        if(gananciasHoy==null) gananciasHoy= 0L;

        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formatoMoneda.setMaximumFractionDigits(0);


        GananciasResponseDto gananciasResponseDto= new GananciasResponseDto();
        gananciasResponseDto.setHoy("Las ganancias de la fecha de hoy "+fechaHoy+" son: "+formatoMoneda.format(gananciasHoy));
        gananciasResponseDto.setSemana("Las ganancias de esta semana son: "+formatoMoneda.format(gananciasSemanaActual));
        gananciasResponseDto.setMes("Las ganancias del mes de "+fechaActual.getMonth().toString()+" son: "+formatoMoneda.format(gananciasMesActual));
        gananciasResponseDto.setAnio("Las ganancias del año de "+anioActual+" son: "+formatoMoneda.format(gananciasAnioActual));

        return gananciasResponseDto;
    }
}
