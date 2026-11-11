package com.kinastudio.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Seguridad de la API: un único login con usuario y contraseña (sesión con cookie).
 *
 * - POST /acceso/login   inicia sesión (campos: nombreUsuario y password)
 * - POST /acceso/logout  cierra sesión
 * - GET  /acceso/csrf    entrega el token CSRF que el front envía en las peticiones que modifican datos
 * - GET  /acceso/yo      devuelve el usuario con sesión iniciada
 *
 * La protección CSRF está activa (es el comportamiento por defecto de Spring Security).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error", "/acceso/csrf").permitAll()
                .requestMatchers("/acceso/registroEsteticista").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginProcessingUrl("/acceso/login")
                .usernameParameter("nombreUsuario")
                .passwordParameter("password")
                .successHandler((request, response, authentication) ->
                    response.setStatus(HttpStatus.OK.value()))
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("Usuario o contraseña incorrectos.");
                })
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/acceso/logout")
                .logoutSuccessHandler((request, response, authentication) ->
                    response.setStatus(HttpStatus.OK.value())))
            // Sin sesión se responde 401 en lugar de redirigir a una página de login
            .exceptionHandling(errores -> errores
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}