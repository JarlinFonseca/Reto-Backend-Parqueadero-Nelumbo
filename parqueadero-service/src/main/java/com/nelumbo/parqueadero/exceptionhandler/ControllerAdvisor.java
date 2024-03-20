package com.nelumbo.parqueadero.exceptionhandler;

import com.nelumbo.parqueadero.exception.NoDataFoundException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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
    private static final String MESSAGE_ERROR = "mensaje";
    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidateExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error ->{
            String fielName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();

            errors.put(fielName, message);
        });
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        Map<String, String> errors = new HashMap<>();
        String messageException = "Se esta violando una constraint de integridad, debido a que estas ingresando repetido " +
                "el documentoDeIdentidad o el correo ya que son unicos (UK).";
        errors.put(MESSAGE_ERROR, messageException);


        return  ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoDataFoundException(
            NoDataFoundException ignoredNoDataFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(MESSAGE, ExceptionResponse.NO_DATA_FOUND.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String,String>> handleFeignException(FeignException feignException){
        Map<String, String> errors = new HashMap<>();
        String messageException = "Error al registrar ingreso de vehiculo debido a que el servicio de correo no está disponible.";
        errors.put(MESSAGE_ERROR, messageException);
        return  ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        logger.info("Llega Excepcion: {}", exception.getClass());
        String messageException = "";
        switch (exception.getClass().toString()) {
            case "class com.nelumbo.parqueadero.exception.VehiculoNoPerteneceParqueaderoException":
                messageException = "El vehiculo no pertenece al parqueadero dado.";
                break;
            case "class com.nelumbo.parqueadero.exception.ParqueaderoNoExisteException":
                messageException = "El parqueadero no existe";
                break;
            case "class com.nelumbo.parqueadero.exception.UsuarioDebeSerRolSocioException":
                messageException = "El usuario autenticado debe ser rol SOCIO";
                break;
            case "class com.nelumbo.parqueadero.exception.UsuarioNoExisteException":
                messageException = "El usuario con rol SOCIO no existe";
                break;
            case "class com.nelumbo.parqueadero.exception.UsuarioSocioNoAutenticadoException":
                messageException = "El usuario SOCIO no se ha autenticado";
                break;
            case "class com.pragma.powerup.domain.exception.NoDataFoundException":
                messageException = "No data found for the requested petition";
                break;
            case "class com.nelumbo.parqueadero.exception.NoEsSocioDelParqueaderoException":
                messageException = "El usuario autenticado no es SOCIO del parqueadero";
                break;
            case "class com.nelumbo.parqueadero.exception.CantidadVehiculosLimiteException":
                messageException = "El parqueadero ya esta lleno, la cantidad de vehiculos limite ha sido superada";
                break;
            case "class com.nelumbo.parqueadero.exception.VehiculoExisteException":
                messageException = "No se puede Registrar Ingreso, ya existe la placa en este u otro parqueadero";
                break;
            case "class com.nelumbo.parqueadero.exception.VehiculoNoExisteException":
                messageException = "No se puede Registrar Salida, no existe la placa en el parqueadero";
                break;
            case "class com.nelumbo.parqueadero.exception.ParqueaderoVacioException":
                messageException = "El parqueadero esta vació, no tiene vehiculos";
                break;
            case "class com.nelumbo.parqueadero.exception.SocioNoTieneParqueaderosException":
                messageException = "El socio no tiene parqueaderos asociados.";
                break;
            case "class com.nelumbo.parqueadero.exception.NoExistenVehiculosRegistrados":
                messageException = "No existen vehiculos registrados";
                break;
            case "class com.nelumbo.parqueadero.exception.NoExistenVehiculosRegistradosPorPrimeraVez":
                messageException = "No hay vehiculos parqueados que esten por primera vez en este parqueadero";
                break;
            case "class com.nelumbo.parqueadero.exception.NoHayCoincidenciasPlacaException":
                messageException = "No hay coincidencias de placas de vehiculos de acuerdo a lo ingresado.";
                break;
            case "class com.nelumbo.parqueadero.exception.ExcelErrorException":
                messageException = "Error al generar el archivo de excel.";
                break;
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap(exception.getClass().toString(), exception.getMessage()));

        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap(MESSAGE_ERROR, messageException));
    }
}
