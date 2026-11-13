package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dominio.repositorios.UsuarioRepository;

// Se prueba contra la base H2 en memoria (src/test/resources/application.properties).
// @Transactional deshace lo que cada test guarda, así los tests no se afectan entre sí.
// Las dos propiedades vacías evitan que se cree el administrador inicial si la computadora
// tiene definidas KINA_ADMIN_EMAIL y KINA_ADMIN_PASSWORD.
@SpringBootTest(properties = { "KINA_ADMIN_EMAIL=", "KINA_ADMIN_PASSWORD=" })
@Transactional
class SistemaAccesoTest {

    @Autowired
    private SistemaAcceso sistema;

    @Autowired
    private UsuarioRepository repositorio;

    @Autowired
    private PasswordEncoder encoder;

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

        assertNotNull(primero.getId());
        assertNotEquals(primero.getId(), segundo.getId());
        assertTrue(segundo.getId() > primero.getId());
    }

    @Test
    void guardaLaContrasenaHasheada() throws UsuarioException {
        Usuario u = sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        assertNotEquals("clave123", u.getPasswordHash());
        assertTrue(encoder.matches("clave123", u.getPasswordHash()));
    }

    @Test
    void elUsuarioQuedaGuardadoEnLaBase() throws UsuarioException {
        Usuario creado = sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        Usuario leido = repositorio.findByNombreUsuarioIgnoreCase("ana");

        assertNotNull(leido);
        assertEquals(creado.getId(), leido.getId());
        assertEquals("ana@mail.com", leido.getEmail());
        assertEquals("ana", leido.getNombreUsuario());
        assertEquals(TipoUsuario.ESTETICISTA, leido.getRol());
        assertEquals(1, repositorio.count());
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

    // ---------- Largos máximos (los de las columnas de la tabla) ----------

    @Test
    void rechazaMailDemasiadoLargo() {
        String largo = "a".repeat(250) + "@mail.com";

        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario(largo, "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA));

        assertEquals("El mail no puede superar los 254 caracteres.", ex.getMessage());
    }

    @Test
    void rechazaNombreDeUsuarioDemasiadoLargo() {
        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("ana@mail.com", "a".repeat(51), "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA));

        assertEquals("El nombre de usuario no puede superar los 50 caracteres.", ex.getMessage());
    }

    @Test
    void rechazaNombreCompletoDemasiadoLargo() {
        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("ana@mail.com", "ana", "clave123", "A".repeat(101), TipoUsuario.ESTETICISTA));

        assertEquals("El nombre completo no puede superar los 100 caracteres.", ex.getMessage());
    }

    @Test
    void rechazaPasswordDemasiadoLarga() {
        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("ana@mail.com", "ana", "x".repeat(73), "Ana Pérez", TipoUsuario.ESTETICISTA));

        assertEquals("La contraseña es demasiado larga.", ex.getMessage());
    }

    // ---------- Un alta rechazada no deja nada guardado ----------

    @Test
    void unAltaRechazadaNoDejaUsuarioGuardado() {
        assertThrows(UsuarioException.class, () ->
            sistema.crearUsuario("no-es-un-mail", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA));

        assertEquals(0, repositorio.count());
    }

    // ---------- Cambio de contraseña (RF.8) ----------

    @Test
    void cambiaLaPasswordCuandoLaActualEsCorrecta() throws UsuarioException {
        sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        sistema.cambiarPassword("ana", "clave123", "otraClave456");

        Usuario guardado = repositorio.findByNombreUsuarioIgnoreCase("ana");
        assertTrue(encoder.matches("otraClave456", guardado.getPasswordHash()));
        assertFalse(encoder.matches("clave123", guardado.getPasswordHash()));
    }

    @Test
    void rechazaCambioConPasswordActualIncorrecta() throws UsuarioException {
        sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.cambiarPassword("ana", "equivocada", "otraClave456"));

        assertEquals("La contraseña actual es incorrecta.", ex.getMessage());
        // La contraseña no cambió
        Usuario guardado = repositorio.findByNombreUsuarioIgnoreCase("ana");
        assertTrue(encoder.matches("clave123", guardado.getPasswordHash()));
    }

    @Test
    void rechazaCambioSiLaNuevaEsIgualALaActual() throws UsuarioException {
        sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.cambiarPassword("ana", "clave123", "clave123"));

        assertEquals("La contraseña nueva debe ser distinta a la actual.", ex.getMessage());
    }

    @Test
    void rechazaCambioConPasswordsVacias() throws UsuarioException {
        sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        for (String vacia : new String[] { null, "", "   " }) {
            UsuarioException sinActual = assertThrows(UsuarioException.class, () ->
                sistema.cambiarPassword("ana", vacia, "otraClave456"));
            assertEquals("Debe completar la contraseña actual y la nueva.", sinActual.getMessage());

            UsuarioException sinNueva = assertThrows(UsuarioException.class, () ->
                sistema.cambiarPassword("ana", "clave123", vacia));
            assertEquals("Debe completar la contraseña actual y la nueva.", sinNueva.getMessage());
        }
    }

    @Test
    void rechazaCambioConPasswordNuevaDemasiadoLarga() throws UsuarioException {
        sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);

        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.cambiarPassword("ana", "clave123", "x".repeat(73)));

        assertEquals("La contraseña es demasiado larga.", ex.getMessage());
    }

    @Test
    void rechazaCambioDeUnUsuarioInexistente() {
        UsuarioException ex = assertThrows(UsuarioException.class, () ->
            sistema.cambiarPassword("nadie", "clave123", "otraClave456"));

        assertEquals("Usuario no encontrado.", ex.getMessage());
    }
}