package com.revplayplaylistservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return requestTemplate -> {
            // Grab the current incoming web request
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                // Extract the Authorization header (the JWT token)
                String token = attributes.getRequest().getHeader("Authorization");

                if (token != null) {
                    // Attach it to the outgoing Walkie-Talkie request!
                    requestTemplate.header("Authorization", token);
                }
            }
        };
    }
}