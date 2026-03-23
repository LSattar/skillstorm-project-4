package com.skillstorm.animalshelter.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import com.skillstorm.animalshelter.dtos.request.CreateApplicationRequest;
import com.skillstorm.animalshelter.dtos.response.AdoptionApplicationResponse;
import com.skillstorm.animalshelter.models.AdoptionApplication;
import com.skillstorm.animalshelter.services.AdoptionApplicationService;

@ExtendWith(MockitoExtension.class)
class AdopterApplicationsControllerTest {

    @Mock
    private AdoptionApplicationService service;

    private AdopterApplicationsController controller;
    private UUID userId;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        controller = new AdopterApplicationsController(service);
        userId = UUID.randomUUID();
        auth = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_ADOPTER")));
    }

    @Test
    void createReturnsUnauthorizedWhenAuthenticationMissing() {
        CreateApplicationRequest req = new CreateApplicationRequest();
        req.setAnimalId(UUID.randomUUID());

        assertThatThrownBy(() -> controller.create(null, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401");
    }

    @Test
    void listOwnReturns200ForAuthenticatedAdopter() {
        AdoptionApplication app = new AdoptionApplication();
        app.setId(UUID.randomUUID());
        app.setAdopterUserId(userId);
        app.setStatus("SUBMITTED");
        when(service.findByAdopterUserId(userId)).thenReturn(List.of(app));

        ResponseEntity<List<AdoptionApplicationResponse>> result = controller.listOwn(auth);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void createUsesPrincipalUserId() {
        CreateApplicationRequest req = new CreateApplicationRequest();
        req.setAnimalId(UUID.randomUUID());
        AdoptionApplication app = new AdoptionApplication();
        app.setId(UUID.randomUUID());
        app.setAnimalId(req.getAnimalId());
        app.setAdopterUserId(userId);
        app.setStatus("SUBMITTED");
        when(service.create(any(UUID.class), any(CreateApplicationRequest.class))).thenReturn(app);

        ResponseEntity<AdoptionApplicationResponse> result = controller.create(auth, req);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getAdopterUserId()).isEqualTo(userId);
    }
}
