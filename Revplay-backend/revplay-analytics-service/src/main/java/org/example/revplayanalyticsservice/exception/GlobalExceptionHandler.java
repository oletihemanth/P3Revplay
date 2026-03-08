package org.example.revplayanalyticsservice.exception;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Catches when the target service is physically offline or throws a 500 error
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignStatusException(FeignException e) {
        System.out.println(" Walkie-Talkie Failed: Target service is down or failing.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"error\": \"An internal service we depend on is currently down. Please try again later.\"}");
    }

    // 2. Catches when the Circuit Breaker is actively "OPEN" to prevent cascading failure
    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<String> handleCircuitBreakerOpen(CallNotPermittedException e) {
        System.out.println(" Circuit Breaker OPEN: Blocking traffic to protect the system.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("{\"error\": \"System is currently under heavy load or maintenance. Traffic paused. Please try again in 10 seconds.\"}");
    }
}