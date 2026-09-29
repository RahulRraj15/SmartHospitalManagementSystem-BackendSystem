package com.shms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI smartHospitalOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Smart Hospital Management System API")
                        .version("1.0.0")
                        .description(
                                "REST API for managing hospital operations "
                                + "including users, departments, doctors, "
                                + "patients, appointments, medicines, "
                                + "prescriptions, billing and medical reports."
                        )
                        .contact(new Contact()
                                .name("Smart Hospital Management System")
                        )
                );
    }
}