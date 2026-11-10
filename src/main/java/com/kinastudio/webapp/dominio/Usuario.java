package com.kinastudio.webapp.dominio;

public class Usuario {
    private long id;
    private static long idAuto;
    private String email;
    private String nombreUsuario;
    private String passwordHash;
    private String nombreCompleto;
    private String comentario;
    private TipoUsuario rol;

    public Usuario(String email, String nombreUsuario, String pass, String nombreCompleto, TipoUsuario rol){
        this.id = ++idAuto;
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = pass;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public long getId(){
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }

    public String getNombreUsuario(){
        return this.nombreUsuario;
    }

    public String getPasswordHash(){
        return this.passwordHash;
    }

    public String getNombreCompleto(){
        return this.nombreCompleto;
    }

    public TipoUsuario getRol(){
        return this.rol;
    }
}
