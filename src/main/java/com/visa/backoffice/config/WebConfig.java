package com.visa.backoffice.config;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String frontendBaseUrl;

    public WebConfig(@Value("${app.frontend-base-url:http://localhost:4200}") String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        Set<String> allowedOrigins = new LinkedHashSet<>();
        allowedOrigins.add(cleanOrigin(frontendBaseUrl));
        allowedOrigins.add("http://localhost:4200");
        allowedOrigins.add("http://127.0.0.1:4200");
        allowedOrigins.add("http://localhost:8080");

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    private String cleanOrigin(String url) {
        if (url == null) {
            return "http://localhost:4200";
        }
        String value = url.trim();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}
