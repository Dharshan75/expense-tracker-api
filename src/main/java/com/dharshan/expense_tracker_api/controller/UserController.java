package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.LoginRequest;
import com.dharshan.expense_tracker_api.dto.RegisterRequest;
import com.dharshan.expense_tracker_api.dto.UserResponse;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.service.UserService;
import com.dharshan.expense_tracker_api.service.JwtService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService,
                          JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // ===============================
    // 🔹 REGISTER
    // ===============================
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request){

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail().toLowerCase()) // ✅ FIX: lowercase
                .password(request.getPassword())
                .build();

        User savedUser = userService.registerUser(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    // ===============================
    // 🔹 LOGIN (Returns JWT)
    // ===============================
    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {

        User user = userService.loginUser(
                request.getEmail().toLowerCase(), // ✅ FIX
                request.getPassword()
        );

        String token = jwtService.generateToken(user.getEmail());

        return Map.of("token", token);
    }

    // ===============================
    // 🔐 PROTECTED ROUTE
    // ===============================
    @GetMapping("/profile")
    public UserResponse profile() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userService.getUserByEmail(email);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}