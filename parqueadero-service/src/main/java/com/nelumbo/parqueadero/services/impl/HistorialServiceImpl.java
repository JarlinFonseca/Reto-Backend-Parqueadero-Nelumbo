package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.entities.Historial;
import com.nelumbo.parqueadero.repositories.HistorialRepository;
import com.nelumbo.parqueadero.services.IHistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HistorialServiceImpl implements IHistorialService {

    private final HistorialRepository historialRepository;

    @Override
    public void guardarHistorial(Historial historial) {
        historialRepository.save(historial);
    }
}
