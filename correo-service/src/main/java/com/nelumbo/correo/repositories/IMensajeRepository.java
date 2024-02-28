package com.nelumbo.correo.repositories;

import com.nelumbo.correo.entities.Mensaje;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IMensajeRepository extends MongoRepository<Mensaje, String> {
}
