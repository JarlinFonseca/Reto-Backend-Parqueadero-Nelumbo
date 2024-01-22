package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.dto.request.ParqueaderoRequestDto;
import com.nelumbo.parqueadero.dto.response.ParqueaderoResponseDto;
import com.nelumbo.parqueadero.entities.Parqueadero;
import com.nelumbo.parqueadero.entities.Usuario;
import com.nelumbo.parqueadero.exception.NoExisteParqueaderosException;
import com.nelumbo.parqueadero.exception.ParqueaderoNoExisteException;
import com.nelumbo.parqueadero.exception.UsuarioDebeSerRolSocioException;
import com.nelumbo.parqueadero.exception.UsuarioNoExisteException;
import com.nelumbo.parqueadero.mapper.IParqueaderoRequestMapper;
import com.nelumbo.parqueadero.mapper.IParqueaderoResponseMapper;
import com.nelumbo.parqueadero.repositories.ParqueaderoRepository;
import com.nelumbo.parqueadero.repositories.UsuarioRepository;
import com.nelumbo.parqueadero.services.IParqueaderoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParqueaderoServiceImpl implements IParqueaderoService {

    private final ParqueaderoRepository parqueaderoRepository;
    private final UsuarioRepository usuarioRepository;
    private final IParqueaderoRequestMapper parqueaderoRequestMapper;
    private final IParqueaderoResponseMapper parqueaderoResponseMapper;
    private final Long ROL_ID_ADMIN= 1L;
    @Override
    public ParqueaderoResponseDto guardarParqueadero(ParqueaderoRequestDto parqueaderoRequestDto) {
        Parqueadero parqueadero = parqueaderoRequestMapper.toParqueadero(parqueaderoRequestDto);
        parqueadero.setFechaRegistro(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        parqueadero.setUsuario(validarUsuario(parqueaderoRequestDto.getUsuarioId()));
        parqueaderoRepository.save(parqueadero);
        return parqueaderoResponseMapper.toResponse(parqueadero);
    }

    private Usuario validarUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if(usuario==null)  throw new UsuarioNoExisteException();
        if(usuario.getRol().getId().equals(ROL_ID_ADMIN)) throw new UsuarioDebeSerRolSocioException();
        return usuario;
    }

    private Parqueadero getParqueaderoById(Long id){
        Parqueadero parqueadero = parqueaderoRepository.findById(id).orElse(null);
        if(parqueadero==null) throw new ParqueaderoNoExisteException();
        return  parqueadero;
    }

    @Override
    public List<ParqueaderoResponseDto> listarParqueaderos() {
        List<Parqueadero> parqueaderos = parqueaderoRepository.findAll();
        if(parqueaderos.isEmpty()) {
            throw new NoExisteParqueaderosException();
        }
        return parqueaderoResponseMapper.toResponseList(parqueaderos);
    }

    @Override
    public ParqueaderoResponseDto obtenerParqueaderoPorId(Long id) {
        return parqueaderoResponseMapper.toResponse(getParqueaderoById(id));
    }

    @Override
    public ParqueaderoResponseDto actualizarParqueadero(Long id, ParqueaderoRequestDto parqueaderoRequestDto) {
        Parqueadero parqueadero = getParqueaderoById(id);
        parqueadero.setNombre(parqueaderoRequestDto.getNombre());
        parqueadero.setCantidadVehiculosMaximo(parqueaderoRequestDto.getCantidadVehiculosMaximo());
        parqueadero.setCostoHoraVehiculo(parqueaderoRequestDto.getCostoHoraVehiculo());
        parqueadero.setUsuario(validarUsuario(parqueaderoRequestDto.getUsuarioId()));
        parqueaderoRepository.save(parqueadero);
        return parqueaderoResponseMapper.toResponse(parqueadero);
    }

    @Override
    public Boolean verificarExistenciaParqueadero(Long id) {
         Parqueadero parqueadero = parqueaderoRepository.findById(id).orElse(null);
         return parqueadero!=null;
    }

    @Override
    public void eliminarParqueadero(Long id) {
        parqueaderoRepository.delete(getParqueaderoById(id));
    }
}
