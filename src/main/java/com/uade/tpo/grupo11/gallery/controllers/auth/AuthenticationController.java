package com.uade.tpo.grupo11.gallery.controllers.auth;

import com.uade.tpo.grupo11.gallery.services.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Puerta de entrada al sistema. Es el unico controller que no exige token,
// porque justamente sirve para conseguirlo.
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService service;

    // POST y no GET aunque solo "consulte": las credenciales viajan en el cuerpo,
    // y un GET las dejaria escritas en la URL y en los logs del servidor.
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}