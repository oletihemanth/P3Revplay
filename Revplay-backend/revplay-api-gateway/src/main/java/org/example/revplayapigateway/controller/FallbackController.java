package org.example.revplayapigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    //  FIX: Changed from @GetMapping to @RequestMapping to catch POST, PUT, DELETE, etc.
    @RequestMapping("/fallback")
    public ResponseEntity<String> globalFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"message\": \"The requested service is currently down or waking up. Please try again in a few seconds!\"}");
    }
}