package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.entities.Rol;
import com.nelumbo.parqueadero.repositories.RolRepository;
import com.nelumbo.parqueadero.services.IRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RolServiceImpl implements IRolService {
    private final RolRepository rolRepository;

    @Override
    public Rol obtenerRolPorId(Long id) {
        return rolRepository.findById(id).orElseThrow();
    }
}
