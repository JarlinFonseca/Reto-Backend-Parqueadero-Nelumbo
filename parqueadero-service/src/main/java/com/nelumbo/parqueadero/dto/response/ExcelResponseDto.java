package com.nelumbo.parqueadero.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelResponseDto {
    private byte[] archivo;
    private String mensaje;
    private String indicadores;
}
