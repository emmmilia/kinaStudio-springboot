package com.kinastudio.webapp.dominio;

import java.lang.reflect.Array;
import java.util.ArrayList;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import java.util.regex.Pattern;

@Service 
public class SistemaAcceso {
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private final PasswordEncoder passwordEncoder;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s.]+(?:\\.[^@\\s.]+)+$");

    public SistemaAcceso(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario crearUsuario(String email, String nombreUsuario, String password, String nombreCompleto, TipoUsuario rol) throws UsuarioException {
        if (esVacio(nombreUsuario) || esVacio(password)) {
            throw new UsuarioException("Usuario y contraseña son obligatorios.");
        }
        if (esVacio(email) || !EMAIL.matcher(email).matches()) {
            throw new UsuarioException("El mail no es válido.");
        }
        if (esVacio(nombreCompleto)) {
            throw new UsuarioException("El nombre completo es obligatorio.");
        }
        if (rol == null) {
            throw new UsuarioException("El rol es obligatorio.");
        }
        for(Usuario u: usuarios){
            if(u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)){
                throw new UsuarioException("Ese nombre de usuario no está disponible.");
            }
            if(u.getEmail().equalsIgnoreCase(email)){
                throw new UsuarioException("Ese mail ya está registrado.");
            }
        }
        Usuario nuevo = new Usuario(email, nombreUsuario, passwordEncoder.encode(password), nombreCompleto, rol);
        usuarios.add(nuevo);
        return nuevo;
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
