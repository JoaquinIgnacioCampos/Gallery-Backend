package com.uade.tpo.grupo11.gallery.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

// Fabrica y verifica los tokens JWT.
// El contenido no esta encriptado y cualquiera lo puede leer; lo que protege es la firma.
@Service
public class JwtService {

    // La clave con la que se firma. Vive en application.properties y NO se sube al repo:
    // quien la tenga puede fabricar tokens validos.
    @Value("${application.security.jwt.secretKey}")
    private String secretKey;

    // Cuanto dura el token, en milisegundos. En el proyecto son 24 horas.
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // Arma el token. Guarda adentro el email (subject), cuando se emitio y cuando vence,
    // y lo firma. Ojo: el ROL no va adentro del token; se lee de la base en cada peticion.
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    // Un token sirve si es de este usuario y todavia no vencio.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Saca el email que viaja adentro del token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Dice si el token ya vencio.
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Abre el token verificando la firma. Si fue alterado o firmado con otra clave,
    // esta linea lanza excepcion y el token se descarta.
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    // Arma la clave con la que se firma y se verifica el token.
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
