package org.example.revplaycatalogservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Grab the current HTTP request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // Extract the JWT Bearer token
            String token = request.getHeader("Authorization");
            if (token != null) {
                // Attach the token to the Walkie-Talkie call!
                template.header("Authorization", token);
            }
        }
    }
}