package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kinastudio.webapp.dominio.excepciones.ClienteException;
import com.kinastudio.webapp.dominio.repositorios.ClienteRepository;

@SpringBootTest(properties = { "KINA_ADMIN_EMAIL=", "KINA_ADMIN_PASSWORD=" })
@Transactional
class SistemaStudioTest {

    @Autowired
    private SistemaStudio sistema;

    @Autowired
    private ClienteRepository repositorio;

    // ---------- Crear ----------

    @Test
    void creaUnClienteValido() throws ClienteException {
        Cliente c = sistema.crearCliente("Ana Pérez", "ana@mail.com", "099123456", "notas");
        assertNotNull(c.getId());
        assertEquals("Ana Pérez", c.getNombreCompleto());
        assertEquals(1, repositorio.count());
    }

    @Test
    void rechazaNombreVacio() {
        ClienteException ex = assertThrows(ClienteException.class, () ->
            sistema.crearCliente("  ", "ana@mail.com", "099123456", null));
        assertEquals("El nombre completo es obligatorio.", ex.getMessage());
    }

    @Test
    void rechazaTelefonoVacio() {
        ClienteException ex = assertThrows(ClienteException.class, () ->
            sistema.crearCliente("Ana Pérez", "ana@mail.com", " ", null));
        assertEquals("El teléfono es obligatorio.", ex.getMessage());
    }

    @Test
    void rechazaEmailInvalido() {
        for (String invalido : new String[] { "no-es-un-mail", "ana@", "@mail.com", "ana@mail", "ana@@mail.com" }) {
            ClienteException ex = assertThrows(ClienteException.class, () ->
                sistema.crearCliente("Ana Pérez", invalido, "099123456", null));
            assertEquals("El email no es válido.", ex.getMessage());
        }
    }

        // ---------- Listar ----------

    @Test
    void listarSinClientesDevuelveVacio() throws ClienteException {
        assertTrue(sistema.listarClientes().isEmpty());
    }

    @Test
    void listarDevuelveTodosLosClientes() throws ClienteException {
        sistema.crearCliente("Ana Pérez", "ana@mail.com", "099123456", null);
        sistema.crearCliente("Luz Gómez", "luz@mail.com", "099654321", null);
        assertEquals(2, sistema.listarClientes().size());
    }

        // ---------- Modificar ----------

    @Test
    void modificarGuardaLosDatosNuevos() throws ClienteException {
        Cliente c = sistema.crearCliente("Ana Pérez", "ana@mail.com", "099123456", null);

        Cliente modificado = sistema.modificarCliente(c.getId(), "Ana Gómez", "nuevo@mail.com", "098000000", "vip");

        assertEquals("Ana Gómez", modificado.getNombreCompleto());
        assertEquals("nuevo@mail.com", modificado.getEmail());
        assertEquals("098000000", modificado.getTelefono());
        assertEquals("vip", modificado.getNotas());
    }

    @Test
    void modificarFallaSiNoExiste() {
        ClienteException ex = assertThrows(ClienteException.class, () ->
            sistema.modificarCliente(9999L, "Ana", "a@mail.com", "099", null));
        assertEquals("Cliente no encontrado", ex.getMessage());
    }

        // ---------- Eliminar ----------

    @Test
    void eliminarBorraElCliente() throws ClienteException {
        Cliente c = sistema.crearCliente("Ana Pérez", "ana@mail.com", "099123456", null);

        sistema.eliminarCliente(c.getId());

        assertFalse(repositorio.existsById(c.getId()));
        assertEquals(0, repositorio.count());
    }

    @Test
    void eliminarFallaSiNoExiste() {
        ClienteException ex = assertThrows(ClienteException.class, () ->
            sistema.eliminarCliente(9999L));
        assertEquals("Cliente no encontrado", ex.getMessage());
    }
}