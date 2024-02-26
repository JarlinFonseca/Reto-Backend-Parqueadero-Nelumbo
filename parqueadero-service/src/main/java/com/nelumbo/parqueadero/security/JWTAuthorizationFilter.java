package com.nelumbo.parqueadero.security;

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

        if(bearerToken!= null && bearerToken.startsWith("Bearer ")){
           // String token = bearerToken.replace("Bearer", "");
            String token = bearerToken.split(" ")[1];
            var isTokenValid = tokenRepository.findByToken(token)
                    .map(t -> !t.isExpired() && !t.isRevoked() )
                    .orElse(false);
            if(isTokenValid) {
                UsernamePasswordAuthenticationToken usernamePAT = tokenUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(usernamePAT);
            }
        }

        filterChain.doFilter(request,response);
    }
}
