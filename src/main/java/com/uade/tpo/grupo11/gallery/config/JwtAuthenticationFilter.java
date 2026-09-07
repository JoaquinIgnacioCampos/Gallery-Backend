package com.uade.tpo.grupo11.gallery.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// FILTRO: se ejecuta ANTES de que la peticion llegue a cualquier controller.
// Su unico trabajo es mirar si viene un token valido y, si viene, dejar registrado
// quien es el usuario para el resto de la peticion.
// OncePerRequestFilter garantiza que corra una sola vez por peticion.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // El token viaja en el header Authorization con el formato: Bearer <token>
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Sin token el filtro NO bloquea: deja pasar la peticion sin autenticar.
            // Quien decide si esa ruta necesita permiso es el SecurityConfig.
            filterChain.doFilter(request, response);
            return;
        }

        // substring(7) saltea la palabra "Bearer " y deja solo el token.
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Se busca al usuario en la base. De aca salen sus permisos actuales,
            // asi que un cambio de rol tiene efecto en la peticion siguiente.
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Queda registrado quien es el usuario de esta peticion. Es lo que mira
                // despues el SecurityConfig para decidir si tiene permiso.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}