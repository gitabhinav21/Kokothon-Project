package com.volunteer.platform.services;

import com.volunteer.platform.dto.LoginRequest;
import com.volunteer.platform.dto.SignupRequest;
import com.volunteer.platform.models.User;
import com.volunteer.platform.repositories.UserRepository;
import com.volunteer.platform.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public Map<String, Object> signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .skills(new ArrayList<>())
                .pastVolunteering(new ArrayList<>())
                .impactScore(0)
                .build();

        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("token", token);
        return response;
    }

    public Map<String, Object> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found. Please sign up first."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        String token = jwtUtils.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userName", user.getName());
        return response;
    }
}
