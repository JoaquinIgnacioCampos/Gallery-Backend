package com.uade.tpo.grupo11.gallery;

import com.uade.tpo.grupo11.gallery.entities.*;
import com.uade.tpo.grupo11.gallery.controllers.obra.ObraController;
import com.uade.tpo.grupo11.gallery.exceptions.GlobalExceptionHandler;
import com.uade.tpo.grupo11.gallery.services.obra.ObraService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:precios;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@Transactional
class ObraPrecioTests {
    @Autowired EntityManager em;
    @Autowired ObraService service;
    MockMvc mvc;
    PerfilArtista artista;
    Obra dentro, extremos, sinVariantes, otroArtista;
    TamanioLienzo tamanio;
    Estilo categoria;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new ObraController(service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        artista = artista("uno");
        tamanio = new TamanioLienzo();
        tamanio.setNombre_tamanio("Prueba");
        tamanio.setAncho_lienzo(10.0);
        tamanio.setLargo_lienzo(10.0);
        em.persist(tamanio);
        dentro = obra(artista, "100.25", "200.50");
        extremos = obra(artista, "50", "300");
        sinVariantes = obra(artista);
        otroArtista = obra(artista("dos"), "150");
        categoria = new Estilo();
        categoria.setNombreEstilo("Paisaje");
        em.persist(categoria);
        Estilo otraCategoria = new Estilo();
        otraCategoria.setNombreEstilo("Retrato");
        em.persist(otraCategoria);
        dentro.getEstilos().add(categoria);
        dentro.getEstilos().add(otraCategoria);
        sinVariantes.getEstilos().add(categoria);
        otroArtista.getEstilos().add(categoria);
        em.flush();
    }

    PerfilArtista artista(String nombre) {
        Usuario usuario = new Usuario();
        usuario.setNombre_usuario(nombre);
        usuario.setContrasenia_usuario("solo-prueba");
        em.persist(usuario);
        PerfilArtista perfil = new PerfilArtista();
        perfil.setUsuario(usuario);
        perfil.setNombre_artistico(nombre);
        em.persist(perfil);
        return perfil;
    }

    Obra obra(PerfilArtista perfil, String... precios) {
        Obra obra = Obra.builder().nombre_obra("Prueba").artista(perfil).en_venta(true).build();
        em.persist(obra);
        for (String precio : precios) {
            Variante variante = Variante.builder().obra(obra).tamanio(tamanio)
                    .precio_variante(new BigDecimal(precio)).stock_variante(3).build();
            em.persist(variante);
            obra.getVariantes().add(variante);
        }
        return obra;
    }

    @Test void categoriaSolaIncluyeObrasSinVariantes() throws Exception {
        mvc.perform(get("/api/obras").param("estiloId", categoria.getId().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        assertThat(service.buscarConFiltros(null, categoria.getId(), null, null))
                .containsExactlyInAnyOrder(dentro, sinVariantes, otroArtista);
    }

    @Test void categoriaYPrecioSinArtista() {
        assertThat(service.buscarConFiltros(null, categoria.getId(), new BigDecimal("100"), new BigDecimal("200.50")))
                .containsExactlyInAnyOrder(dentro, otroArtista);
    }

    @Test void combinaTodosLosFiltros() throws Exception {
        mvc.perform(get("/api/obras").param("estiloId", categoria.getId().toString())
                        .param("artistaId", artista.getId().toString())
                        .param("precioMin", "100").param("precioMax", "200.50"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(dentro.getId().intValue()));
    }

    @Test void categoriaInexistenteDevuelveListaVacia() throws Exception {
        mvc.perform(get("/api/obras").param("estiloId", "9223372036854775807"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test void categoriaConPrecioSinCoincidencias() {
        assertThat(service.buscarConFiltros(null, categoria.getId(), new BigDecimal("250"), null)).isEmpty();
    }

    @Test void sinFiltrosConservaObrasSinVariantes() {
        assertThat(service.buscarConFiltros(null, null, null, null))
                .containsExactlyInAnyOrder(dentro, extremos, sinVariantes, otroArtista);
    }

    @Test void conservaFiltroPorArtista() {
        assertThat(service.buscarConFiltros(artista.getId(), null, null, null))
                .containsExactlyInAnyOrder(dentro, extremos, sinVariantes);
    }

    @Test void combinaArtistaYRangoSinDuplicadosNiMezclarVariantes() {
        assertThat(service.buscarConFiltros(artista.getId(), null, new BigDecimal("100.25"), new BigDecimal("200.50")))
                .containsExactly(dentro);
    }

    @Test void limiteMinimoIndependiente() {
        assertThat(service.buscarConFiltros(null, null, new BigDecimal("200.50"), null))
                .containsExactlyInAnyOrder(dentro, extremos);
    }

    @Test void limiteMaximoIndependiente() {
        assertThat(service.buscarConFiltros(null, null, null, new BigDecimal("50"))).containsExactly(extremos);
    }

    @Test void limitesIgualesSonInclusivos() {
        assertThat(service.buscarConFiltros(null, null, new BigDecimal("100.25"), new BigDecimal("100.25")))
                .containsExactly(dentro);
    }

    @Test void sinCoincidenciasDevuelveListaVacia() throws Exception {
        mvc.perform(get("/api/obras").param("precioMax", "0"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test void parametrosHttpLleganHastaLaConsulta() throws Exception {
        mvc.perform(get("/api/obras").param("artistaId", artista.getId().toString())
                        .param("precioMin", "100.25").param("precioMax", "200.50"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(dentro.getId().intValue()));
    }

    @Test void rechazaPreciosNegativosRangoInvertidoYTexto() throws Exception {
        mvc.perform(get("/api/obras").param("precioMin", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/obras").param("precioMax", "-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/obras").param("precioMin", "200").param("precioMax", "100"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/obras").param("precioMin", "texto")).andExpect(status().isBadRequest());
    }
}
