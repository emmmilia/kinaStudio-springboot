package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dominio.repositorios.TokenRecuperacionRepository;
import com.kinastudio.webapp.dominio.repositorios.UsuarioRepository;

// El envío de mails se reemplaza por un mock: los tests no mandan nada y capturan el token
// que se habría enviado en el enlace.
@SpringBootTest(properties = { "KINA_ADMIN_EMAIL=", "KINA_ADMIN_PASSWORD=" })
@Transactional
class RecuperacionPasswordTest {

    private static final String ENLACE_INVALIDO = "El enlace no es válido o expiró.";

    @Autowired
    private SistemaAcceso sistema;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private TokenRecuperacionRepository tokens;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean
    private ServicioEmail servicioEmail;

    private Usuario crearAna() throws UsuarioException {
        return sistema.crearUsuario("ana@mail.com", "ana", "clave123", "Ana Pérez", TipoUsuario.ESTETICISTA);
    }

    /** Pide la recuperación y devuelve el token que se mandó por mail. */
    private String pedirToken() throws UsuarioException {
        sistema.solicitarRecuperacionPassword("ana@mail.com");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(servicioEmail, times(1)).enviarRecuperacion(eq("ana@mail.com"), eq("Ana Pérez"), captor.capture());
        return captor.getValue();
    }

    // ---------- Solicitar ----------

    @Test
    void siElMailExisteSeEnviaUnEnlace() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        assertFalse(token.isBlank());
        assertEquals(1, tokens.count());
    }

    @Test
    void elMailSeBuscaSinImportarMayusculas() throws UsuarioException {
        crearAna();
        sistema.solicitarRecuperacionPassword("ANA@Mail.com");
        verify(servicioEmail).enviarRecuperacion(eq("ana@mail.com"), eq("Ana Pérez"), any());
    }

    @Test
    void siElMailNoExisteNoFallaNiEnviaNada() throws UsuarioException {
        sistema.solicitarRecuperacionPassword("nadie@mail.com");
        verify(servicioEmail, never()).enviarRecuperacion(any(), any(), any());
        assertEquals(0, tokens.count());
    }

    @Test
    void sinMailLanzaExcepcion() {
        UsuarioException e = assertThrows(UsuarioException.class, () -> sistema.solicitarRecuperacionPassword(" "));
        assertEquals("Debe ingresar el mail.", e.getMessage());
    }

    @Test
    void elTokenNoSeGuardaEnTextoPlano() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        assertTrue(tokens.findAll().stream().noneMatch(t -> t.getTokenHash().equals(token)));
    }

    // ---------- Restablecer ----------

    @Test
    void conElTokenSeCambiaLaPassword() throws UsuarioException {
        crearAna();
        String token = pedirToken();

        sistema.restablecerPassword(token, "nueva456");

        Usuario ana = usuarios.findByNombreUsuarioIgnoreCase("ana");
        assertTrue(encoder.matches("nueva456", ana.getPasswordHash()));
        assertFalse(encoder.matches("clave123", ana.getPasswordHash()));
    }

    @Test
    void elTokenSoloSirveUnaVez() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        sistema.restablecerPassword(token, "nueva456");

        UsuarioException e = assertThrows(UsuarioException.class, () -> sistema.restablecerPassword(token, "otra789"));
        assertEquals(ENLACE_INVALIDO, e.getMessage());
    }

    @Test
    void unTokenInexistenteEsInvalido() throws UsuarioException {
        crearAna();
        UsuarioException e = assertThrows(UsuarioException.class,
                () -> sistema.restablecerPassword("token-inventado", "nueva456"));
        assertEquals(ENLACE_INVALIDO, e.getMessage());
    }

    @Test
    void unTokenVencidoEsInvalido() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        // Se reemplaza el registro por uno idéntico pero ya vencido
        TokenRecuperacion original = tokens.findAll().get(0);
        tokens.delete(original);
        tokens.flush();
        tokens.save(new TokenRecuperacion(original.getUsuario(), original.getTokenHash(),
                LocalDateTime.now().minusMinutes(1)));

        UsuarioException e = assertThrows(UsuarioException.class, () -> sistema.restablecerPassword(token, "nueva456"));
        assertEquals(ENLACE_INVALIDO, e.getMessage());
        assertTrue(encoder.matches("clave123", usuarios.findByNombreUsuarioIgnoreCase("ana").getPasswordHash()));
    }

    @Test
    void pedirOtroEnlaceInvalidaElAnterior() throws UsuarioException {
        crearAna();
        sistema.solicitarRecuperacionPassword("ana@mail.com");
        sistema.solicitarRecuperacionPassword("ana@mail.com");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(servicioEmail, times(2)).enviarRecuperacion(any(), any(), captor.capture());
        List<String> enviados = captor.getAllValues();

        UsuarioException e = assertThrows(UsuarioException.class,
                () -> sistema.restablecerPassword(enviados.get(0), "nueva456"));
        assertEquals(ENLACE_INVALIDO, e.getMessage());
        sistema.restablecerPassword(enviados.get(1), "nueva456");
    }

    @Test
    void alRestablecerSeInvalidanLosOtrosEnlacesPendientes() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        sistema.restablecerPassword(token, "nueva456");
        assertTrue(tokens.findByUsuarioAndUsadoFalse(usuarios.findByNombreUsuarioIgnoreCase("ana")).isEmpty());
    }

    @Test
    void laPasswordNuevaEsObligatoria() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        UsuarioException e = assertThrows(UsuarioException.class, () -> sistema.restablecerPassword(token, " "));
        assertEquals("Debe completar la contraseña nueva.", e.getMessage());
    }

    @Test
    void laPasswordNuevaNoPuedeSerDemasiadoLarga() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        UsuarioException e = assertThrows(UsuarioException.class,
                () -> sistema.restablecerPassword(token, "a".repeat(73)));
        assertEquals("La contraseña es demasiado larga.", e.getMessage());
    }

    @Test
    void unaPasswordInvalidaNoGastaElToken() throws UsuarioException {
        crearAna();
        String token = pedirToken();
        assertThrows(UsuarioException.class, () -> sistema.restablecerPassword(token, "a".repeat(73)));
        sistema.restablecerPassword(token, "nueva456");
    }

    @Test
    void sinTokenEsInvalido() {
        UsuarioException e = assertThrows(UsuarioException.class, () -> sistema.restablecerPassword(null, "nueva456"));
        assertEquals(ENLACE_INVALIDO, e.getMessage());
    }
}