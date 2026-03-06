package com.skillstorm.animalshelter.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.animalshelter.dtos.request.LoginRequest;
import com.skillstorm.animalshelter.dtos.request.RegisterRequest;
import com.skillstorm.animalshelter.dtos.response.LoginResponse;
import com.skillstorm.animalshelter.dtos.response.UserMeResponse;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(req);
        UserMeResponse me = toUserMeResponse(user);
        LoginResponse response = new LoginResponse("placeholder-token-" + user.getId(), me);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Invalid username or password"));
        if (!user.getIsEnabled()) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Account is disabled");
        }
        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new com.skillstorm.animalshelter.exceptions.ResourceNotFoundException("Invalid username or password");
        }
        UserMeResponse me = toUserMeResponse(user);
        LoginResponse response = new LoginResponse("placeholder-token-" + user.getId(), me);
        return ResponseEntity.ok(response);
    }

    private UserMeResponse toUserMeResponse(User user) {
        UserMeResponse me = new UserMeResponse();
        me.setId(user.getId());
        me.setUsername(user.getUsername());
        me.setEmail(user.getEmail());
        me.setDisplayName(user.getDisplayName());
        me.setIsEnabled(user.getIsEnabled());
        me.setRoles(userService.getRoleNamesByUserId(user.getId()));
        return me;
    }
}
