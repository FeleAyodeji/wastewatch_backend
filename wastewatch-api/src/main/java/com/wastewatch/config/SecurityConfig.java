package com.wastewatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize on controllers
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwksUri;

    private final AppProperties appProperties;

    public SecurityConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Stateless API — no sessions, no cookies
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable CSRF — not needed for stateless JWT APIs
                .csrf(AbstractHttpConfigurer::disable)

                // CORS — only allow requests from the frontend domain
                .cors(c -> c.configurationSource(corsConfigurationSource()))

                // Route-level access rules
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints — no JWT needed
                        .requestMatchers(HttpMethod.GET,
                                "/reports/nearby",
                                "/authority-scores",
                                "/leaderboard/**",
                                "/actuator/health"
                        ).permitAll()

                        // Coming Soon waitlist endpoints — open to anyone
                        .requestMatchers(HttpMethod.POST,
                                "/store/notify",
                                "/ussd/notify"
                        ).permitAll()

                        // Everything else requires a valid Supabase JWT
                        .anyRequest().authenticated()
                )

                // JWT validation using Supabase public keys
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                        .authenticationEntryPoint((request, response, ex) -> {
                            // Return clean JSON error instead of default HTML 401
                            response.setContentType("application/json");
                            response.setStatus(401);
                            response.getWriter().write(
                                    "{\"success\":false,\"error\":{\"code\":\"UNAUTHORIZED\"," +
                                            "\"message\":\"Missing or invalid token.\"}}"
                            );
                        })
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Fetches Supabase public keys once and caches them
        // Validates: signature, expiry, issuer automatically
        return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(appProperties.getAllowedOrigins());
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}