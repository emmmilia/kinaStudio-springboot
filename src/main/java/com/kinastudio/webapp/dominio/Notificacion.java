package com.kinastudio.webapp.dominio;

import java.time.LocalDateTime;

public class Notificacion {
    private long id;
    private String mensaje;
    private String email;
    private LocalDateTime fechaHora;

    public Notificacion(long id, String mensaje, String email, LocalDateTime fechaHora){
        this.id = id;
        this.mensaje = mensaje;
        this.email = email;
        this.fechaHora = fechaHora;
    }
}
