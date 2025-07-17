package com.castor.youtube.service;

import com.castor.youtube.entity.User;
import com.castor.youtube.repository.UserRepository;
import com.castor.youtube.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String register(AuthRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRoles(request.getRoles() != null && !request.getRoles().isEmpty()
                ? request.getRoles()
                : Set.of("ROLE_USER"));

        userRepository.save(user);

        return "Usuario registrado exitosamente.";
    }

    public String login(AuthRequest request) {
        User user = null;
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user = userRepository.findByEmail(request.getEmail()).orElse(null);
        }
        if (user == null && request.getUsername() != null && !request.getUsername().isEmpty()) {
            user = userRepository.findByUsername(request.getUsername()).orElse(null);
        }
        if (user == null) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }
        return jwtUtil.generateToken(user.getEmail());
    }
}