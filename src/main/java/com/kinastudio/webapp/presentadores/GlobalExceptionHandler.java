package com.kinastudio.webapp.presentadores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kinastudio.webapp.dominio.excepciones.ClienteException;
import com.kinastudio.webapp.dominio.excepciones.MaterialException;
import com.kinastudio.webapp.dominio.excepciones.ServicioException;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity<String> handleUsuarioInvalidoException(UsuarioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ClienteException.class)
    public ResponseEntity<String> handleClienteInvalidoException(ClienteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MaterialException.class)
    public ResponseEntity<String> handleMaterialInvalidoException(MaterialException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ServicioException.class)
    public ResponseEntity<String> handleServicioInvalidoException(ServicioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}