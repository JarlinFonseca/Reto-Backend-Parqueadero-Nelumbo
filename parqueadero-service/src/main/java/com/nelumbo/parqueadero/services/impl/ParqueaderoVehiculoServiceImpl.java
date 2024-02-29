package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.request.IngresoVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.request.SalidaVehiculoParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.*;
import com.nelumbo.parqueadero.entities.Historial;
import com.nelumbo.parqueadero.entities.Parqueadero;
import com.nelumbo.parqueadero.entities.ParqueaderoVehiculo;
import com.nelumbo.parqueadero.entities.Vehiculo;
import com.nelumbo.parqueadero.exception.*;
import com.nelumbo.parqueadero.feignclients.CorreoFeignClients;
import com.nelumbo.parqueadero.feignclients.dto.request.MensajeRequestDto;
import com.nelumbo.parqueadero.repositories.ParqueaderoRepository;
import com.nelumbo.parqueadero.repositories.ParqueaderoVehiculoRepository;
import com.nelumbo.parqueadero.services.IHistorialService;
import com.nelumbo.parqueadero.services.IParqueaderoService;
import com.nelumbo.parqueadero.services.IParqueaderoVehiculoService;
import com.nelumbo.parqueadero.services.IToken;
import com.nelumbo.parqueadero.services.IVehiculoService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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
    private final CorreoFeignClients correoFeignClients;
    private static final Logger logger = LoggerFactory.getLogger(ParqueaderoVehiculoServiceImpl.class);

    @Override
    public IngresoVehiculoParqueaderoResponseDto registrarIngreso(IngresoVehiculoParqueaderoRequestDto ingresoVehiculoParqueaderoRequestDto) {
        verificarSocioAutenticado(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId());
        Long cantidadMaxVehiculos = parqueaderoService.obtenerParqueaderoPorId(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId()).getCantidadVehiculosMaximo();
        Long cantidadActualVehiculo = parqueaderoVehiculoRepository.getCountVehiculosParqueadero(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId());
        String placa = ingresoVehiculoParqueaderoRequestDto.getPlaca().toUpperCase();
        if((cantidadMaxVehiculos- cantidadActualVehiculo) <= 0) throw new CantidadVehiculosLimiteException();
        Vehiculo vehiculo= vehiculoService.obtenerVehiculoPorPlaca(placa);
        if(Boolean.TRUE.equals(vehiculoService.verificarExistenciaVehiculo(vehiculo)))  throw new VehiculoExisteException();
        vehiculo = vehiculoService.guardarVehiculo(placa, vehiculo);
        Parqueadero parqueadero =  parqueaderoRepository.findById(ingresoVehiculoParqueaderoRequestDto.getParqueaderoId()).orElseThrow();

        ParqueaderoVehiculo parqueaderoVehiculo = new ParqueaderoVehiculo();
        parqueaderoVehiculo.setFechaIngreso(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        parqueaderoVehiculo.setFlagIngresoActivo(true);
        parqueaderoVehiculo.setVehiculo(vehiculo);
        parqueaderoVehiculo.setParqueadero(parqueadero);
        parqueaderoVehiculoRepository.save(parqueaderoVehiculo);
        IngresoVehiculoParqueaderoResponseDto ingresoVehiculoParqueaderoResponseDto = new IngresoVehiculoParqueaderoResponseDto();
        ingresoVehiculoParqueaderoResponseDto.setId(vehiculo.getId());


        try{
            correoFeignClients.enviarCorreo(guardarDatosMensaje(vehiculo,parqueadero));
        }catch (FeignException e){
            logger.error("El servicio de correo no esta disponible debido a lo siguiente: ".concat(e.getMessage()));
        }

        return ingresoVehiculoParqueaderoResponseDto;
    }

    private MensajeRequestDto guardarDatosMensaje(Vehiculo vehiculo, Parqueadero parqueadero){
        MensajeRequestDto mensajeRequestDto= new MensajeRequestDto();
        mensajeRequestDto.setDescripcion("Vehiculo parqueado correctamente");
        mensajeRequestDto.setEmail(parqueadero.getUsuario().getCorreo());
        mensajeRequestDto.setParqueaderoNombre(parqueadero.getNombre());
        mensajeRequestDto.setPlaca(vehiculo.getPlaca());
        return mensajeRequestDto;
    }

    @Override
    public SalidaVehiculoParqueaderoResponseDto registrarSalida(SalidaVehiculoParqueaderoRequestDto salidaVehiculoParqueaderoRequestDto) {
        Long idSocioAuth= verificarSocioAutenticado(salidaVehiculoParqueaderoRequestDto.getParqueaderoId());
        String placa = salidaVehiculoParqueaderoRequestDto.getPlaca().toUpperCase();
        Vehiculo vehiculo = vehiculoService.obtenerVehiculoPorPlaca(placa);
        if(Boolean.FALSE.equals(vehiculoService.verificarExistenciaVehiculo(vehiculo))) throw new VehiculoNoExisteException();
        ParqueaderoVehiculo parqueaderoVehiculo = obtenerParqueaderoVehiculoPorIdYFlagVehiculoActivo(vehiculo.getId(), true);
        if(!Objects.equals(parqueaderoVehiculo.getParqueadero().getId(), salidaVehiculoParqueaderoRequestDto.getParqueaderoId())) throw new VehiculoNoPerteneceParqueaderoException();
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
        double resta= (tiempoFinal - tiempoInicial);
        resta=resta / 3600000;
        return resta;
    }

    @Override
    public ParqueaderoVehiculo obtenerParqueaderoVehiculoPorIdYFlagVehiculoActivo(Long id, Boolean flag) {
        return parqueaderoVehiculoRepository.findByVehiculo_idAndFlagIngresoActivo(id, flag).orElse(null);
    }

    @Override
    public List<VehiculoParqueadoResponseDto> obtenerVehiculosParqueadosPorIdParqueadero(Long parqueaderoId) {
        if(Boolean.TRUE.equals(esRolSocio())) verificarSocioAutenticado(parqueaderoId);

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
    public List<IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto> obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiez() {
        List<Object[]> vehiculos;
        if(Boolean.TRUE.equals(esRolSocio())) vehiculos = parqueaderoVehiculoRepository.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiezSocio(obtenerIdUsuarioAutenticado()).orElseThrow();
        else vehiculos = parqueaderoVehiculoRepository.obtenerVehiculosMasVecesRegistradosEnDiferentesParqueaderosLimiteDiezAdmin().orElseThrow();

        if(vehiculos.isEmpty()) throw new NoExistenVehiculosRegistrados();
        return vehiculos.stream().map(vehiculosParqueadero ->{
            IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto vehiculosMasVecesRegistradoResponseDto = new IndicadorVehiculosMasVecesRegistradoDiferentesParqueaderosDto();
            Vehiculo vehiculo = vehiculoService.obtenerVehiculoPorId(Long.parseLong(vehiculosParqueadero[0].toString()));
            vehiculosMasVecesRegistradoResponseDto.setVehiculo(vehiculo);
            vehiculosMasVecesRegistradoResponseDto.setCantidadVecesRegistrado(Long.parseLong(vehiculosParqueadero[1].toString()));
            return vehiculosMasVecesRegistradoResponseDto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<IndicadorVehiculosMasVecesRegistradoResponseDto> obtenerVehiculosMasVecesRegistradosParqueaderoPorId(Long parqueaderoId) {
        if(Boolean.TRUE.equals(esRolSocio())){
            verificarSocioAutenticado(parqueaderoId);
        }
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

    @Override
    public List<VehiculoParqueadoResponseDto> buscarVehiculoPorCoincidenciaPlaca(String placa) {
        List<Object[]> vehiculos;
        if(Boolean.TRUE.equals(esRolSocio())){
            Long idSocioAuth = obtenerIdUsuarioAutenticado();
            List<Parqueadero> parqueaderosSocio = parqueaderoRepository.findAllByUsuario_id(idSocioAuth).orElse(null);
            if(parqueaderosSocio==null || parqueaderosSocio.isEmpty()) throw new NoHayCoincidenciasPlacaException();
            List<Long> idsParqueaderos = parqueaderosSocio.stream().map(Parqueadero::getId).collect(Collectors.toList());
             vehiculos = parqueaderoVehiculoRepository.getVehiculosParqueadosPorCoincidenciaSocio(placa, idsParqueaderos).orElseThrow();
        }else {
            vehiculos = parqueaderoVehiculoRepository.getVehiculosParqueadosPorCoincidencia(placa).orElseThrow();
        }
        if (vehiculos.isEmpty()) throw new NoHayCoincidenciasPlacaException();

        return vehiculos.stream().map(vehiculo ->{
            VehiculoParqueadoResponseDto vehiculoParqueadoResponseDto = new VehiculoParqueadoResponseDto();
            ParqueaderoVehiculo parqueaderoVehiculo= parqueaderoVehiculoRepository.findById(Long.parseLong(vehiculo[0].toString())).orElseThrow();
            vehiculoParqueadoResponseDto.setId(parqueaderoVehiculo.getId());
            vehiculoParqueadoResponseDto.setPlaca(vehiculo[1].toString());
            vehiculoParqueadoResponseDto.setFechaIngreso(parqueaderoVehiculo.getFechaIngreso());
            return  vehiculoParqueadoResponseDto;

        }).collect(Collectors.toList());
    }

    private Long verificarSocioAutenticado(Long parqueaderoId){
        String tokenBearer = token.getBearerToken();
        if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        Long idSocioAuth = token.getUsuarioAutenticadoId(tokenBearer);
        Long idSocioParqueadero=  parqueaderoRepository.findById(parqueaderoId).orElseThrow(ParqueaderoNoExisteException::new).getUsuario().getId();
        if(!idSocioAuth.equals(idSocioParqueadero)) throw new NoEsSocioDelParqueaderoException();
        return idSocioAuth;
    }

    private Boolean esRolSocio() {
        String tokenBearer = token.getBearerToken();
        if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        String rolSocioAuth = token.getUsuarioAutenticadoRol(tokenBearer);
        return rolSocioAuth.equals("SOCIO");
    }

    private Long obtenerIdUsuarioAutenticado(){
        String tokenBearer = token.getBearerToken();
        if(tokenBearer== null) throw new UsuarioSocioNoAutenticadoException();
        return token.getUsuarioAutenticadoId(tokenBearer);
    }

}
