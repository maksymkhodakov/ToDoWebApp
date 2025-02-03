package com.example.todowebapp.config;

import com.example.todowebapp.security.CustomPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSecurityConfig {

    @Bean
    public CustomPermissionEvaluator customPermissionEvaluator() {
        return new CustomPermissionEvaluator();
    }

}
