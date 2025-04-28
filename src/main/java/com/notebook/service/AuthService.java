package com.notebook.service;

import com.notebook.dto.AuthRequest;
import com.notebook.dto.AuthResponse;
import com.notebook.model.User;
import com.notebook.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(AuthRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        // Save user
        user = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtService.generateToken(user);
        
        // Return response
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(user);
        
        // Return response
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .token(token)
                .build();
    }
}
