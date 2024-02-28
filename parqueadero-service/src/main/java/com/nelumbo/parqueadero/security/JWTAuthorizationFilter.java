package com.nelumbo.parqueadero.security;

import com.nelumbo.parqueadero.entities.Token;
import com.nelumbo.parqueadero.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTAuthorizationFilter  extends OncePerRequestFilter {
    private final TokenUtils tokenUtils;
    private final TokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if(bearerToken!= null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.replace("Bearer", "").trim();
            Optional<Token> tokenJwt = tokenRepository.findByTokenJwt(token);
            var isTokenValid = tokenJwt
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if(Boolean.TRUE.equals(isTokenValid)) {
                UsernamePasswordAuthenticationToken usernamePAT = tokenUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(usernamePAT);
            }else {
                tokenJwt.ifPresent(value -> tokenRepository.deleteById(value.getId()));
            }
        }

        filterChain.doFilter(request,response);
    }
}
