package com.kinastudio.webapp.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kinastudio.webapp.dominio.excepciones.ServicioException;
import com.kinastudio.webapp.dominio.repositorios.MaterialRepository;
import com.kinastudio.webapp.dominio.repositorios.ServicioRepository;
import com.kinastudio.webapp.dominio.repositorios.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// Pruebas del CRUD de servicios (RF.53 y RF.54) contra la base H2 en memoria.
// @Transactional deshace lo que guarda cada test, así no se afectan entre sí.
@SpringBootTest(properties = { "KINA_ADMIN_EMAIL=", "KINA_ADMIN_PASSWORD=" })
@Transactional
class SistemaStudioServicioTest {

    private static final BigDecimal PRECIO = new BigDecimal("1200.00");
    private static final int TIEMPO = 60;

    @Autowired
    private SistemaStudio sistema;

    @Autowired
    private ServicioRepository repositorio;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MaterialRepository materialRepository;

    // Se usa para obligar a releer el servicio desde la base y comprobar
    // que las relaciones muchos a muchos se guardaron de verdad.
    @PersistenceContext
    private EntityManager entityManager;

    private Usuario crearEsteticista(String usuario) {
        return usuarioRepository.save(new Usuario(usuario + "@kina.test", usuario,
                "hash-de-prueba", "Esteticista " + usuario, TipoUsuario.ESTETICISTA));
    }

    private Material crearMaterial(String nombre) {
        return materialRepository.save(new Material(nombre, 10, new BigDecimal("350.00"),
                "OPI", Categoria.NAILS, 3));
    }

    private Servicio crearManicura() throws ServicioException {
        return sistema.crearServicio("Manicura", List.of(crearEsteticista("ana")), TIEMPO,
                List.of(crearMaterial("Esmalte rojo")), PRECIO);
    }

    // Guarda lo pendiente en la base y vacía la memoria de JPA,
    // así el próximo findById lee de la base y no del objeto en memoria.
    private Servicio releer(long id) {
        entityManager.flush();
        entityManager.clear();
        return repositorio.findById(id).orElseThrow();
    }

    // ---------- Crear ----------

    @Test
    void creaUnServicioValido() throws ServicioException {
        Servicio s = crearManicura();

        assertTrue(s.getId() > 0);
        assertEquals("Manicura", s.getNombre());
        assertEquals(TIEMPO, s.getTiempoEstimado());
        assertEquals(0, PRECIO.compareTo(s.getPrecioBase()));
        assertEquals(1, repositorio.count());
    }

    @Test
    void guardaEsteticistasYMaterialesEnLaBase() throws ServicioException {
        Usuario ana = crearEsteticista("ana");
        Usuario lu = crearEsteticista("lu");
        Material esmalte = crearMaterial("Esmalte rojo");

        Servicio s = sistema.crearServicio("Kapping", List.of(ana, lu), 90, List.of(esmalte), PRECIO);
        Servicio leido = releer(s.getId());

        assertEquals(2, leido.getEsteticistasHabilitadas().size());
        assertEquals(1, leido.getMateriales().size());
        assertEquals("Esmalte rojo", leido.getMateriales().get(0).getNombre());
    }

    @Test
    void aceptaServicioSinMateriales() {
        assertDoesNotThrow(() ->
            sistema.crearServicio("Perfilado de cejas", List.of(crearEsteticista("ana")), 30,
                    new ArrayList<>(), PRECIO));
    }

    @Test
    void sacaLosEspaciosDelNombre() throws ServicioException {
        Servicio s = sistema.crearServicio("  Manicura  ", new ArrayList<>(), TIEMPO,
                new ArrayList<>(), PRECIO);

        assertEquals("Manicura", s.getNombre());
    }

    @Test
    void aceptaPrecioEnCero() {
        assertDoesNotThrow(() ->
            sistema.crearServicio("Retoque", new ArrayList<>(), 15, new ArrayList<>(), BigDecimal.ZERO));
    }

    @Test
    void rechazaNombreVacio() {
        for (String vacio : new String[] { null, "", "   " }) {
            ServicioException ex = assertThrows(ServicioException.class, () ->
                sistema.crearServicio(vacio, new ArrayList<>(), TIEMPO, new ArrayList<>(), PRECIO));
            assertEquals("El nombre es obligatorio.", ex.getMessage());
        }
    }

    @Test
    void rechazaTiempoEnCeroONegativo() {
        for (int tiempo : new int[] { 0, -30 }) {
            ServicioException ex = assertThrows(ServicioException.class, () ->
                sistema.crearServicio("Manicura", new ArrayList<>(), tiempo, new ArrayList<>(), PRECIO));
            assertEquals("El tiempo estimado debe ser mayor a cero.", ex.getMessage());
        }
    }

