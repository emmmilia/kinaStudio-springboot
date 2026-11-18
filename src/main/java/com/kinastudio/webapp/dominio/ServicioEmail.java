package com.kinastudio.webapp.dominio;

public interface ServicioEmail {
    void enviarRecuperacion(String destinatario, String nombreCompleto, String token);
}