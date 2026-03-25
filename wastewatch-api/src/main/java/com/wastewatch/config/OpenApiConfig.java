package com.wastewatch.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wastewatchOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WasteWatch API")
                        .description("""
                                Nigeria's civic waste reporting platform.
                                
                                **Authentication:** All protected endpoints require a 
                                Supabase JWT passed as a Bearer token in the 
                                Authorization header.
                                
                                **Base URL:** /api/v1
                                
                                **How to get a token:**
                                POST https://yourproject.supabase.co/auth/v1/token?grant_type=password
                                with your email and password.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WasteWatch Team")
                                .email("dev@wastewatch.ng")))
                // Adds the Authorize button to Swagger UI
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your Supabase JWT here")));
    }
}