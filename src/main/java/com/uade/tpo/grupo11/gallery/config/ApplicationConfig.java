package com.uade.tpo.grupo11.gallery.config;

import com.uade.tpo.grupo11.gallery.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Las cuatro piezas que Spring Security necesita para poder autenticar.
// Cada @Bean es un objeto que Spring crea una vez y le inyecta a quien lo pida.
@Configuration
public class ApplicationConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Le dice a Spring Security como buscar un usuario. Nuestro "nombre de usuario" es el email.
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // QUIEN valida. Junta las dos piezas de arriba: busca al usuario por email
    // y compara la contrasenia recibida contra el hash guardado.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // El que coordina la autenticacion. Es el que llama el AuthenticationService al loguear.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // BCrypt: hashea la contrasenia al registrarse y la compara al loguearse.
    // Es de una sola via: del hash no se puede volver a la contrasenia original.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}