package com.nelumbo.correo.exception;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String mensaje) {
        super(mensaje);
    }
}
