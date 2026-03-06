package org.example.revplaycatalogservice.client;

import org.example.revplaycatalogservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// This tells Feign to look inside the Eureka phonebook for "REVPLAY-USER-SERVICE"
@FeignClient(name = "REVPLAY-USER-SERVICE", fallback = UserClientFallback.class)
public interface UserClient {

    // This must match the exact URL path in your User Service!
    @GetMapping("/api/users/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);

}