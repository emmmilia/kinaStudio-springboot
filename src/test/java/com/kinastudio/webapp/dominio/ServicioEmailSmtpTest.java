package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

// Prueba unitaria sin Spring: el proveedor y el sender de mail son mocks, no se manda nada.
class ServicioEmailSmtpTest {

    private ObjectProvider<JavaMailSender> proveedor;
    private JavaMailSender sender;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void preparar() {
        proveedor = mock(ObjectProvider.class);
        sender = mock(JavaMailSender.class);
        when(proveedor.getIfAvailable()).thenReturn(sender);
    }

    private SimpleMailMessage enviarYCapturar(ServicioEmailSmtp servicio) {
        servicio.enviarRecuperacion("ana@mail.com", "Ana Pérez", "tok123");
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        return captor.getValue();
    }

    @Test
    void armaYEnviaElMailConElEnlace() {
        ServicioEmailSmtp servicio = new ServicioEmailSmtp(proveedor, "kina@gmail.com",
                "http://localhost:5173", "kina@gmail.com");

        SimpleMailMessage mensaje = enviarYCapturar(servicio);

        assertEquals("kina@gmail.com", mensaje.getFrom());
        assertEquals("ana@mail.com", mensaje.getTo()[0]);
        assertEquals("Kina Studio - Restablecer contraseña", mensaje.getSubject());
        assertTrue(mensaje.getText().contains("Hola Ana Pérez"));
        assertTrue(mensaje.getText().contains("http://localhost:5173/?token=tok123"));
    }

    @Test
    void laUrlDelFrontSeUsaSinBarrasFinales() {
        ServicioEmailSmtp servicio = new ServicioEmailSmtp(proveedor, "kina@gmail.com",
                "http://localhost:5173//", "kina@gmail.com");

        SimpleMailMessage mensaje = enviarYCapturar(servicio);

        assertTrue(mensaje.getText().contains("http://localhost:5173/?token=tok123"));
    }

    @Test
    void sinRemitenteNoSeFijaElDe() {
        ServicioEmailSmtp servicio = new ServicioEmailSmtp(proveedor, "",
                "http://localhost:5173", "kina@gmail.com");

        SimpleMailMessage mensaje = enviarYCapturar(servicio);

        assertNull(mensaje.getFrom());
    }

    @Test
    void sinUsuarioSmtpNoEnviaNada() {
        ServicioEmailSmtp servicio = new ServicioEmailSmtp(proveedor, "kina@gmail.com",
                "http://localhost:5173", "");

        servicio.enviarRecuperacion("ana@mail.com", "Ana Pérez", "tok123");

        verify(sender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sinJavaMailSenderNoFalla() {
        when(proveedor.getIfAvailable()).thenReturn(null);
        ServicioEmailSmtp servicio = new ServicioEmailSmtp(proveedor, "kina@gmail.com",
                "http://localhost:5173", "kina@gmail.com");

        servicio.enviarRecuperacion("ana@mail.com", "Ana Pérez", "tok123");

        verify(sender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void siFallaElEnvioNoLanzaExcepcion() {
        doThrow(new MailSendException("fallo")).when(sender).send(any(SimpleMailMessage.class));
        ServicioEmailSmtp servicio = new ServicioEmailSmtp(proveedor, "kina@gmail.com",
                "http://localhost:5173", "kina@gmail.com");

        servicio.enviarRecuperacion("ana@mail.com", "Ana Pérez", "tok123");

        verify(sender).send(any(SimpleMailMessage.class));
    }
}
