package com.nelumbo.correo.repositories;

import com.nelumbo.correo.entities.Mensaje;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IMensajeRepository extends MongoRepository<Mensaje, String> {

    List<Mensaje> findByFechaEnviadoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
