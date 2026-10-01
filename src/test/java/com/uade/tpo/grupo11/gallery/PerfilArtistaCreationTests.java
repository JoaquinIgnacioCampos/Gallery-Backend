package com.uade.tpo.grupo11.gallery;

import com.uade.tpo.grupo11.gallery.config.JwtService;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.entities.enums.Rol;
import com.uade.tpo.grupo11.gallery.repositories.PerfilArtistaRepository;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:perfil_tests;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.database=H2", "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "application.security.jwt.secretKey=0123456789012345678901234567890123456789012345678901234567890123",
        "application.security.jwt.expiration=86400000"
})
@Transactional
class PerfilArtistaCreationTests {
    private static final String URL = "/api/usuarios/me/perfil-artista";
    private static final String BODY = "{\"nombre_artistico\":\"Mi arte\",\"acepta_encargos\":true}";
    @Autowired WebApplicationContext context;
    @Autowired FilterChainProxy securityFilter;
    @Autowired UsuarioRepository usuarios;
    @Autowired PerfilArtistaRepository perfiles;
    @Autowired JwtService jwt;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilter).build();
    }

    private Usuario usuario(String nombre, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre_usuario(nombre);
        usuario.setEmail_usuario(nombre + "@example.com");
        usuario.setContrasenia_usuario("unused-in-jwt-tests");
        usuario.setRol_usuario(rol);
        return usuarios.saveAndFlush(usuario);
    }

    private String token(Usuario usuario) {
        return "Bearer " + jwt.generateToken(usuario);
    }

    @Test
    void clienteCreaSoloSuPerfilYCambiaSuRol() throws Exception {
        Usuario titular = usuario("titular", Rol.CLIENTE);
        Usuario otro = usuario("otro", Rol.CLIENTE);
        mvc.perform(post(URL).header("Authorization", token(titular))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/usuarios/" + titular.getId() + "/perfil-artista"));
        assertTrue(perfiles.findByUsuarioId(titular.getId()).isPresent());
        assertTrue(perfiles.findByUsuarioId(otro.getId()).isEmpty());
        assertEquals(Rol.ARTISTA_CLIENTE, usuarios.findById(titular.getId()).orElseThrow().getRol_usuario());
        assertEquals(Rol.CLIENTE, usuarios.findById(otro.getId()).orElseThrow().getRol_usuario());
    }

    @Test
    void adminEsRechazadoSinCrearPerfilNiCambiarRol() throws Exception {
        Usuario admin = usuario("admin", Rol.ADMIN);
        mvc.perform(post(URL).header("Authorization", token(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden())
                .andExpect(content().string("ya tiene permisos para hacer todo"));
        assertTrue(perfiles.findByUsuarioId(admin.getId()).isEmpty());
        assertEquals(Rol.ADMIN, usuarios.findById(admin.getId()).orElseThrow().getRol_usuario());
    }

    @Test
    void sinTokenNoCreaPerfil() throws Exception {
        long cantidad = perfiles.count();
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().is4xxClientError());
        assertEquals(cantidad, perfiles.count());
    }

    @Test
    void rutaAnteriorNoPermiteCrearPerfilParaOtraCuenta() throws Exception {
        Usuario titular = usuario("titular", Rol.CLIENTE);
        Usuario otro = usuario("otro", Rol.CLIENTE);
        mvc.perform(post("/api/usuarios/" + otro.getId() + "/perfil-artista")
                        .header("Authorization", token(titular))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isMethodNotAllowed());
        assertTrue(perfiles.findByUsuarioId(otro.getId()).isEmpty());
        assertEquals(Rol.CLIENTE, otro.getRol_usuario());
    }

    @Test
    void perfilDuplicadoEsRechazado() throws Exception {
        Usuario titular = usuario("titular", Rol.CLIENTE);
        String token = token(titular);
        mvc.perform(post(URL).header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated());
        long cantidad = perfiles.count();
        mvc.perform(post(URL).header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict());
        assertEquals(cantidad, perfiles.count());
    }

    @Test
    void nombreInvalidoNoCreaPerfilNiCambiaRol() throws Exception {
        Usuario titular = usuario("titular", Rol.CLIENTE);
        mvc.perform(post(URL).header("Authorization", token(titular))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nombre_artistico\":\" \"}"))
                .andExpect(status().isBadRequest());
        assertTrue(perfiles.findByUsuarioId(titular.getId()).isEmpty());
        assertEquals(Rol.CLIENTE, titular.getRol_usuario());
    }
}
