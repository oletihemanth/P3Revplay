package com.revplayplaylistservice.exception;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignStatusException(FeignException e) {
        System.out.println(" Playlist Service Walkie-Talkie Failed: Target service is down.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"error\": \"An internal service (like Catalog or User) is currently down. Please try again later.\"}");
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<String> handleCircuitBreakerOpen(CallNotPermittedException e) {
        System.out.println(" Playlist Circuit Breaker OPEN: Blocking traffic to protect the system.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"error\": \"System is currently under heavy load. Traffic paused. Please try again in 10 seconds.\"}");
    }
}