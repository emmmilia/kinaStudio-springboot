package com.kinastudio.webapp.dominio;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.kinastudio.webapp.dominio.excepciones.UsuarioException;

@Component
public class Fachada {

    private final SistemaStudio sistemaStudio;
    private final SistemaAcceso sistemaAcceso;
    private final SistemaAgenda sistemaAgenda;
    private final SistemaPago sistemaPago;

    public Fachada(SistemaStudio sistemaStudio, SistemaAcceso sistemaAcceso,
                   SistemaAgenda sistemaAgenda, SistemaPago sistemaPago) {
        this.sistemaStudio = sistemaStudio;
        this.sistemaAcceso = sistemaAcceso;
        this.sistemaAgenda = sistemaAgenda;
        this.sistemaPago = sistemaPago;
    }

    public Usuario modificarUsuario(String nombreUsuario, String email, String nombreCompleto, String comentario) throws UsuarioException{
        return sistemaAcceso.modificarUsuario(nombreUsuario, email, nombreCompleto, comentario);
    }

    public Usuario registrarEsteticista(String email, String nombreUsuario, String password,
                                        String nombreCompleto) throws UsuarioException {
        return sistemaAcceso.crearUsuario(email, nombreUsuario, password, nombreCompleto, TipoUsuario.ESTETICISTA);
    }

    public Usuario registrarAdministradorInicial(String email, String nombreUsuario, String password,
                                                 String nombreCompleto) throws UsuarioException {
        return sistemaAcceso.crearUsuario(email, nombreUsuario, password, nombreCompleto, TipoUsuario.ADMINISTRADOR);
    }
    
    public void cambiarPassword(String nombreUsuario, String nuevaPassword, String passwordActual) throws UsuarioException{
        sistemaAcceso.cambiarPassword(nombreUsuario, passwordActual, nuevaPassword);
    }
}