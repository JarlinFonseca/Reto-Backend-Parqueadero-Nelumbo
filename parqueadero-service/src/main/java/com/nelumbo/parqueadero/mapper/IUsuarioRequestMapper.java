package com.nelumbo.parqueadero.mapper;

import com.nelumbo.parqueadero.dto.request.UsuarioRequestDto;
import com.nelumbo.parqueadero.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUsuarioRequestMapper {
    Usuario toUsuario(UsuarioRequestDto usuarioRequestDto);
}
