package com.nelumbo.parqueadero.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReporteResponseDto {
    private String mensaje;
    private ExcelResponseDto excelResponse;
}