    @Test
    void rechazaPrecioNegativo() {
        ServicioException ex = assertThrows(ServicioException.class, () ->
            sistema.crearServicio("Manicura", new ArrayList<>(), TIEMPO, new ArrayList<>(),
                    new BigDecimal("-1")));
        assertEquals("El precio base no puede ser negativo.", ex.getMessage());
    }

    @Test
    void unAltaRechazadaNoDejaNadaGuardado() {
        assertThrows(ServicioException.class, () ->
            sistema.crearServicio("Manicura", new ArrayList<>(), 0, new ArrayList<>(), PRECIO));
        assertEquals(0, repositorio.count());
    }

    // ---------- Listar ----------

    @Test
    void listarSinServiciosDevuelveVacio() throws ServicioException {
        assertTrue(sistema.listarServicios().isEmpty());
    }

    @Test
    void listarDevuelveTodosLosServicios() throws ServicioException {
        crearManicura();
        sistema.crearServicio("Lifting de pestañas", List.of(crearEsteticista("lu")), 75,
                new ArrayList<>(), new BigDecimal("1500.00"));

        assertEquals(2, sistema.listarServicios().size());
    }

    // ---------- Modificar ----------

    @Test
    void modificarGuardaLosDatosNuevos() throws ServicioException {
        Servicio s = crearManicura();
        Usuario lu = crearEsteticista("lu");
        Material base = crearMaterial("Base coat");
        Material top = crearMaterial("Top coat");

        sistema.modificarServicio(s.getId(), "Manicura semipermanente", List.of(lu), 80,
                List.of(base, top), new BigDecimal("1400.00"));
        Servicio leido = releer(s.getId());

        assertEquals("Manicura semipermanente", leido.getNombre());
        assertEquals(80, leido.getTiempoEstimado());
        assertEquals(0, new BigDecimal("1400.00").compareTo(leido.getPrecioBase()));
        assertEquals(1, leido.getEsteticistasHabilitadas().size());
        assertEquals("lu", leido.getEsteticistasHabilitadas().get(0).getNombreUsuario());
        assertEquals(2, leido.getMateriales().size());
    }

    @Test
    void modificarFallaSiNoExiste() {
        ServicioException ex = assertThrows(ServicioException.class, () ->
            sistema.modificarServicio(9999L, "Manicura", new ArrayList<>(), TIEMPO,
                    new ArrayList<>(), PRECIO));
        assertEquals("Servicio no encontrado", ex.getMessage());
    }

    @Test
    void modificarRechazaDatosInvalidosYNoCambiaNada() throws ServicioException {
        Servicio s = crearManicura();

        ServicioException ex = assertThrows(ServicioException.class, () ->
            sistema.modificarServicio(s.getId(), "   ", new ArrayList<>(), TIEMPO,
                    new ArrayList<>(), PRECIO));
        assertEquals("El nombre es obligatorio.", ex.getMessage());

        Servicio leido = releer(s.getId());
        assertEquals("Manicura", leido.getNombre());
        assertEquals(1, leido.getEsteticistasHabilitadas().size());
    }

    @Test
    void modificarRechazaTiempoEnCero() throws ServicioException {
        Servicio s = crearManicura();

        ServicioException ex = assertThrows(ServicioException.class, () ->
            sistema.modificarServicio(s.getId(), "Manicura", new ArrayList<>(), 0,
                    new ArrayList<>(), PRECIO));
        assertEquals("El tiempo estimado debe ser mayor a cero.", ex.getMessage());
    }

    @Test
    void modificarRechazaPrecioNegativo() throws ServicioException {
        Servicio s = crearManicura();

        ServicioException ex = assertThrows(ServicioException.class, () ->
            sistema.modificarServicio(s.getId(), "Manicura", new ArrayList<>(), TIEMPO,
                    new ArrayList<>(), new BigDecimal("-1")));
        assertEquals("El precio base no puede ser negativo.", ex.getMessage());
    }

    // ---------- Eliminar ----------

    @Test
    void eliminarBorraElServicio() throws ServicioException {
        Servicio s = crearManicura();

        sistema.eliminarServicio(s.getId());

        assertFalse(repositorio.existsById(s.getId()));
        assertEquals(0, repositorio.count());
    }

    @Test
    void eliminarNoBorraLasEsteticistasNiLosMateriales() throws ServicioException {
        Servicio s = crearManicura();
        long esteticistasAntes = usuarioRepository.count();
        long materialesAntes = materialRepository.count();

        sistema.eliminarServicio(s.getId());
        entityManager.flush();

        assertEquals(esteticistasAntes, usuarioRepository.count());
        assertEquals(materialesAntes, materialRepository.count());
    }

    @Test
    void eliminarFallaSiNoExiste() {
        ServicioException ex = assertThrows(ServicioException.class, () ->
            sistema.eliminarServicio(9999L));
        assertEquals("Servicio no encontrado", ex.getMessage());
    }
}
