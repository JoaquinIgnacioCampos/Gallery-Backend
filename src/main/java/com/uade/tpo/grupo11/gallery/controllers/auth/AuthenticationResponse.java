package com.uade.tpo.grupo11.gallery.controllers.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthenticationResponse(
        @JsonProperty("access_token") String access_token
) {}
