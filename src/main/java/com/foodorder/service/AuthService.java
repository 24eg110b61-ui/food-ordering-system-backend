package com.foodorder.service;

import com.foodorder.dto.Dtos.*;
import com.foodorder.exception.ApiException;
import com.foodorder.model.Role;
import com.foodorder.model.User;
import com.foodorder.repository.UserRepository;
import com.foodorder.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (userRepo.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        User u = new User();
        u.setName(r.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(r.password()));
        u.setPhone(r.phone());
        u.setAddress(r.address());
        u.setRole(Role.CUSTOMER);
        return toResponse(userRepo.save(u));
    }

    public AuthResponse login(LoginRequest r) {
        User u = userRepo.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid email or password"));
        return toResponse(u);
    }

    private AuthResponse toResponse(User u) {
        String token = jwtUtil.generateToken(u.getEmail(), u.getRole().name());
        return new AuthResponse(token, u.getId(), u.getName(), u.getEmail(),
                u.getPhone(), u.getAddress(), u.getRole().name());
    }
}
