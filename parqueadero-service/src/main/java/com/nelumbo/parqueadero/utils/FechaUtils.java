package com.nelumbo.parqueadero.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class FechaUtils {

    public Date convertirFechaUtcToColombia(Date fechaUTC){
        ZoneId zonaColombia = ZoneId.of("America/Bogota");
        return  Date.from(fechaUTC.toInstant().atZone(zonaColombia).toInstant());
    }


    public String convertirFechaToString(Date fecha){
        // Convertir Date a LocalDateTime
        Instant instant = fecha.toInstant();
        LocalDateTime fechaLocal = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return fechaLocal.format(formatter);
    }
}
