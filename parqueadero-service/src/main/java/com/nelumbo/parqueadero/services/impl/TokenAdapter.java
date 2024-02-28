package com.nelumbo.parqueadero.services.impl;

import com.nelumbo.parqueadero.exception.NoDataFoundException;
import com.nelumbo.parqueadero.security.TokenUtils;
import com.nelumbo.parqueadero.services.IToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenAdapter implements IToken {
    private final TokenUtils tokenUtils;
    private static final String JWT_BEARER_PREFIX ="Bearer ";
    @Override
    public String getBearerToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("Authorization");
        }
        return null;
    }

    @Override
    public String getCorreo(String token) {
        if(token==(null)) throw  new NoDataFoundException();
        return tokenUtils.getCorreo(token.replace(JWT_BEARER_PREFIX,""));
    }

    @Override
    public Long getUsuarioAutenticadoId(String token) {
        if(token==(null)) throw  new NoDataFoundException();
        return tokenUtils.getUsuarioAutenticadoId(token.replace(JWT_BEARER_PREFIX,""));
    }

    @Override
    public String getUsuarioAutenticadoRol(String token) {
        if(token==(null)) throw  new NoDataFoundException();
        return tokenUtils.getUsuarioAutenticadoRol(token.replace(JWT_BEARER_PREFIX,""));
    }
}
