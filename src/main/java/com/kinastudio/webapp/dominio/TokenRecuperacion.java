package com.kinastudio.webapp.dominio;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens_recuperacion")
public class TokenRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime vencimiento;

    @Column(nullable = false)
    private boolean usado;

    protected TokenRecuperacion() {
    }

    public TokenRecuperacion(Usuario usuario, String tokenHash, LocalDateTime vencimiento) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.vencimiento = vencimiento;
        this.usado = false;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public String getTokenHash() {
        return this.tokenHash;
    }

    public LocalDateTime getVencimiento() {
        return this.vencimiento;
    }

    public boolean isUsado() {
        return this.usado;
    }

    public void marcarUsado() {
        this.usado = true;
    }

    public boolean estaVigente(LocalDateTime ahora) {
        return !this.usado && ahora.isBefore(this.vencimiento);
    }
}