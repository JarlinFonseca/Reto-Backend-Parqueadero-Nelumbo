package com.nelumbo.parqueadero.mapper;

import com.nelumbo.parqueadero.dto.response.UsuarioResponseDto;
import com.nelumbo.parqueadero.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUsuarioResponseMapper {

    @Mapping(source = "usuario.rol.nombre", target = "rol")
    UsuarioResponseDto toResponse(Usuario usuario);
}
