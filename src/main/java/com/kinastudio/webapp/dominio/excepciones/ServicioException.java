package com.kinastudio.webapp.dominio.excepciones;

public class ServicioException extends Exception{
    public ServicioException(String mensaje){
        super(mensaje);
    }
}