package com.kinastudio.webapp.config;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import com.kinastudio.webapp.dominio.Fachada;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;

/**
 * Crea el primer administrador al arrancar la app, a partir de variables de entorno.
 * Si no están definidas, no hace nada.
 */
@Component
public class AdministradorInicial implements ApplicationRunner {

    private final Fachada fachada;

    @Value("${KINA_ADMIN_EMAIL:}")
    private String email;

    @Value("${KINA_ADMIN_PASSWORD:}")
    private String password;

    private static final Logger log = LoggerFactory.getLogger(AdministradorInicial.class);

    public AdministradorInicial(Fachada fachada) {
        this.fachada = fachada;
    }

    @Override
    public void run(ApplicationArguments args) throws UsuarioException {
        if (email.isBlank() || password.isBlank()) {
            return;
        }
        try{
          fachada.registrarAdministradorInicial(email, "admin", password, "Administrador");  
        } catch (UsuarioException e){
            log.warn("No se creó el administrador inicial: {}", e.getMessage());
        }
        
    }
}