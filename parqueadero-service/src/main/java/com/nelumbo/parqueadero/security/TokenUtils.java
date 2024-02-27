package com.nelumbo.parqueadero.security;

import com.nelumbo.parqueadero.entities.Token;
import com.nelumbo.parqueadero.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenUtils {

    @Value("${ACCESS_TOKEN_SECRET}")
    private String accessTokenSecret;

    private static final Long ACCESS_TOKEN_VALIDITY_SECONDS = 21_600L;

    private final TokenRepository tokenRepository;

    public String createToken(String nombre, String email, String rol, Long id){
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS*1_000;
        Date expirationDate = new Date(System.currentTimeMillis()+expirationTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("nombre", nombre);
        extra.put("rol", rol);
        extra.put("id", id);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expirationDate)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(accessTokenSecret.getBytes()))
                .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String email= claims.getSubject();
            Collection<SimpleGrantedAuthority> authorities=
                    Arrays.stream(claims.get("rol").toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(email, null, authorities);

        }catch (JwtException e) {
            Token jwt = tokenRepository.findByTokenJwt(token).orElseThrow();
            jwt.setExpired(true);
            jwt.setRevoked(true);
            tokenRepository.save(jwt);
            return null;
        }


    }

    public String getCorreo(String token){
        try {
            Claims claims  = Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return  claims.getSubject();

        }catch (JwtException e){
            return  null;
        }
    }

    public Long getUsuarioAutenticadoId(String token){
        try {
            Claims claims  = Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("id", Long.class);

        }catch (JwtException e){
            return  null;
        }
    }

    public String getUsuarioAutenticadoRol(String token){
        try {
            Claims claims  = Jwts.parserBuilder()
                    .setSigningKey(accessTokenSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("rol", String.class);

        }catch (JwtException e){
            return  null;
        }
    }
}
