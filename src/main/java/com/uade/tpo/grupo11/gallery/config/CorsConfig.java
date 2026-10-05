package com.uade.tpo.grupo11.gallery.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Permiso para que el frontend, que corre en otro puerto, pueda llamar a esta API.
// Sin esto el navegador bloquea las peticiones aunque el backend responda bien.
@Configuration
public class CorsConfig {

    // Declara desde que origenes, con que metodos y con que headers se acepta una peticion.
    // Solo habilitamos localhost en cualquier puerto: es el front nuestro en desarrollo.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Authorization tiene que estar permitido o el token nunca llega al backend.
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
