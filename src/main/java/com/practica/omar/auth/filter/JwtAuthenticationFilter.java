package com.practica.omar.auth.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practica.omar.model.entities.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.practica.omar.auth.TokenJwtConfig.*;

//  el usernamePasswordAuthenticationFilter  es para autentificar un usuario con contraseña y nomnbre
public class JwtAuthenticationFilter  extends UsernamePasswordAuthenticationFilter{

    // lo utilizamos para inyectar una variable authenticationManager
    private AuthenticationManager autentificationManager;
    

public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
    this.autentificationManager = authenticationManager;
}

// este metodo su tarea principal es autenticar al usuario por el usuario y contrasela que 
// que se envia en las solicitudes HTTP
@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
    User user = null;
    String username = null;
    String password = null;


    try{
        // intenta leer el contenido de las solicitudes, es como RequestBody
        user = new ObjectMapper().readValue(request.getInputStream(), User.class);
        username = user.getUsername();
        password = user.getPassword();

        // nos permite dar mensaje en la consola
        logger.info("Username desde request InputStream (raw) " + username);
        logger.info("Password desde request InputStream (raw) " + password);
   // cuando hay problemas al leer el flujo de entrada, por ejemplo que
   // el internet se seva y no termina de leer el contenido
    } catch(StreamReadException e){
        e.printStackTrace();
        // cuando hay problemas al convertir en el formato esperado
    } catch(DatabindException e) {
        e.printStackTrace();
        // cuando hay problemas al leer o escribir en el flujo 
        // de entreda y salida
    } catch(IOException e){
        e.printStackTrace();
    }
 
    // se crea el token de authentification
    // se crea un objeto de UsernamePasswordAurthenticationToken 
    // con el nombre de usuario y la contraseña obtenida
    UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(username, password);


    // aqui se procede a la autentificacion , dando como resultado la autentificacion exitosa y la verificacion de credenciales 
    // roles
    return autentificationManager.authenticate(authToken);

}

// cuando sale todo bien
@Override
protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authResult) throws IOException, ServletException {

            // extraer el nombre del usuario del objeto authentification
            String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
 
//            // la cadena ayuda para como base para generar el token
//            String originalInput = SECRET_KEY+":"+ username;
//
//            //crea un token codificado en base 64, pero esto no es recomendable para produccion
//            String token = Base64.getEncoder().encodeToString(originalInput.getBytes());


    // creamos el token con el usuario, la clave secreta, el tipo de inicio y expiracoin
    String token = Jwts.builder()
            .setSubject(username)
            .signWith(SECRET_KEY)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .compact();
            // agrega el token como encabezado de authorization de la respuesta HTTP
            response.addHeader(HEADER_AUTHORIZATION,PREFIX_TOKEN+ token);

            // crea un mapa paera contruir el cuerpo de la respuesta de json
            // ademas, de informacion sobre la authentification
            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("message", String.format("Hola %s, has iniciado sesion con exito",username));
            body.put("username", username);

            //escribir el cuerpo de respuesta en la salida de la respuesta HTTP
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            // Configurar el estado y el tipo de contenido de la respuesta HTTP
            response.setStatus(200);
            response.setContentType("application/json");
}

// nos da cuando la authentificac ion es fallida

@Override
protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException failed) throws IOException, ServletException {
     // Crear un cuerpo de respuesta JSON para un intento fallido de autenticación
            Map<String, Object> body = new HashMap<>();
    body.put("message", "Error en la authenticacion username o password incorrecta!");
    body.put("error", failed.getMessage());

    response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    response.setStatus(401);
    response.setContentType("application/json");

}


}
