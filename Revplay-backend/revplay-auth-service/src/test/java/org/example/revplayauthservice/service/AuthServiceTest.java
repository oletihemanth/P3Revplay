package org.example.revplayauthservice.service;

import org.example.revplayauthservice.dto.LoginRequest;
import org.example.revplayauthservice.dto.RegisterRequest;
import org.example.revplayauthservice.entity.User;
import org.example.revplayauthservice.repository.UserRepository;
import org.example.revplayauthservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private RegisterRequest mockRegisterRequest;
    private LoginRequest mockLoginRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setName("Sagar");
        mockUser.setEmail("sagar@gmail.com");
        mockUser.setPassword("hashedPassword");
        mockUser.setRole("USER");

        mockRegisterRequest = new RegisterRequest();
        mockRegisterRequest.setName("Sagar");
        mockRegisterRequest.setEmail("sagar@gmail.com");
        mockRegisterRequest.setPassword("rawPassword");

        mockLoginRequest = new LoginRequest();
        mockLoginRequest.setEmail("sagar@gmail.com");
        mockLoginRequest.setPassword("rawPassword");
    }

    // --- TEST 1: REGISTRATION (SUCCESS) ---
    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail("sagar@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashedPassword");

        String result = authService.registerUser(mockRegisterRequest);

        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- TEST 2: REGISTRATION (EMAIL ALREADY EXISTS) ---
    @Test
    void registerUser_WhenEmailExists_ShouldThrowException() {
        when(userRepository.existsByEmail("sagar@gmail.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(mockRegisterRequest);
        });

        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // --- TEST 3: LOGIN (SUCCESS) ---
    @Test
    void loginUser_Success() {
        // Mock the AuthenticationManager
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByEmail("sagar@gmail.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken("sagar@gmail.com", "ROLE_USER")).thenReturn("fake-jwt-token");

        Map<String, String> response = authService.loginUser(mockLoginRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.get("token"));
        assertEquals("USER", response.get("role"));
        assertEquals("Sagar", response.get("name"));
    }

    // --- TEST 4: FORGOT PASSWORD (SUCCESS) ---
    @Test
    void resetPassword_Success() {
        when(userRepository.findByEmail("sagar@gmail.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");

        String result = authService.resetPassword("sagar@gmail.com", "newPassword123");

        assertEquals("Password successfully reset!", result);
        verify(userRepository, times(1)).save(mockUser);
    }
}