package com.nelumbo.parqueadero.repositories;

import com.nelumbo.parqueadero.entities.Parqueadero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParqueaderoRepository extends JpaRepository<Parqueadero, Long> {

}
