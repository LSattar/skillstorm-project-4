package com.skillstorm.animalshelter.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.skillstorm.animalshelter.services.JwtService;
import com.skillstorm.animalshelter.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final long LINK_STATE_EXPIRATION_SECONDS = 300;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(req);
        UserMeResponse me = toUserMeResponse(user);
        List<String> roles = userService.getRoleNamesByUserId(user.getId());
        String token = jwtService.createToken(user.getId(), user.getUsername(), roles);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponse(token, me));
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
        List<String> roles = userService.getRoleNamesByUserId(user.getId());
        String token = jwtService.createToken(user.getId(), user.getUsername(), roles);
        return ResponseEntity.ok(new LoginResponse(token, me));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID)) {
            log.debug("GET /api/auth/me called without valid JWT");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = (UUID) authentication.getPrincipal();
        User user = userService.findByIdOrThrow(userId);
        return ResponseEntity.ok(toUserMeResponse(user));
    }

    @GetMapping("/oauth2/google-url")
    public ResponseEntity<Map<String, String>> oauth2GoogleUrl(HttpServletRequest request) {
        log.debug("GET /api/auth/oauth2/google-url requested");
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort())
                + (request.getContextPath() != null && !request.getContextPath().isEmpty() ? request.getContextPath() : "");
        String redirectUrl = baseUrl + "/oauth2/authorization/google";
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    /**
     * Returns the redirect URL to start the "Link Google" OAuth flow. Call with Bearer JWT.
     * Only STAFF and FOSTER roles may link Google; adopters sign in with Google directly.
     */
    @GetMapping("/link-google")
    public ResponseEntity<Map<String, String>> linkGoogle(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID)) {
            log.debug("GET /api/auth/link-google called without valid JWT");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = (UUID) authentication.getPrincipal();
        List<String> roles = userService.getRoleNamesByUserId(userId);
        boolean staffOrFoster = roles.stream().anyMatch(r -> "STAFF".equals(r) || "FOSTER".equals(r));
        if (!staffOrFoster) {
            log.warn("Adopter attempted link-google, userId={}", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String linkToken = jwtService.createLinkStateToken(userId, LINK_STATE_EXPIRATION_SECONDS);
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort())
                + (request.getContextPath() != null && !request.getContextPath().isEmpty() ? request.getContextPath() : "");
        String redirectUrl = baseUrl + "/oauth2/authorization/google?linkToken=" + linkToken;
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
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
