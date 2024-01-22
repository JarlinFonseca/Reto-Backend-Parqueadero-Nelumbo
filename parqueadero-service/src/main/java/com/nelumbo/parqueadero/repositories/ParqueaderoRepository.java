package com.nelumbo.parqueadero.repositories;

import com.nelumbo.parqueadero.entities.Parqueadero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParqueaderoRepository extends JpaRepository<Parqueadero, Long> {

    Optional<List<Parqueadero>> findAllByUsuario_id(Long usuarioId);

}
