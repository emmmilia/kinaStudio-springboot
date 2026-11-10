package com.kinastudio.webapp.presentadores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kinastudio.webapp.dominio.excepciones.UsuarioException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity handleUsuarioInvalidoException(UsuarioException ex) {
        return ResponseEntity.status(299).body(ex.getMessage());
    }

}