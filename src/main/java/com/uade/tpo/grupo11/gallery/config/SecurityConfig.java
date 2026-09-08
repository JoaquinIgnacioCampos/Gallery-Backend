package com.uade.tpo.grupo11.gallery.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

// El portero de la API: que rutas son publicas, cuales piden login y cuales piden rol.
// Se evalua de arriba hacia abajo y gana la primera regla que coincide: el orden importa.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    // Define que rutas son publicas, cuales exigen estar logueado y cuales exigen un rol.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF se apaga porque no usamos cookies de sesion: cada peticion
                // se autentica con su token, asi que ese ataque no aplica.
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // La documentacion de la API es publica: sin esto Swagger devuelve 401.
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // Registrarse es publico: todavia no hay usuario ni token.
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // El catalogo se puede mirar sin loguearse, pero solo mirar.
                        .requestMatchers(HttpMethod.GET,
                                "/api/obras/**", "/api/variantes/**", "/api/imagenes/**",
                                "/api/estilos/**", "/api/tamanios-lienzo/**", "/api/marcos/**",
                                "/api/artistas/**").permitAll()

                        // ADMINISTRACION DE CUENTAS: asignar permisos. Solo ADMIN.
                        .requestMatchers("/api/usuarios/*/rol").hasAuthority("ADMIN")

                        // CONFIGURACION DEL CATALOGO: estilos, tamanios y marcos son maestros
                        // del sistema, no de un artista puntual. Escribirlos es solo de ADMIN.
                        .requestMatchers(HttpMethod.POST,
                                "/api/estilos/**", "/api/tamanios-lienzo/**", "/api/marcos/**")
                                .hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/estilos/**", "/api/tamanios-lienzo/**", "/api/marcos/**")
                                .hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/estilos/**", "/api/tamanios-lienzo/**", "/api/marcos/**")
                                .hasAuthority("ADMIN")

                        // PUBLICAR Y GESTIONAR OBRAS: solo quien vende.
                        .requestMatchers(HttpMethod.POST,
                                "/api/obras/**", "/api/variantes/**", "/api/imagenes/**")
                                .hasAnyAuthority("ARTISTA", "ARTISTA_CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/obras/**", "/api/variantes/**", "/api/imagenes/**")
                                .hasAnyAuthority("ARTISTA", "ARTISTA_CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/variantes/**")
                                .hasAnyAuthority("ARTISTA", "ARTISTA_CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/obras/**", "/api/variantes/**", "/api/imagenes/**")
                                .hasAnyAuthority("ARTISTA", "ARTISTA_CLIENTE", "ADMIN")

                        // ENCARGOS: los pide quien compra; el estado lo mueve el artista.
                        // Que sea EL artista de ese encargo lo verifica el service, no esta regla.
                        .requestMatchers(HttpMethod.POST, "/api/encargos/**")
                                .hasAnyAuthority("CLIENTE", "ARTISTA_CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/encargos/**")
                                .hasAnyAuthority("ARTISTA", "ARTISTA_CLIENTE", "ADMIN")

                        // ADMINISTRACION: la lista completa de usuarios es informacion sensible.
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAuthority("ADMIN")

                        // COMPRAR: carrito y checkout son de quien compra.
                        .requestMatchers("/api/carritos/**", "/api/items-carrito/**", "/api/checkout/**")
                                .hasAnyAuthority("CLIENTE", "ARTISTA_CLIENTE", "ADMIN")

                        // Todo lo demas exige estar logueado, sin importar el rol.
                        .anyRequest().authenticated())
                // STATELESS: el servidor no guarda sesiones. Cada peticion se identifica
                // sola con su token, que es lo que permite escalar sin estado compartido.
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                // Nuestro filtro de JWT corre antes que el de usuario y contrasenia de Spring,
                // porque en esta API la identidad llega en el token y no en un formulario.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}