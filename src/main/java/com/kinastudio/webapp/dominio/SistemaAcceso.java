package com.kinastudio.webapp.dominio;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dominio.repositorios.UsuarioRepository;

@Service
public class SistemaAcceso {

    //Coinciden con el largo de las columnas de la tabla usuarios
    private static final int MAX_EMAIL = 254;
    private static final int MAX_NOMBRE_USUARIO = 50;
    private static final int MAX_NOMBRE_COMPLETO = 100;
    //BCrypt solo usa los primeros 72 bytes de la contraseña
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public SistemaAcceso(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario crearUsuario(String email, String nombreUsuario, String password, String nombreCompleto,
            TipoUsuario rol) throws UsuarioException {
        if (esVacio(nombreUsuario) || esVacio(password)) {
            throw new UsuarioException("Usuario y contraseña son obligatorios.");
        }
        if (!esMailValido(email)) {
            throw new UsuarioException("El mail no es válido.");
        }
        if (esVacio(nombreCompleto)) {
            throw new UsuarioException("El nombre completo es obligatorio.");
        }
        if (rol == null) {
            throw new UsuarioException("El rol es obligatorio.");
        }
        validarLargos(email, nombreUsuario, password, nombreCompleto);

        if (usuarioRepository.existsByNombreUsuarioIgnoreCase(nombreUsuario)) {
            throw new UsuarioException("Ese nombre de usuario no está disponible.");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new UsuarioException("Ese mail ya está registrado.");
        }

        Usuario nuevo = new Usuario(email, nombreUsuario, passwordEncoder.encode(password), nombreCompleto, rol);
        try {
            return usuarioRepository.save(nuevo);
        } catch (DataIntegrityViolationException e) {
            // Dos altas simultáneas con el mismo dato: la base lo frena con sus restricciones únicas
            throw new UsuarioException("Ese mail o nombre de usuario ya está registrado.");
        }
    }

    private void validarLargos(String email, String nombreUsuario, String password, String nombreCompleto)
            throws UsuarioException {
        if (email.length() > MAX_EMAIL) {
            throw new UsuarioException("El mail no puede superar los " + MAX_EMAIL + " caracteres.");
        }
        if (nombreUsuario.length() > MAX_NOMBRE_USUARIO) {
            throw new UsuarioException(
                    "El nombre de usuario no puede superar los " + MAX_NOMBRE_USUARIO + " caracteres.");
        }
        if (nombreCompleto.length() > MAX_NOMBRE_COMPLETO) {
            throw new UsuarioException(
                    "El nombre completo no puede superar los " + MAX_NOMBRE_COMPLETO + " caracteres.");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new UsuarioException("La contraseña es demasiado larga.");
        }
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }


    private boolean esMailValido(String email) {
        if (esVacio(email) || email.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }
        int arroba = email.indexOf('@');
        if (arroba <= 0 || arroba != email.lastIndexOf('@')) {
            return false;
        }
        String dominio = email.substring(arroba + 1);
        int punto = dominio.indexOf('.');
        return punto > 0 && !dominio.endsWith(".") && !dominio.contains("..");
    }
}