package com.shramikconnect.security; // <--- CHANGE THIS LINE TO MATCH YOUR FOLDER

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF
            .authorizeHttpRequests(auth -> auth
                // Allow Swagger UI
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // Allow Admin APIs
                .requestMatchers("/api/admin/**").permitAll()
                
                // Allow Login/Auth APIs (since I see you have Auth files)
                .requestMatchers("/api/auth/**").permitAll()
                
                // Block everything else
                .anyRequest().authenticated()
            );

        return http.build();
    }
}