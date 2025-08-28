package com.file_share.service;

import com.file_share.config.JwtUtil;
import com.file_share.dto.AuthResponse;
import com.file_share.dto.LoginRequest;
import com.file_share.dto.RegisterRequest;
import com.file_share.entity.User;
import com.file_share.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if(!request.getPassword().equals(request.getRepeatedPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        String hashed = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getUsername(), request.getEmail(), hashed);
        userRepository.save(user);

        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(user.getEmail(), jwt);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(user.getEmail(), jwt);
    }
}
