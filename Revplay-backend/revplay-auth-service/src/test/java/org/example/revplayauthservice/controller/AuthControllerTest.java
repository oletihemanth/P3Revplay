package org.example.revplayauthservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.revplayauthservice.dto.LoginRequest;
import org.example.revplayauthservice.dto.RegisterRequest;
import org.example.revplayauthservice.service.AuthService;
// 🚨 Keep these security imports to prevent ApplicationContext crashes!
import org.example.revplayauthservice.security.JwtAuthenticationFilter;
import org.example.revplayauthservice.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Security for tests
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // 🚨 Fake the security beans so the application context doesn't crash!
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtUtil jwtUtil;

    private RegisterRequest mockRegisterRequest;
    private LoginRequest mockLoginRequest;

    @BeforeEach
    void setUp() {
        mockRegisterRequest = new RegisterRequest();
        mockRegisterRequest.setName("Sagar");
        mockRegisterRequest.setEmail("sagar@gmail.com");
        mockRegisterRequest.setPassword("password123");

        mockLoginRequest = new LoginRequest();
        mockLoginRequest.setEmail("sagar@gmail.com");
        mockLoginRequest.setPassword("password123");
    }

    // --- TEST 1: REGISTER ENDPOINT ---
    @Test
    void register_ShouldReturn200() throws Exception {
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn("User registered successfully!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully!"));
    }

    // --- TEST 2: LOGIN ENDPOINT ---
    @Test
    void login_ShouldReturn200AndToken() throws Exception {
        Map<String, String> mockResponse = new HashMap<>();
        mockResponse.put("token", "fake-jwt-token");
        mockResponse.put("role", "USER");

        when(authService.loginUser(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // --- TEST 3: FORGOT PASSWORD ENDPOINT ---
    @Test
    void forgotPassword_ShouldReturn200() throws Exception {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("email", "sagar@gmail.com");
        requestMap.put("newPassword", "newPass");

        when(authService.resetPassword("sagar@gmail.com", "newPass")).thenReturn("Password successfully reset!");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password successfully reset!"));
    }
}