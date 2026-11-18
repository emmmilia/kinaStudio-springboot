package com.kinastudio.webapp.dominio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Envía los mails por SMTP (Brevo). Si no hay usuario SMTP configurado (KINA_MAIL_USER vacía),
 * escribe el enlace en el log para poder probar el flujo en desarrollo sin mandar mails.
 */
@Component
public class ServicioEmailSmtp implements ServicioEmail {

    private static final Logger LOG = LoggerFactory.getLogger(ServicioEmailSmtp.class);
    private static final String ASUNTO = "Kina Studio - Restablecer contraseña";

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;
    private final String urlFront;
    private final String usuarioSmtp;

    public ServicioEmailSmtp(ObjectProvider<JavaMailSender> mailSender,
            @Value("${kina.mail.remitente:}") String remitente,
            @Value("${kina.front.url:http://localhost:5173}") String urlFront,
            @Value("${spring.mail.username:}") String usuarioSmtp) {
        this.mailSender = mailSender;
        this.remitente = remitente;
        this.urlFront = quitarBarrasFinales(urlFront);
        this.usuarioSmtp = usuarioSmtp;
    }

    @Override
    public void enviarRecuperacion(String destinatario, String nombreCompleto, String token) {
        String enlace = this.urlFront + "/?token=" + token;
        JavaMailSender sender = this.mailSender.getIfAvailable();

        if (sender == null || this.usuarioSmtp.isBlank()) {
            LOG.warn("SMTP sin configurar: no se envió el mail. Enlace de recuperación para {}: {}",
                    destinatario, enlace);
            return;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        if (!this.remitente.isBlank()) {
            mensaje.setFrom(this.remitente);
        }
        mensaje.setTo(destinatario);
        mensaje.setSubject(ASUNTO);
        mensaje.setText("Hola " + nombreCompleto + ",\n\n"
                + "Recibimos un pedido para restablecer la contraseña de tu cuenta de Kina Studio.\n"
                + "Para elegir una contraseña nueva ingresá al siguiente enlace (vale 30 minutos):\n\n"
                + enlace + "\n\n"
                + "Si no fuiste vos, ignorá este mensaje: tu contraseña no se modifica.");
        try {
            sender.send(mensaje);
        } catch (MailException e) {
            LOG.error("No se pudo enviar el mail de recuperación a {}: {}", destinatario, e.getMessage());
        }
    }

    private static String quitarBarrasFinales(String url) {
        String limpia = url;
        while (limpia.endsWith("/")) {
            limpia = limpia.substring(0, limpia.length() - 1);
        }
        return limpia;
    }
}