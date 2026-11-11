package com.kinastudio.webapp.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Permite que el front-end (otro origen, por ejemplo http://localhost:5173)
 * llame a esta API desde el navegador, enviando la cookie de sesión.
 * Los orígenes permitidos se configuran con la propiedad kina.cors.origenes
 * (lista separada por comas). Por defecto: el servidor de desarrollo de Vite.
 */
@Configuration
public class ConfiguracionCors {

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${kina.cors.origenes:http://localhost:5173}") List<String> origenes) {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOrigins(origenes);
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("Content-Type", "X-CSRF-TOKEN"));
        // Necesario para que el navegador envíe y reciba la cookie de sesión
        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }
}
