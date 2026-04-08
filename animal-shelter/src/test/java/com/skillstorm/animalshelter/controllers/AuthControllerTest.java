package com.skillstorm.animalshelter.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;

import com.skillstorm.animalshelter.dtos.request.LoginRequest;
import com.skillstorm.animalshelter.dtos.request.RegisterRequest;
import com.skillstorm.animalshelter.models.User;
import com.skillstorm.animalshelter.services.JwtService;
import com.skillstorm.animalshelter.services.UserService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(userService, passwordEncoder, jwtService);
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("returns 201 and login response on success")
        void returns201OnSuccess() {
            RegisterRequest req = new RegisterRequest();
            req.setUsername("adopter1");
            req.setEmail("adopter@example.com");
            req.setPassword("password123");
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername(req.getUsername());
            user.setEmail(req.getEmail());
            user.setIsEnabled(true);
            when(userService.register(any(RegisterRequest.class))).thenReturn(user);
            when(userService.getRoleNamesByUserId(user.getId())).thenReturn(List.of("ADOPTER"));
            when(jwtService.createToken(user.getId(), user.getUsername(), List.of("ADOPTER"))).thenReturn("test-jwt-token");

            ResponseEntity<com.skillstorm.animalshelter.dtos.response.LoginResponse> result = controller.register(req);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getAccessToken()).isNotNull();
            assertThat(result.getBody().getUser().getUsername()).isEqualTo("adopter1");
            assertThat(result.getBody().getUser().getEmail()).isEqualTo("adopter@example.com");
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("returns 200 and token when credentials valid")
        void returns200WhenValid() {
            LoginRequest req = new LoginRequest();
            req.setUsername("adopter1");
            req.setPassword("password123");
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername("adopter1");
            user.setEmail("adopter@example.com");
            user.setIsEnabled(true);
            user.setPasswordHash("hashed");
            when(userService.findByUsername("adopter1")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
            when(userService.getRoleNamesByUserId(user.getId())).thenReturn(List.of("ADOPTER"));
            when(jwtService.createToken(user.getId(), user.getUsername(), List.of("ADOPTER"))).thenReturn("test-jwt-token");

            ResponseEntity<com.skillstorm.animalshelter.dtos.response.LoginResponse> result = controller.login(req);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody().getAccessToken()).isNotNull();
            assertThat(result.getBody().getUser().getUsername()).isEqualTo("adopter1");
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            LoginRequest req = new LoginRequest();
            req.setUsername("nobody");
            req.setPassword("password123");
            when(userService.findByUsername("nobody")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.login(req))
                    .isInstanceOf(com.skillstorm.animalshelter.exceptions.ResourceNotFoundException.class)
                    .hasMessageContaining("Invalid username or password");
        }
    }

    @Nested
    @DisplayName("GET /api/auth/oauth2/google-url")
    class OAuthGoogleUrl {

        @Test
        @DisplayName("returns 200 with oauth redirect url")
        void returnsOauthRedirectUrl() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setScheme("http");
            request.setServerName("localhost");
            request.setServerPort(8080);
            request.setContextPath("");

            ResponseEntity<java.util.Map<String, String>> result = controller.oauth2GoogleUrl(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().get("redirectUrl")).isEqualTo("http://localhost:8080/oauth2/authorization/google");
        }
    }
}
