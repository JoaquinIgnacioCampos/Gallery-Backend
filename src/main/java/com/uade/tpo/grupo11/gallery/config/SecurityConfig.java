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

// El portero de la API: define que rutas son publicas, cuales exigen estar logueado
// y cuales exigen un rol determinado. Se evalua de arriba hacia abajo y gana la
// primera regla que coincide, por eso el orden importa.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

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
                                "/api/estilos/**", "/api/tamanios-lienzo/**", "/api/marcos/**").permitAll()

                        // ADMINISTRACION DE CUENTAS: asignar permisos y dar de baja. Solo ADMIN.
                        .requestMatchers("/api/usuarios/*/rol").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAuthority("ADMIN")

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