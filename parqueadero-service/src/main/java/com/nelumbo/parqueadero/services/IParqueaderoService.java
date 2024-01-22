package com.nelumbo.parqueadero.services;

import com.nelumbo.parqueadero.dto.request.ParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.ParqueaderoResponseDto;
import com.nelumbo.parqueadero.dto.response.ParqueaderoSocioResponseDto;

import java.util.List;

public interface IParqueaderoService {

    ParqueaderoResponseDto guardarParqueadero(ParqueaderoRequestDto parqueaderoRequestDto);
    List<ParqueaderoResponseDto> listarParqueaderos();
    ParqueaderoResponseDto obtenerParqueaderoPorId(Long id);
    ParqueaderoResponseDto actualizarParqueadero(Long id, ParqueaderoRequestDto parqueaderoRequestDto);
    Boolean verificarExistenciaParqueadero(Long id);
    void eliminarParqueadero(Long id);
    List<ParqueaderoSocioResponseDto> listarParqueaderosSocio();



}
