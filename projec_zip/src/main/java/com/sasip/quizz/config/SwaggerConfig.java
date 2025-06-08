package com.sasip.quizz.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Quiz API")
                        .version("1.0")
                        .description("API documentation for the Quiz system")
                        .termsOfService("") 
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Support Team") 
                                .url("")
                                .email(""))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("MIT License") 
                                .url(""))); 
    }
}