package com.mmcafe.upload_image_ia.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Upload Image IA API")
                .version("v1")
                .description("Endpoints de upload e processamento de imagens"));
    }
}