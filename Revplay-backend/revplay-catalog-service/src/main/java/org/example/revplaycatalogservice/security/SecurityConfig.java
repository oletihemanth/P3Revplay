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
                        //  ADDED SWAGGER ENDPOINTS HERE: Allow API Gateway to fetch docs
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 1. Make images and audio public so HTML tags work!
                        .requestMatchers("/api/songs/play/**", "/api/songs/image/**").permitAll()

                        //  FIX: Punch a hole for ALL users to increment play counts!
                        // Because this is above the blanket rules below, it grants permission first.
                        .requestMatchers(HttpMethod.PUT, "/api/songs/*/increment-play").authenticated()

                        // 2. Restrict all other modifying endpoints to Artists only
                        .requestMatchers(HttpMethod.POST, "/api/songs", "/api/songs/**", "/api/albums", "/api/albums/**").hasAuthority("ROLE_ARTIST")
                        .requestMatchers(HttpMethod.PUT, "/api/songs", "/api/songs/**", "/api/albums", "/api/albums/**").hasAuthority("ROLE_ARTIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/songs", "/api/songs/**", "/api/albums", "/api/albums/**").hasAuthority("ROLE_ARTIST")

                        // 3. Normal users can view (GET) and interact with other endpoints
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}