package com.shramikconnect.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply updated CORS policy
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(auth -> auth
                // 1. Public Endpoints
                .requestMatchers(
                    "/api/auth/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // 2. Allow Pre-flight OPTIONS requests for CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 3. Worker Protected Paths (Profile, Feed, Applications)
                // hasAnyAuthority matches the role strings exactly as they come from your DB/JWT
                .requestMatchers("/api/worker/**").hasAnyAuthority("WORKER", "ROLE_WORKER")
                .requestMatchers("/api/jobs/feed/**").hasAnyAuthority("WORKER", "ROLE_WORKER")
                .requestMatchers("/api/applications/apply/**").hasAnyAuthority("WORKER", "ROLE_WORKER")
                .requestMatchers("/api/applications/my-status").hasAnyAuthority("WORKER", "ROLE_WORKER")
                
                // 4. Admin/Organization Protected Paths
                .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                .requestMatchers("/api/organization/**").hasAnyAuthority("ORGANIZATION", "ROLE_ORGANIZATION")
                
                // 5. Shared Authenticated features
                .requestMatchers("/api/jobs/**").authenticated()
                .requestMatchers("/api/applications/**").authenticated()
                
                .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider())
            // JWT Filter must run before the default authentication filter to catch the Bearer token
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder()); 
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // CDAC Mumbai development preference
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); 
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Explicitly allow Authorization to solve the persistent 403 Forbidden
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}