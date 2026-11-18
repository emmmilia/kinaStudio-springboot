package com.kinastudio.webapp.dominio.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kinastudio.webapp.dominio.TokenRecuperacion;
import com.kinastudio.webapp.dominio.Usuario;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

    TokenRecuperacion findByTokenHash(String tokenHash);

    List<TokenRecuperacion> findByUsuarioAndUsadoFalse(Usuario usuario);
}