package com.nelumbo.parqueadero.exceptionhandler;

import com.nelumbo.parqueadero.exception.NoDataFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {
    private static final String MESSAGE = "message";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidateExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) ->{
            String fielName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();

            errors.put(fielName, message);
        });
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoDataFoundException(
            NoDataFoundException ignoredNoDataFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.NO_DATA_FOUND.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        System.out.println("llega Excepcion:"+exception.getClass().toString());
        String messageError = "";
        String messageException = "";
        switch (exception.getClass().toString()) {
            case "class com.nelumbo.parqueadero.exception.NoExisteParqueaderosException":
                messageError = "mensaje";
                messageException = "No existen parqueaderos guardados";
                break;
            case "class com.nelumbo.parqueadero.exception.ParqueaderoNoExisteException":
                messageError = "mensaje";
                messageException = "El parqueadero no existe";
                break;
            case "class com.nelumbo.parqueadero.exception.UsuarioDebeSerRolSocioException":
                messageError = "mensaje";
                messageException = "El usuario autenticado debe ser rol SOCIO";
                break;
            case "class com.nelumbo.parqueadero.exception.UsuarioNoExisteException":
                messageError = "mensaje";
                messageException = "El usuario con rol SOCIO no existe";
                break;
            case "class com.nelumbo.parqueadero.exception.UsuarioSocioNoAutenticadoException":
                messageError = "mensaje";
                messageException = "El usuario SOCIO no se ha autenticado";
                break;
            case "class com.pragma.powerup.domain.exception.NoDataFoundException":
                messageError = "Message Error";
                messageException = "No data found for the requested petition";
                break;
            case "class com.nelumbo.parqueadero.exception.NoEsSocioDelParqueaderoException":
                messageError = "mensaje";
                messageException = "El usuario autenticado no es SOCIO del parqueadero";
                break;
            case "class com.nelumbo.parqueadero.exception.CantidadVehiculosLimiteException":
                messageError = "mensaje";
                messageException = "El parqueadero ya esta lleno, la cantidad de vehiculos limite ha sido superada";
                break;
            case "class com.nelumbo.parqueadero.exception.VehiculoExisteException":
                messageError = "mensaje";
                messageException = "No se puede Registrar Ingreso, ya existe la placa en este u otro parqueadero";
                break;
            case "class com.nelumbo.parqueadero.exception.VehiculoNoExisteException":
                messageError = "mensaje";
                messageException = "No se puede Registrar Salida, no existe la placa en el parqueadero";
                break;
            case "class com.nelumbo.parqueadero.exception.ParqueaderoVacioException":
                messageError = "mensaje";
                messageException = "El parqueadero esta vació, no tiene vehiculos";
                break;
            case "class com.nelumbo.parqueadero.exception.SocioNoTieneParqueaderosException":
                messageError = "mensaje";
                messageException = "El socio no tiene parqueaderos asociados.";
                break;
            case "class com.nelumbo.parqueadero.exception.NoExistenVehiculosRegistrados":
                messageError = "mensaje";
                messageException = "No existen vehiculos registrados";
                break;
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap(exception.getClass().toString(), exception.getMessage()));

        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap(messageError, messageException));
    }
}
