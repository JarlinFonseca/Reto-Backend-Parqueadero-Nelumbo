package com.nelumbo.parqueadero.services;

public interface IToken {
    String getBearerToken();

    String getCorreo(String token);

    Long getUsuarioAutenticadoId(String token);

    String getUsuarioAutenticadoRol(String token);
}
