package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.exception.NoDataFoundException;
import com.nelumbo.parqueadero.security.TokenUtils;
import com.nelumbo.parqueadero.services.IToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenAdapter implements IToken {
    private final TokenUtils tokenUtils;
    @Override
    public String getBearerToken() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
    }

    @Override
    public String getCorreo(String token) {
        if(token==(null)) throw  new NoDataFoundException();
        return tokenUtils.getCorreo(token.replace("Bearer ",""));
    }

    @Override
    public Long getUsuarioAutenticadoId(String token) {
        if(token==(null)) throw  new NoDataFoundException();
        return tokenUtils.getUsuarioAutenticadoId(token.replace("Bearer ",""));
    }

    @Override
    public String getUsuarioAutenticadoRol(String token) {
        if(token==(null)) throw  new NoDataFoundException();
        return tokenUtils.getUsuarioAutenticadoRol(token.replace("Bearer ",""));
    }
}
