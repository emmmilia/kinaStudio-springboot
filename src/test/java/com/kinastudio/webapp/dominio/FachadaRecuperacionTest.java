package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.kinastudio.webapp.dominio.excepciones.UsuarioException;
import com.kinastudio.webapp.dominio.repositorios.UsuarioRepository;

// Comprueba que la Fachada delega la recuperación de contraseña en SistemaAcceso.
@SpringBootTest(properties = { "KINA_ADMIN_EMAIL=", "KINA_ADMIN_PASSWORD=" })
@Transactional
class FachadaRecuperacionTest {

    @Autowired
    private Fachada fachada;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean
    private ServicioEmail servicioEmail;

    @Test
    void solicitaYRestableceLaPasswordAtravesDeLaFachada() throws UsuarioException {
        fachada.registrarEsteticista("ana@mail.com", "ana", "clave123", "Ana Pérez");

        fachada.solicitarRecuperacionPassword("ana@mail.com");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(servicioEmail).enviarRecuperacion(eq("ana@mail.com"), any(), captor.capture());

        fachada.restablecerPassword(captor.getValue(), "nueva456");

        assertTrue(encoder.matches("nueva456", usuarios.findByNombreUsuarioIgnoreCase("ana").getPasswordHash()));
    }
}