package com.davivienda.surveyplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Permitir en todos los endpoints
                .allowedOrigins("http://localhost:3001")  // Tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")  // Permitir todos los headers incluido User-Id
                .allowCredentials(true)  // Permitir cookies/auth
                .maxAge(3600);  // Cache preflight por 1 hora
    }
}
