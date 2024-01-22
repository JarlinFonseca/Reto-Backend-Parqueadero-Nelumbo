package com.nelumbo.parqueadero.mapper;

import com.nelumbo.parqueadero.dto.request.ParqueaderoRequestDto;
import com.nelumbo.parqueadero.entities.Parqueadero;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IParqueaderoRequestMapper {
    Parqueadero toParqueadero(ParqueaderoRequestDto parqueaderoRequestDto);
}
