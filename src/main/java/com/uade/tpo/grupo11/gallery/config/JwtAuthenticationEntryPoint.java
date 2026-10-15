package com.uade.tpo.grupo11.gallery.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Responde 401 cuando una ruta exige estar logueado y la peticion no trae un token valido.
// El 401 le indica al front que tiene que pedirle al usuario que vuelva a iniciar sesion.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Lo deja el JwtAuthenticationFilter cuando el token vino pero no sirve (vencido, alterado...).
    static final String MOTIVO_TOKEN_RECHAZADO = "motivoTokenRechazado";

    static final String SIN_SESION = "Tenés que iniciar sesión para continuar";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Object motivo = request.getAttribute(MOTIVO_TOKEN_RECHAZADO);
        RespuestaSeguridad.escribir(response, HttpServletResponse.SC_UNAUTHORIZED,
                motivo != null ? motivo.toString() : SIN_SESION);
    }
}
