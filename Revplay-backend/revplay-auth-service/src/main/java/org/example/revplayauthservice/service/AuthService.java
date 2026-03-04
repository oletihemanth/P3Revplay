package org.example.revplayauthservice.service;

import org.example.revplayauthservice.dto.LoginRequest;
import org.example.revplayauthservice.dto.RegisterRequest;
import org.example.revplayauthservice.entity.User;
import org.example.revplayauthservice.repository.UserRepository;
import org.example.revplayauthservice.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Standardizing Role
        String role = (request.getRole() != null && !request.getRole().isEmpty())
                ? request.getRole().toUpperCase() : "USER";
        user.setRole(role);

        userRepository.save(user);
        return "User registered successfully!";
    }

    public Map<String, String> loginUser(LoginRequest request) {
        // 1. Authenticate via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Fetch User Details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), "ROLE_" + user.getRole());

        // 4. Construct Response Map for Angular
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("name", user.getName());

        return response;
    }

    public String resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found."));

        // Hash the new password before saving it to the database
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password successfully reset!";
    }
}