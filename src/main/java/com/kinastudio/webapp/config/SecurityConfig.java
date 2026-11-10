package com.kinastudio.webapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

/**
 * Configuración provisoria de seguridad.
 * Deja abierto /acceso/** y exige autenticación para el resto.
 * Cuando exista el login (RF.2), el registro de esteticistas pasa a exigir rol de administrador.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable) //NOSONAR provisorio hasta RF.2: se activa CSRF con el login por sesión
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/acceso/**").permitAll() //esto se cambia despues
                .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}