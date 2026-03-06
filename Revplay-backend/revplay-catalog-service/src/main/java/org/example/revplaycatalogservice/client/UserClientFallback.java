package org.example.revplaycatalogservice.client;

import org.example.revplaycatalogservice.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDTO getUserByEmail(String email) {
        // This code ONLY runs if the User Service is dead!
        System.out.println(" CIRCUIT BREAKER TRIGGERED! User Service is down.");
        System.out.println(" Providing fallback data for: " + email);

        // Return a dummy UserDTO so the Catalog Service doesn't crash!
        // We leave the name 'null' so your SongService uses the email prefix instead.
        return new UserDTO(null, null, email, null);
    }
}