package com.nelumbo.correo.repositories;

import com.nelumbo.correo.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMensajeRepository extends JpaRepository<Mensaje, Long> {
}
