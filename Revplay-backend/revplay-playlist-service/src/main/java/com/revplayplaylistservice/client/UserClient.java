package com.revplayplaylistservice.client;

import com.revplayplaylistservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "REVPLAY-USER-SERVICE")
public interface UserClient {
    @GetMapping("/api/users/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);
}