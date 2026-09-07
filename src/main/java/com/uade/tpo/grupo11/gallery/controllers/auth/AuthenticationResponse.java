package com.uade.tpo.grupo11.gallery.controllers.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

// La respuesta del login: solo el token, nada del usuario.
// @JsonProperty fuerza el nombre "access_token" en el JSON, que es como se llama por convencion.
public record AuthenticationResponse(
        @JsonProperty("access_token") String access_token
) {}
