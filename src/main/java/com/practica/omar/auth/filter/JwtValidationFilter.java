package com.practica.omar.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.*;

import static com.practica.omar.auth.TokenJwtConfig.*;

// lo utilizamos para la verificacion y validacion del token JWT
public class JwtValidationFilter extends BasicAuthenticationFilter {


    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(HEADER_AUTHORIZATION);

       if(header == null || !header.startsWith(PREFIX_TOKEN)){
           // Si no, continuar con el siguiente filtro en la cadena
           chain.doFilter(request, response);
           return;
       }
       String token = header.replace(PREFIX_TOKEN,"");
       System.out.print(token);
       byte[] tokenDecodeBytes = Base64.getDecoder().decode(token);
       String tokenDecode = new String(tokenDecodeBytes);

        System.out.println(tokenDecode);

        String[] tokenArr = tokenDecode.split(":");
        System.out.println(tokenArr.length);
       String secret = tokenArr[0];
       String username = tokenArr[1];

       if(SECRET_KEY.equals(secret)){
           List<GrantedAuthority> authorities = new ArrayList<>();
           authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

           UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
                   authorities);

           SecurityContextHolder.getContext().setAuthentication(authentication);
           chain.doFilter(request, response);
       }else {
           //es una interfaz que define una colección de pares clave-valor
           Map<String, String> body = new HashMap<>();
           body.put("message","el token es invalido");

           response.getWriter().write(new ObjectMapper().writeValueAsString(body));
           response.setStatus(403);
           response.setContentType("application/json");

       }
    }


}
