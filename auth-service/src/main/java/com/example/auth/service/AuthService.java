package com.example.auth.service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.AppUser;
import com.example.auth.repo.UserRepository;
import com.example.common.jwt.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${security.jwt.secret}") String secret,
                       @Value("${security.jwt.issuer}") String issuer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = new JwtService(secret, issuer);
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        AppUser u = new AppUser();
        u.setEmail(req.email().toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole("CUSTOMER");
        AppUser saved = userRepository.save(u);

        String token = jwtService.generateToken(saved.getId(), saved.getEmail(), saved.getRole(), 3600);
        return new AuthResponse(saved.getId(), saved.getEmail(), saved.getRole(), token);
    }

    public AuthResponse login(LoginRequest req) {
        AppUser u = userRepository.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtService.generateToken(u.getId(), u.getEmail(), u.getRole(), 3600);
        return new AuthResponse(u.getId(), u.getEmail(), u.getRole(), token);
    }
}
