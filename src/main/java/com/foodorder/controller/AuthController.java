package com.foodorder.controller;

import com.foodorder.dto.Dtos.*;
import com.foodorder.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest r) {
        return authService.register(r);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) {
        return authService.login(r);
    }
}
