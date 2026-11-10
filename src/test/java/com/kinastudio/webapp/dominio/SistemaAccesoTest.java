package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kinastudio.webapp.dominio.SistemaAcceso;
import com.kinastudio.webapp.dominio.TipoUsuario;
import com.kinastudio.webapp.dominio.Usuario;
import com.kinastudio.webapp.dominio.excepciones.UsuarioException;

class SistemaAccesoTest {

    private PasswordEncoder encoder;
    private SistemaAcceso sistema;

    @BeforeEach
    void setUp() {
        // Fuerza 4 (la mínima de BCrypt): hace que los tests corran rápido
        encoder = new BCryptPasswordEncoder(4);
        sistema = new SistemaAcceso(encoder);
    }

    // ---------- Altas válidas ----------

    @Test
    void creaUnaEsteticistaValida() throws UsuarioException {
        Usuario u = sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        assertEquals("ana@mail.com", u.getEmail());
        assertEquals("ana", u.getNombreUsuario());
        assertEquals("Ana Pérez", u.getNombreCompleto());
        assertEquals(TipoUsuario.ESTETICISTA, u.getRol());
    }

    @Test
    void creaUnAdministrador() throws UsuarioException {
        Usuario u = sistema.crearUsuario("admin@mail.com", "admin", "clave123", "Administrador", TipoUsuario.ADMINISTRADOR);

        assertEquals(TipoUsuario.ADMINISTRADOR, u.getRol());
    }

    @Test
    void asignaIdsDistintosYCrecientes() throws UsuarioException {
        Usuario primero = sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);
        Usuario segundo = sistema.crearUsuario("luz@mail.com", "luz", "clave123", "Luz Gómez", TipoUsuario.ESTETICISTA);

        assertNotEquals(primero.getId(), segundo.getId());
        assertTrue(segundo.getId() > primero.getId());
    }

    @Test
    void guardaLaContrasenaHasheada() throws UsuarioException {
        Usuario u = sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        assertNotEquals("clave123", u.getPasswordHash());
        assertTrue(encoder.matches("clave123", u.getPasswordHash()));
    }

    // ---------- Duplicados ----------

    @Test
    void rechazaNombreDeUsuarioRepetidoSinImportarMayusculas() throws UsuarioException {
        sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("otra@mail.com", "ANA", "clave123", "Otra Ana", TipoUsuario.ESTETICISTA));

        assertEquals("Ese nombre de usuario no está disponible.", ex.getMessage());
    }

    @Test
    void rechazaMailRepetidoSinImportarMayusculas() throws UsuarioException {
        sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("ANA@mail.com", "ana2", "clave123", "Ana Dos", TipoUsuario.ESTETICISTA));

        assertEquals("Ese mail ya está registrado.", ex.getMessage());
    }

    // ---------- Campos obligatorios ----------

    @Test
    void rechazaNombreDeUsuarioVacio() {
        for (String vacio : new String[] { null, "", "   " }) {
            UsuarioException ex = assertThrows(UsuarioException.class, () ->
                sistema.crearUsuario("ana@mail.com", vacio, "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA));

            assertEquals("Usuario y contraseña son obligatorios.", ex.getMessage());
        }
    }

    @Test
    void rechazaPasswordVacia() {
        for (String vacia : new String[] { null, "", "   " }) {
            UsuarioException ex = assertThrows(UsuarioException.class, () ->
                sistema.crearUsuario("ana@mail.com", "ana", vacia, "Ana Pérez", TipoUsuario.ESTETICISTA));

            assertEquals("Usuario y contraseña son obligatorios.", ex.getMessage());
        }
    }

    @Test
    void rechazaMailInvalido() {
        for (String invalido : new String[] { null, "", "no-es-un-mail", "ana@", "@mail.com", "ana@mail" }) {
            UsuarioException ex = assertThrows(UsuarioException.class, () ->
                sistema.crearUsuario(invalido, "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA));

            assertEquals("El mail no es válido.", ex.getMessage());
        }
    }

    @Test
    void rechazaNombreCompletoVacio() {
        for (String vacio : new String[] { null, "", "   " }) {
            UsuarioException ex = assertThrows(UsuarioException.class, () ->
                sistema.crearUsuario("ana@mail.com", "ana", "clave123", vacio, TipoUsuario.ESTETICISTA));

            assertEquals("El nombre completo es obligatorio.", ex.getMessage());
        }
    }

    @Test
    void rechazaRolNulo() {
        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", null));

        assertEquals("El rol es obligatorio.", ex.getMessage());
    }

    @Test
    void unAltaRechazadaNoDejaUsuarioGuardado() throws UsuarioException {
        assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("no-es-un-mail", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA));

        // Si el alta anterior hubiera guardado algo, esta fallaría por nombre de usuario repetido
        Usuario u = sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);
        assertEquals("ana", u.getNombreUsuario());
    }
}