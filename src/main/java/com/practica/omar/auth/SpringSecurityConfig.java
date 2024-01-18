package com.practica.omar.auth;

import com.practica.omar.auth.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.practica.omar.auth.filter.JwtAuthenticationFilter;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean 
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean 
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // sirve para iniciar las reglas de las solicitudes HTTP
        return http.authorizeHttpRequests()
        // los metodos que estaran libres de la autentificacion
        .requestMatchers(HttpMethod.GET, "/users").permitAll()
        // todos los demas metodos necesitaran autentificacion
        .anyRequest().authenticated()
        .and()
        // agrega un filtro personalizado al flujo de procesamiento de spring
        // este filtro se encarga de manejar la authentificaion basada en token JWT
        .addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
        // desactivado la proteccion CSRF, se recomienda para microservicios
        .csrf(config -> config.disable())
        // con esto el usuario no deja su informacion en el servidor sino 
        // el token para permitir el ingreso a la aplicacion
        .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
    }
    

}
