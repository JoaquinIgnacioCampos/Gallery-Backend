package com.uade.tpo.grupo11.gallery.services.auth;

import com.uade.tpo.grupo11.gallery.config.JwtService;
import com.uade.tpo.grupo11.gallery.controllers.auth.AuthenticationRequest;
import com.uade.tpo.grupo11.gallery.controllers.auth.AuthenticationResponse;
import com.uade.tpo.grupo11.gallery.controllers.usuario.UsuarioRequest;
import com.uade.tpo.grupo11.gallery.entities.Usuario;
import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import com.uade.tpo.grupo11.gallery.services.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

// Verifica las credenciales y, si son correctas, entrega un token.
@Service
public class AuthenticationService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    // Valida mail y contrasenia y, si estan bien, devuelve un token.
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // Aca se comprueba la contrasenia comparando contra el hash guardado.
        // Si no coincide lanza BadCredentialsException y el handler global responde 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail_usuario(), request.getContrasenia_usuario()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail_usuario())
                .orElseThrow();

        // Recien con las credenciales validadas se genera el token.
        var jwtToken = jwtService.generateToken(usuario);
        return new AuthenticationResponse(jwtToken);
    }

    // Registra al usuario (delega toda la validacion/creacion en UsuarioService,
    // que ya tiene los chequeos de duplicados y el hash de la contrasenia)
    // y, si sale bien, genera su token para que no tenga que loguearse aparte.
    public AuthenticationResponse register(UsuarioRequest usuarioRequest) {
        Usuario usuario = usuarioService.createUsuario(usuarioRequest);

        var jwtToken = jwtService.generateToken(usuario);
        return new AuthenticationResponse(jwtToken);
    }

}
