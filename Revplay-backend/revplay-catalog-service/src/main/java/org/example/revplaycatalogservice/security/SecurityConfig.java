package org.example.revplaycatalogservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Make images and audio public so HTML tags work!
                        .requestMatchers("/api/songs/play/**", "/api/songs/image/**").permitAll()

                        // 2. ONLY ARTISTS can Upload, Edit, and Delete! (Removed ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/songs/**", "/api/albums/**").hasRole("ARTIST")
                        .requestMatchers(HttpMethod.PUT, "/api/songs/**", "/api/albums/**").hasRole("ARTIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/songs/**", "/api/albums/**").hasRole("ARTIST")

                        // 3. Normal users can view (GET) and interact with other endpoints
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}