package com.nelumbo.parqueadero.security;

import com.nelumbo.parqueadero.entities.Token;
import com.nelumbo.parqueadero.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");

         final String jwt;
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            return;
        }
        jwt = authHeader.replace("Bearer", "").trim();
       // jwt =  authHeader.split(" ")[1];
        Token storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if(storedToken!=null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}
