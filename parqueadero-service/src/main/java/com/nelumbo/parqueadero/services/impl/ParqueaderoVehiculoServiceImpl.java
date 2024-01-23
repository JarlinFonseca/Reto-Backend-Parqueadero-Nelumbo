package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.request.IngresoVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.request.SalidaVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.IndicadorVehiculosMasVecesRegistradoResponseDto;
import com.nelumbo.parqueadero.dto.response.IngresoVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.ParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.SalidaVehiculoParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.VehiculoParqueadoResponseDto;
import com.nelumbo.parqueadero.entities.Historial;
import com.nelumbo.parqueadero.entities.Parqueadero;
import com.nelumbo.parqueadero.entities.ParqueaderoVehiculo;
import com.nelumbo.parqueadero.entities.Vehiculo;
import com.nelumbo.parqueadero.exception.CantidadVehiculosLimiteException;
import com.nelumbo.parqueadero.exception.NoEsSocioDelParqueaderoException;
import com.nelumbo.parqueadero.exception.NoExistenVehiculosRegistrados;
import com.nelumbo.parqueadero.exception.ParqueaderoNoExisteException;
import com.nelumbo.parqueadero.exception.ParqueaderoVacioException;
import com.nelumbo.parqueadero.exception.UsuarioSocioNoAutenticadoException;
import com.nelumbo.parqueadero.exception.VehiculoExisteException;
import com.nelumbo.parqueadero.exception.VehiculoNoExisteException;
import com.nelumbo.parqueadero.repositories.ParqueaderoRepository;
import com.nelumbo.parqueadero.repositories.ParqueaderoVehiculoRepository;
import com.nelumbo.parqueadero.services.IHistorialService;
import com.nelumbo.parqueadero.services.IParqueaderoService;
import com.nelumbo.parqueadero.services.IParqueaderoVehiculoService;
import com.nelumbo.parqueadero.services.IToken;
import com.nelumbo.parqueadero.services.IVehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParqueaderoVehiculoServiceImpl implements IParqueaderoVehiculoService {
    private final IParqueaderoService parqueaderoService;
    private final IVehiculoService vehiculoService;
    private final IHistorialService historialService;
    private final ParqueaderoVehiculoRepository parqueaderoVehiculoRepository;
    private final ParqueaderoRepository parqueaderoRepository;
    private final IToken token;

    @Override
    public IngresoVehiculoParqueaderoResponseDto registrarIngreso(IngresoVehiculoParqueaderoRequestDto ingresoVehiculoParqueaderoRequestDto) {
        verificarSocioAutenticado(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId());
        Long cantidadMaxVehiculos = parqueaderoService.obtenerParqueaderoPorId(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId()).getCantidadVehiculosMaximo();
        Long cantidadActualVehiculo = parqueaderoVehiculoRepository.getCountVehiculosParqueadero(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId());
        String placa = ingresoVehiculoParqueaderoRequestDto.getPlaca().toUpperCase();
        if((cantidadMaxVehiculos- cantidadActualVehiculo) <= 0) throw new CantidadVehiculosLimiteException();
        if(Boolean.TRUE.equals(vehiculoService.verificarExistenciaVehiculo(placa)))  throw new VehiculoExisteException();
        vehiculoService.guardarVehiculo(placa);
        Parqueadero parqueadero =  parqueaderoRepository.findById(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId()).orElseThrow();
        Vehiculo vehiculo= vehiculoService.obtenerVehiculoPorPlaca(placa);
        ParqueaderoVehiculo parqueaderoVehiculo = new ParqueaderoVehiculo();
        parqueaderoVehiculo.setFechaIngreso(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        parqueaderoVehiculo.setFlagIngresoActivo(true);
        parqueaderoVehiculo.setVehiculo(vehiculo);
        parqueaderoVehiculo.setParqueadero(parqueadero);
        parqueaderoVehiculoRepository.save(parqueaderoVehiculo);
        IngresoVehiculoParqueaderoResponseDto ingresoVehiculoParqueaderoResponseDto = new IngresoVehiculoParqueaderoResponseDto();
        ingresoVehiculoParqueaderoResponseDto.setId(vehiculo.getId());

        return ingresoVehiculoParqueaderoResponseDto;
    }

    @Override
    public SalidaVehiculoParqueaderoResponseDto registrarSalida(SalidaVehiculoParqueaderoRequestDto salidaVehiculoParqueaderoRequestDto) {
        Long idSocioAuth= verificarSocioAutenticado(salidaVehiculoParqueaderoRequestDto.getParqueaderoId());
        String placa = salidaVehiculoParqueaderoRequestDto.getPlaca().toUpperCase();
        if(!vehiculoService.verificarExistenciaVehiculo(placa)) throw new VehiculoNoExisteException();
        Vehiculo vehiculo = vehiculoService.obtenerVehiculoPorPlaca(placa);
        ParqueaderoVehiculo parqueaderoVehiculo = obtenerParqueaderoVehiculoPorIdYFlagVehiculoActivo(vehiculo.getId(), true);
        if(!idSocioAuth.equals(parqueaderoVehiculo.getParqueadero().getUsuario().getId())) throw new NoEsSocioDelParqueaderoException();
        Date fechaSalida = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date fechaIngreso = parqueaderoVehiculo.getFechaIngreso();
        Double costoHoraVehiculo = parqueaderoVehiculo.getParqueadero().getCostoHoraVehiculo();
        Double horas = obtenerHoras(fechaIngreso, fechaSalida);

        if(horas<1) horas=1.0;
        Double pagoTotal =  (horas*costoHoraVehiculo);
        Long pagoTotalSinDecimales = pagoTotal.longValue();

        Historial historial = new Historial();
        historial.setFechaIngreso(fechaIngreso);
        historial.setFechaSalida(fechaSalida);
        historial.setPagoTotal(pagoTotalSinDecimales.doubleValue());

        // Cambiar flag de parqueado activo a false
        parqueaderoVehiculo.setFlagIngresoActivo(false);
        parqueaderoVehiculoRepository.save(parqueaderoVehiculo);

        historial.setParqueaderoVehiculo(parqueaderoVehiculo);
        historialService.guardarHistorial(historial);

        return new SalidaVehiculoParqueaderoResponseDto("Salida registrada");
    }

    private Double obtenerHoras(Date fechaIngreso, Date fechaSalida){
        Long tiempoInicial=fechaIngreso.getTime();
        Long tiempoFinal=fechaSalida.getTime();
        Double resta= Double.valueOf(tiempoFinal - tiempoInicial);
        resta=resta / 3600000;
        return resta;
    }

    @Override
    public ParqueaderoVehiculo obtenerParqueaderoVehiculoPorIdYFlagVehiculoActivo(Long id, Boolean flag) {
        return parqueaderoVehiculoRepository.findByVehiculo_idAndFlagIngresoActivo(id, flag).orElse(null);
    }

    @Override
    public List<VehiculoParqueadoResponseDto> obtenerVehiculosParqueadosPorIdParqueadero(Long parqueaderoId) {
        if(Boolean.FALSE.equals(parqueaderoService.verificarExistenciaParqueadero(parqueaderoId))) throw new ParqueaderoNoExisteException();
        List<ParqueaderoVehiculo> parqueaderoVehiculos = parqueaderoVehiculoRepository.findAllByParqueadero_idAndFlagIngresoActivo(parqueaderoId,true).orElseThrow();
        if( parqueaderoVehiculos.isEmpty()) throw new ParqueaderoVacioException();

        return parqueaderoVehiculos.stream().
                map(parqueaderoVehiculo -> {
            VehiculoParqueadoResponseDto vehiculoParqueadoResponseDto = new VehiculoParqueadoResponseDto();
            vehiculoParqueadoResponseDto.setId(parqueaderoVehiculo.getId());
            vehiculoParqueadoResponseDto.setPlaca(parqueaderoVehiculo.getVehiculo().getPlaca());
            vehiculoParqueadoResponseDto.setFechaIngreso(parqueaderoVehiculo.getFechaIngreso());
            return vehiculoParqueadoResponseDto;
        }).collect(Collectors.toList()) ;
    }

    @Override
    public List<VehiculoParqueadoResponseDto> obtenerVehiculosParqueaderosAsociadosPorId(Long parqueaderoId) {
        verificarSocioAutenticado(parqueaderoId);
        return obtenerVehiculosParqueadosPorIdParqueadero(parqueaderoId);
    }

    @Override
    public List<IndicadorVehiculosMasVecesRegistradoResponseDto> obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez() {
        List<Object[]> vehiculos = parqueaderoVehiculoRepository.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez().orElseThrow();
        if(vehiculos.isEmpty()) throw new NoExistenVehiculosRegistrados();
        return vehiculos.stream().map(vehiculosParqueadero ->{
            IndicadorVehiculosMasVecesRegistradoResponseDto vehiculosMasVecesRegistradoResponseDto = new IndicadorVehiculosMasVecesRegistradoResponseDto();
            Vehiculo vehiculo = vehiculoService.obtenerVehiculoPorId(Long.parseLong(vehiculosParqueadero[0].toString()));
            ParqueaderoResponseDto parqueadero= parqueaderoService.obtenerParqueaderoPorId(Long.parseLong(vehiculosParqueadero[1].toString()));
            vehiculosMasVecesRegistradoResponseDto.setNombreParqueadero(parqueadero.getNombre());
            vehiculosMasVecesRegistradoResponseDto.setVehiculo(vehiculo);
            vehiculosMasVecesRegistradoResponseDto.setCantidadVecesRegistrado(Long.parseLong(vehiculosParqueadero[2].toString()));
            return vehiculosMasVecesRegistradoResponseDto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<IndicadorVehiculosMasVecesRegistradoResponseDto> obtenerVehiculosMasVecesRegistradosParqueaderoPorId(Long parqueaderoId) {
        List<Object[]> vehiculos = parqueaderoVehiculoRepository.obtenerVehiculosMasVecesRegistradosEnUnParqueaderoLimiteDiez(parqueaderoId).orElseThrow();
        if(vehiculos.isEmpty()) throw new NoExistenVehiculosRegistrados();

        return vehiculos.stream().map(vehiculosParqueadero ->{
            IndicadorVehiculosMasVecesRegistradoResponseDto vehiculosMasVecesRegistradoResponseDto = new IndicadorVehiculosMasVecesRegistradoResponseDto();
            Vehiculo vehiculo = vehiculoService.obtenerVehiculoPorId(Long.parseLong(vehiculosParqueadero[0].toString()));
            ParqueaderoResponseDto parqueadero= parqueaderoService.obtenerParqueaderoPorId(Long.parseLong(vehiculosParqueadero[1].toString()));
            vehiculosMasVecesRegistradoResponseDto.setNombreParqueadero(parqueadero.getNombre());
            vehiculosMasVecesRegistradoResponseDto.setVehiculo(vehiculo);
            vehiculosMasVecesRegistradoResponseDto.setCantidadVecesRegistrado(Long.parseLong(vehiculosParqueadero[2].toString()));
            return vehiculosMasVecesRegistradoResponseDto;
        }).collect(Collectors.toList());
    }

    private Long verificarSocioAutenticado(Long parqueaderoId){
        String tokenBearer = token.getBearerToken();
        if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        Long idSocioAuth = token.getUsuarioAutenticadoId(tokenBearer);
        Long idSocioParqueadero=  parqueaderoService.obtenerParqueaderoPorId(parqueaderoId).getUsuario().getId();
        if(!idSocioAuth.equals(idSocioParqueadero)) throw new NoEsSocioDelParqueaderoException();
        return idSocioAuth;
    }
}
