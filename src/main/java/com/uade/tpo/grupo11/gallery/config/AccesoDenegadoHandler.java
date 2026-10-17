package com.uade.tpo.grupo11.gallery.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Responde 403 cuando el usuario SI esta logueado pero su rol no alcanza para esa ruta.
// A diferencia del 401, volver a iniciar sesion no lo soluciona.
@Component
public class AccesoDenegadoHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        RespuestaSeguridad.escribir(response, HttpServletResponse.SC_FORBIDDEN,
                "No tenés permiso para realizar esta acción");
    }
}
