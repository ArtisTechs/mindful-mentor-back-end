package com.example.mindful_mentor.config;

import com.example.mindful_mentor.security.JwtAuthenticationFilter;
import com.example.mindful_mentor.service.UserDetailsServiceImpl;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and()  // Enable CORS
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/users/signup", "/api/users/login").permitAll() 
                .requestMatchers("/api/users/status").hasRole("COUNSELOR")
                .requestMatchers("/api/users/delete/**").hasRole("COUNSELOR")
                .requestMatchers("/api/moods/students-with-mood-today").hasRole("COUNSELOR")
                .requestMatchers("/api/appointments/status/**").hasRole("COUNSELOR")
                .requestMatchers("/chat/**").permitAll() 
                .anyRequest().authenticated()
            )
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow localhost and any subdomain of example.com
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "https://*.example.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        config.setExposedHeaders(Arrays.asList("Authorization"));

        // Allowing preflight requests
        config.setMaxAge(3600L); // Optional: Cache the CORS preflight response for 1 hour

        // Register CORS configuration for all endpoints
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
