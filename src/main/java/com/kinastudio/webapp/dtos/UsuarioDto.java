package com.kinastudio.webapp.dtos;

import com.kinastudio.webapp.dominio.TipoUsuario;
import com.kinastudio.webapp.dominio.Usuario;

public class UsuarioDto {
    private String email;
    private String nombreUsuario;
    private String nombreCompleto;
    private String comentario;
    private TipoUsuario rol;

    public UsuarioDto(){
    }

    public UsuarioDto(Usuario usuario){
        this.email = usuario.getEmail();
        this.nombreUsuario = usuario.getNombreUsuario();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.rol = usuario.getRol();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public TipoUsuario getRol() {
        return rol;
    }

    public void setRol(TipoUsuario rol) {
        this.rol = rol;
    }
}
