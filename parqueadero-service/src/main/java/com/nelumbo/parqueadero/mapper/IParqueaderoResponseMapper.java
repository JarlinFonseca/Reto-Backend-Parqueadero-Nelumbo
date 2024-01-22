package com.nelumbo.parqueadero.mapper;

import com.nelumbo.parqueadero.dto.response.ParqueaderoResponseDto;
import com.nelumbo.parqueadero.entities.Parqueadero;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IParqueaderoResponseMapper {
    ParqueaderoResponseDto toResponse(Parqueadero parqueadero);
    List<ParqueaderoResponseDto> toResponseList(List<Parqueadero> parqueaderos);
}
