package com.kinastudio.webapp.dominio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dominio.repositorios.TokenRecuperacionRepository;
import com.kinastudio.webapp.dominio.repositorios.UsuarioRepository;

@Service
public class SistemaAcceso {

    //Coinciden con el largo de las columnas de la tabla usuarios
    private static final int MAX_EMAIL = 254;
    private static final int MAX_NOMBRE_USUARIO = 50;
    private static final int MAX_NOMBRE_COMPLETO = 100;
    //BCrypt solo usa los primeros 72 bytes de la contraseña
    private static final int MAX_PASSWORD_BYTES = 72;
    //Recuperación de contraseña
    private static final int MINUTOS_VIGENCIA_TOKEN = 30;
    private static final int BYTES_TOKEN = 32;
    private static final String ENLACE_INVALIDO = "El enlace no es válido o expiró.";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServicioEmail servicioEmail;

    public SistemaAcceso(UsuarioRepository usuarioRepository, TokenRecuperacionRepository tokenRepository,
            PasswordEncoder passwordEncoder, ServicioEmail servicioEmail) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.servicioEmail = servicioEmail;
    }

    public Usuario crearUsuario(String email, String nombreUsuario, String password, String nombreCompleto,
            TipoUsuario rol) throws UsuarioException {
        if (esVacio(nombreUsuario) || esVacio(password)) {
            throw new UsuarioException("Usuario y contraseña son obligatorios.");
        }
        if (!ValidadorEmail.esValido(email)) {
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

    public Usuario modificarUsuario(String nombreUsuario, String email, String nombreCompleto, String comentario) throws UsuarioException{
        Usuario usuario = usuarioRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if (usuario == null) {
            throw new UsuarioException("Usuario no encontrado.");
        }
        if(!ValidadorEmail.esValido(email)){
            throw new UsuarioException("El mail no es válido.");
        }
        if(esVacio(nombreCompleto)){
            throw new UsuarioException("El nombre es obligatorio");
        }
         if (email.length() > MAX_EMAIL || nombreCompleto.length() > MAX_NOMBRE_COMPLETO){
            throw new UsuarioException("Algún dato supera el largo permitido.");
        }
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, usuario.getId())){
            throw new UsuarioException("Ese mail ya está registrado.");
        }
        usuario.setEmail(email);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setComentario(comentario);
        try{
            return usuarioRepository.save(usuario);
        }catch(DataIntegrityViolationException e){
            throw new UsuarioException("Ese mail ya está registrado");
        }
    }

    public Usuario obtenerUsuario(String nombreUsuario) throws UsuarioException {
        Usuario usuario = usuarioRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if (usuario == null) {
            throw new UsuarioException("Usuario no encontrado.");
        }
        return usuario;
    }

    public void cambiarPassword(String nombreUsuario, String passwordActual, String nuevaPassword) throws UsuarioException{
        if(esVacio(nuevaPassword)||esVacio(passwordActual)){
            throw new UsuarioException("Debe completar la contraseña actual y la nueva.");
        }
        if(nuevaPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES){
            throw new UsuarioException("La contraseña es demasiado larga.");
        }
        Usuario usuario = usuarioRepository.findByNombreUsuarioIgnoreCase(nombreUsuario);
        if(usuario == null){
            throw new UsuarioException("Usuario no encontrado.");
        }
        if (!passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
            throw new UsuarioException("La contraseña actual es incorrecta.");
        }
        if (passwordEncoder.matches(nuevaPassword, usuario.getPasswordHash())) {
            throw new UsuarioException("La contraseña nueva debe ser distinta a la actual.");
        }
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    /**
     * Paso 1 de la recuperación: si el mail pertenece a un usuario, le manda un enlace con un token.
     * Si no existe NO falla ni avisa: así nadie puede averiguar qué mails están registrados.
     */
    public void solicitarRecuperacionPassword(String email) throws UsuarioException {
        if (esVacio(email)) {
            throw new UsuarioException("Debe ingresar el mail.");
        }
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim());
        if (usuario == null) {
            return;
        }
        invalidarTokensPendientes(usuario);

        String token = generarToken();
        tokenRepository.save(new TokenRecuperacion(usuario, hashear(token),
                LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA_TOKEN)));
        servicioEmail.enviarRecuperacion(usuario.getEmail(), usuario.getNombreCompleto(), token);
    }

    /** Paso 2 de la recuperación: con el token del mail se elige la contraseña nueva. */
    @Transactional
    public void restablecerPassword(String token, String passwordNueva) throws UsuarioException {
        if (esVacio(passwordNueva)) {
            throw new UsuarioException("Debe completar la contraseña nueva.");
        }
        if (passwordNueva.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw new UsuarioException("La contraseña es demasiado larga.");
        }
        if (esVacio(token)) {
            throw new UsuarioException(ENLACE_INVALIDO);
        }
        TokenRecuperacion registro = tokenRepository.findByTokenHash(hashear(token));
        if (registro == null || !registro.estaVigente(LocalDateTime.now())) {
            throw new UsuarioException(ENLACE_INVALIDO);
        }
        Usuario usuario = registro.getUsuario();
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        // Marca este y cualquier otro enlace pendiente como usado
        invalidarTokensPendientes(usuario);
    }

    private void invalidarTokensPendientes(Usuario usuario) {
        var pendientes = tokenRepository.findByUsuarioAndUsadoFalse(usuario);
        pendientes.forEach(TokenRecuperacion::marcarUsado);
        tokenRepository.saveAll(pendientes);
    }

    private String generarToken() {
        byte[] bytes = new byte[BYTES_TOKEN];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashear(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 es obligatorio en toda JVM; no puede pasar
            throw new IllegalStateException("SHA-256 no disponible", e);
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

}