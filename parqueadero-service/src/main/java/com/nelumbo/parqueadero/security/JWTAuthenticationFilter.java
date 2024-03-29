package com.nelumbo.parqueadero.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nelumbo.parqueadero.dto.response.UsuarioLoginResponseDto;
import com.nelumbo.parqueadero.entities.Token;
import com.nelumbo.parqueadero.entities.Usuario;
import com.nelumbo.parqueadero.enums.TokenType;
import com.nelumbo.parqueadero.repositories.TokenRepository;
import com.nelumbo.parqueadero.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final TokenUtils tokenUtils;
    private final UsuarioRepository usuarioRepository;
    private final TokenRepository tokenRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        AuthCredentials authCredentials= new AuthCredentials();

        try {
            authCredentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
        }catch (IOException e){
            logger.error("Error al leer AuthCredentials desde el request.", e);
        }

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getPassword(),
                Collections.emptyList()
        );


        return getAuthenticationManager().authenticate(usernamePAT);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl userDetails= (UserDetailsImpl) authResult.getPrincipal();
        Object[] authorities = userDetails.getAuthorities().toArray();
        String token = tokenUtils.createToken(userDetails.getNombre(), userDetails.getUsername(),
                authorities[0].toString(), userDetails.getId());
        response.addHeader("Authorization", "Bearer "+token);

        Usuario usuario= usuarioRepository.findById(userDetails.getId()).orElseThrow();
        revokeAllUserTokens(usuario);
        saveTokenUser(usuario, token);



        Gson gson = new Gson();
        UsuarioLoginResponseDto usuarioLoginResponseDto= new UsuarioLoginResponseDto();
        usuarioLoginResponseDto.setMensaje("Usuario logueado exitosamente.");
        usuarioLoginResponseDto.setToken("Bearer "+token);
        String jsonObject = gson.toJson(usuarioLoginResponseDto);

        // Configura la respuesta con el contenido JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonObject);
        response.getWriter().flush();

        super.successfulAuthentication(request, response, chain, authResult);
    }


    private void saveTokenUser(Usuario usuario, String tokenJwt){
        var token = Token.builder()
                .usuario(usuario)
                .tokenJwt(tokenJwt)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Usuario usuario) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(usuario.getId());
        if(validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
