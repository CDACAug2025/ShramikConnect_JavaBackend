package com.shramikconnect.security;

import java.util.List;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 🛡️ 1. Global Security Defaults
				.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())

				// 🔓 2. Request Authorization Logic
				.authorizeHttpRequests(auth -> auth
						// Public Paths
						.requestMatchers("/api/auth/**", "/images/**", "/api/payments/**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// 👮 1. SPECIFIC ADMIN RULES (Must be first)
						// Allow Admin to access the shared worker order path for all-order visibility
						.requestMatchers(HttpMethod.GET, "/api/worker/orders/admin/all").hasRole("ADMIN")
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						// 👷 2. ROLE SPECIFIC ACCESS (Worker & Products)
						// Shared Product access
						.requestMatchers("/api/worker/products/**").hasAnyRole("WORKER", "ADMIN")

						// Specific Worker Order actions
						.requestMatchers("/api/worker/orders/create").hasRole("WORKER")
						.requestMatchers("/api/worker/orders/verify").hasRole("WORKER")
						.requestMatchers(HttpMethod.GET, "/api/worker/orders/my-orders").hasRole("WORKER")

						// 🚧 3. BROAD CATCH-ALLS (Must be after specific sub-paths)
						.requestMatchers("/api/worker/**", "/api/jobs/feed/**").hasRole("WORKER")
						.requestMatchers("/api/organization/**").hasRole("ORGANIZATION")
						.requestMatchers("/api/supervisor/**").hasRole("SUPERVISOR")


						// 🔔 Shared Authenticated Paths
						.requestMatchers("/api/notifications/**")
						.hasAnyRole("ADMIN", "WORKER", "ORGANIZATION", "SUPERVISOR")
						.requestMatchers("/api/jobs/**", "/api/applications/**", "/api/contracts/**", "/api/chat/**")
						.authenticated()

						.anyRequest().authenticated())

				// 🔑 3. Authentication Configuration
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
		config.setAllowCredentials(true);
		config.setExposedHeaders(List.of("Authorization"));
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}