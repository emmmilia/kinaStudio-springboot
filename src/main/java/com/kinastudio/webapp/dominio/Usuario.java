package com.kinastudio.webapp.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    // BCrypt genera 60 caracteres; se deja margen por si se cambia de algoritmo
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(length = 500)
    private String comentario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario rol;

    // Lo exige JPA para poder armar los objetos al leerlos de la base
    protected Usuario() {
    }

    public Usuario(String email, String nombreUsuario, String passwordHash, String nombreCompleto, TipoUsuario rol) {
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNombreUsuario() {
        return this.nombreUsuario;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public String getNombreCompleto() {
        return this.nombreCompleto;
    }

    public String getComentario() {
        return this.comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public TipoUsuario getRol() {
        return this.rol;
    }

    public void setPassword(String passwordNueva){
        this.passwordHash = passwordNueva;
    }
}