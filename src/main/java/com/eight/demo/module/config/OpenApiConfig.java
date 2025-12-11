package com.eight.demo.module.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Playground Restful APIs")
                        .description("Playground Module")
                        .version("V2")
                        .contact(new Contact().name("yoooche").url("https://github.com/yoooche/playground")));
    }
}
